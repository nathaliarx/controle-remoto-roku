package com.nathalia.rokuremote.net

import android.content.Context
import android.net.wifi.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketTimeoutException

class SsdpDiscovery(private val appContext: Context) {

    private val multicastAddress = "239.255.255.250"
    private val multicastPort = 1900

    private val query = buildString {
        append("M-SEARCH * HTTP/1.1\r\n")
        append("HOST: $multicastAddress:$multicastPort\r\n")
        append("MAN: \"ssdp:discover\"\r\n")
        append("ST: roku:ecp\r\n")
        append("MX: 3\r\n")
        append("\r\n")
    }.toByteArray()

    suspend fun discover(timeoutMs: Int = 4000): List<String> = withContext(Dispatchers.IO) {
        val wifi = appContext.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val lock = wifi?.createMulticastLock("rokucontrol-ssdp")?.apply {
            setReferenceCounted(false)
            runCatching { acquire() }
        }
        try {
            scan(timeoutMs)
        } finally {
            runCatching { lock?.release() }
        }
    }

    private fun scan(timeoutMs: Int): List<String> {
        val hosts = LinkedHashSet<String>()
        DatagramSocket().use { socket ->
            socket.soTimeout = timeoutMs
            socket.broadcast = true
            val target = InetSocketAddress(InetAddress.getByName(multicastAddress), multicastPort)
            repeat(2) { runCatching { socket.send(DatagramPacket(query, query.size, target)) } }
            val buffer = ByteArray(2048)
            val deadline = System.currentTimeMillis() + timeoutMs
            while (System.currentTimeMillis() < deadline) {
                val packet = DatagramPacket(buffer, buffer.size)
                try {
                    socket.receive(packet)
                } catch (e: SocketTimeoutException) {
                    break
                }
                val response = String(packet.data, 0, packet.length)
                parseLocationHost(response)?.let { hosts.add(it) }
            }
        }
        return hosts.toList()
    }

    private fun parseLocationHost(response: String): String? {
        val line = response.lineSequence()
            .firstOrNull { it.startsWith("LOCATION:", ignoreCase = true) }
            ?: return null
        val url = line.substringAfter(":", "").trim()
        return Regex("http://([^:/]+)").find(url)?.groupValues?.getOrNull(1)
    }
}

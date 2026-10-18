package com.nathalia.rokuremote.net

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object WakeOnLan {

    fun buildMagicPacket(mac: String): ByteArray {
        val bytes = mac.split(':', '-')
            .map { it.toInt(16).toByte() }
        require(bytes.size == 6) { "MAC inválido: $mac" }
        val packet = ByteArray(6 + 16 * 6)
        for (i in 0 until 6) packet[i] = 0xFF.toByte()
        for (i in 1..16) System.arraycopy(bytes.toByteArray(), 0, packet, i * 6, 6)
        return packet
    }

    suspend fun send(mac: String, broadcast: String = "255.255.255.255", port: Int = 9): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val payload = buildMagicPacket(mac)
                DatagramSocket().use { socket ->
                    socket.broadcast = true
                    socket.send(
                        DatagramPacket(payload, payload.size, InetAddress.getByName(broadcast), port)
                    )
                }
            }
        }
}

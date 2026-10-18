package com.nathalia.rokuremote.data

import android.content.Context
import com.nathalia.rokuremote.model.RemoteKey
import com.nathalia.rokuremote.model.RokuApp
import com.nathalia.rokuremote.model.RokuDevice
import com.nathalia.rokuremote.model.TvChannel
import com.nathalia.rokuremote.net.EcpClient
import com.nathalia.rokuremote.net.SsdpDiscovery

class RokuRepository(
    appContext: Context,
    private val ecp: EcpClient = EcpClient(),
    private val ssdp: SsdpDiscovery = SsdpDiscovery(appContext)
) {
    private val prefs = appContext.getSharedPreferences("rokucontrol", Context.MODE_PRIVATE)

    var lastHost: String?
        get() = prefs.getString("last_host", null)
        set(value) { prefs.edit().putString("last_host", value).apply() }

    suspend fun discoverHosts(): List<String> = ssdp.discover()

    suspend fun connect(host: String): Result<RokuDevice> =
        ecp.deviceInfo(host, "http://$host:8060").onSuccess { lastHost = host }

    suspend fun sendKey(device: RokuDevice, key: RemoteKey): Result<Unit> =
        ecp.keypress(device.baseUrl, key)

    suspend fun sendText(device: RokuDevice, text: String): Result<Unit> =
        ecp.typeText(device.baseUrl, text)

    suspend fun launchApp(device: RokuDevice, appId: String): Result<Unit> =
        ecp.launchApp(device.baseUrl, appId)

    suspend fun listApps(device: RokuDevice): Result<List<RokuApp>> =
        ecp.apps(device.baseUrl)

    suspend fun activeAppId(device: RokuDevice): Result<String?> =
        ecp.activeAppId(device.baseUrl)

    suspend fun listTvChannels(device: RokuDevice): Result<List<TvChannel>> =
        ecp.tvChannels(device.baseUrl)

    fun iconUrl(device: RokuDevice, appId: String): String =
        ecp.iconUrl(device.baseUrl, appId)
}

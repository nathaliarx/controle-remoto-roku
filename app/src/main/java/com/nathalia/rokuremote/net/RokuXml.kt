package com.nathalia.rokuremote.net

import com.nathalia.rokuremote.model.RokuApp
import com.nathalia.rokuremote.model.RokuDevice
import com.nathalia.rokuremote.model.TvChannel

object RokuXml {

    private fun tag(xml: String, name: String): String? =
        Regex("<$name>(.*?)</$name>", RegexOption.DOT_MATCHES_ALL)
            .find(xml)?.groupValues?.getOrNull(1)?.trim()

    fun parseDeviceInfo(host: String, xml: String): RokuDevice = RokuDevice(
        host = host,
        friendlyName = tag(xml, "friendly-device-name") ?: tag(xml, "user-device-name").orEmpty(),
        modelName = tag(xml, "model-name").orEmpty(),
        serialNumber = tag(xml, "serial-number").orEmpty(),
        softwareVersion = tag(xml, "software-version").orEmpty(),
        isTv = tag(xml, "is-tv") == "true",
        supportsFindRemote = tag(xml, "supports-find-remote") == "true",
        powerMode = tag(xml, "power-mode").orEmpty(),
        networkName = tag(xml, "network-name").orEmpty()
    )

    fun parseApps(xml: String): List<RokuApp> =
        Regex("<app id=\"([^\"]+)\"(?:[^>]*?type=\"([^\"]*)\")?(?:[^>]*?version=\"([^\"]*)\")?[^>]*>(.*?)</app>")
            .findAll(xml)
            .map {
                RokuApp(
                    id = it.groupValues[1],
                    type = it.groupValues[2],
                    version = it.groupValues[3],
                    name = it.groupValues[4].trim()
                )
            }
            .toList()

    fun parseTvChannels(xml: String): List<TvChannel> =
        Regex("<channel>(.*?)</channel>", RegexOption.DOT_MATCHES_ALL)
            .findAll(xml)
            .map { m ->
                val block = m.groupValues[1]
                TvChannel(
                    number = tag(block, "number").orEmpty(),
                    name = tag(block, "name").orEmpty(),
                    type = tag(block, "type").orEmpty(),
                    hidden = tag(block, "user-hidden") == "true"
                )
            }
            .filter { it.number.isNotEmpty() }
            .toList()

    fun parseActiveAppId(xml: String): String? =
        Regex("<app id=\"([^\"]+)\"").find(xml)?.groupValues?.getOrNull(1)
}

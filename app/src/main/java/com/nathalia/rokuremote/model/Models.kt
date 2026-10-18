package com.nathalia.rokuremote.model

data class RokuDevice(
    val host: String,
    val port: Int = 8060,
    val friendlyName: String = "",
    val modelName: String = "",
    val serialNumber: String = "",
    val softwareVersion: String = "",
    val isTv: Boolean = false,
    val supportsFindRemote: Boolean = false,
    val powerMode: String = "",
    val networkName: String = ""
) {
    val baseUrl: String get() = "http://$host:$port"
}

data class RokuApp(
    val id: String,
    val name: String,
    val type: String,
    val version: String
) {
    val isSideloaded: Boolean get() = id == "dev"
    val isTvInput: Boolean get() = type == "tvin"
}

data class TvChannel(
    val number: String,
    val name: String,
    val type: String,
    val hidden: Boolean
)

sealed interface ConnectionState {
    data object Disconnected : ConnectionState
    data object Connecting : ConnectionState
    data class Connected(val device: RokuDevice) : ConnectionState
    data class Error(val message: String) : ConnectionState
}

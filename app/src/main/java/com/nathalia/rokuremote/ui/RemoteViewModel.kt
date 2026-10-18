package com.nathalia.rokuremote.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nathalia.rokuremote.data.RokuRepository
import com.nathalia.rokuremote.model.ConnectionState
import com.nathalia.rokuremote.model.RemoteKey
import com.nathalia.rokuremote.model.RokuApp
import com.nathalia.rokuremote.model.RokuDevice
import com.nathalia.rokuremote.model.TvChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val connection: ConnectionState = ConnectionState.Disconnected,
    val discovered: List<String> = emptyList(),
    val discovering: Boolean = false,
    val lastHost: String? = null,
    val apps: List<RokuApp> = emptyList(),
    val activeAppId: String? = null,
    val channels: List<TvChannel> = emptyList()
)

class RemoteViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = RokuRepository(app)

    private val _state = MutableStateFlow(UiState(lastHost = repo.lastHost))
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _toasts = Channel<String>(Channel.BUFFERED)
    val toasts = _toasts.receiveAsFlow()

    private val device: RokuDevice?
        get() = (_state.value.connection as? ConnectionState.Connected)?.device

    fun discover() {
        _state.update { it.copy(discovering = true) }
        viewModelScope.launch {
            val hosts = repo.discoverHosts()
            _state.update { it.copy(discovered = hosts, discovering = false) }
            when {
                hosts.size == 1 -> connect(hosts.first())
                hosts.isEmpty() -> _toasts.send("Nenhuma TV encontrada. Informe o IP para conectar.")
            }
        }
    }

    fun connect(host: String) {
        _state.update { it.copy(connection = ConnectionState.Connecting) }
        viewModelScope.launch {
            repo.connect(host).fold(
                onSuccess = { dev ->
                    _state.update { it.copy(connection = ConnectionState.Connected(dev), lastHost = host) }
                    refresh()
                },
                onFailure = {
                    _state.update { s -> s.copy(connection = ConnectionState.Error(host)) }
                    _toasts.send("Não foi possível conectar em $host. Verifique o Wi-Fi e o IP.")
                }
            )
        }
    }

    fun press(key: RemoteKey) {
        val dev = device ?: return
        viewModelScope.launch {
            repo.sendKey(dev, key).onFailure { fail(key.ecp) }
        }
    }

    fun launch(app: RokuApp) {
        val dev = device ?: return
        viewModelScope.launch {
            repo.launchApp(dev, app.id)
                .onSuccess { _state.update { it.copy(activeAppId = app.id) } }
                .onFailure { _toasts.send("Não abriu ${app.name}.") }
        }
    }

    fun sendText(text: String) {
        val dev = device ?: return
        viewModelScope.launch {
            repo.sendText(dev, text).onFailure { _toasts.send("Não enviou o texto.") }
        }
    }

    fun refresh() {
        val dev = device ?: return
        viewModelScope.launch {
            repo.listApps(dev).onSuccess { apps -> _state.update { it.copy(apps = apps) } }
            repo.activeAppId(dev).onSuccess { id -> _state.update { it.copy(activeAppId = id) } }
            if (dev.isTv) repo.listTvChannels(dev).onSuccess { c -> _state.update { it.copy(channels = c) } }
        }
    }

    fun disconnect() {
        _state.update { it.copy(connection = ConnectionState.Disconnected, apps = emptyList(), channels = emptyList()) }
    }

    fun iconUrl(appId: String): String? = device?.let { repo.iconUrl(it, appId) }

    private suspend fun fail(label: String) {
        _toasts.send("Comando $label não chegou à TV.")
    }
}

package com.nathalia.rokuremote.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.LaunchedEffect
import com.nathalia.rokuremote.model.ConnectionState
import com.nathalia.rokuremote.model.RemoteKey
import com.nathalia.rokuremote.ui.theme.Mint

private enum class Tab { REMOTE, APPS, TV, KEYBOARD }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RemoteApp(vm: RemoteViewModel) {
    val state by vm.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        vm.toasts.collect { snackbar.showSnackbar(it) }
    }

    val connected = state.connection as? ConnectionState.Connected
    if (connected == null) {
        Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
            ConnectScreen(state, vm::discover, vm::connect, Modifier.padding(padding))
        }
        return
    }

    var tab by remember { mutableStateOf(Tab.REMOTE) }
    var showInputs by remember { mutableStateOf(false) }
    val device = connected.device

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(device.friendlyName.ifEmpty { device.modelName }, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            androidx.compose.material3.Surface(Modifier.size(8.dp), CircleShape, color = Mint) {}
                            Text("conectado · ${device.host}", style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showInputs = true }) { Icon(Icons.Default.Input, "Entradas") }
                    IconButton(onClick = vm::refresh) { Icon(Icons.Default.Refresh, "Atualizar") }
                    IconButton(onClick = vm::disconnect) { Icon(Icons.Default.Logout, "Desconectar") }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavItem(tab == Tab.REMOTE, { tab = Tab.REMOTE }, Icons.Default.SettingsRemote, "Controle")
                NavItem(tab == Tab.APPS, { tab = Tab.APPS }, Icons.Default.Apps, "Apps")
                if (device.isTv) NavItem(tab == Tab.TV, { tab = Tab.TV }, Icons.Default.LiveTv, "TV")
                NavItem(tab == Tab.KEYBOARD, { tab = Tab.KEYBOARD }, Icons.Default.Keyboard, "Teclado")
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                Tab.REMOTE -> RemoteControls(onKey = vm::press)
                Tab.APPS -> AppsScreen(state.apps, state.activeAppId, { vm.iconUrl(it) }, vm::launch)
                Tab.TV -> TvScreen(state.channels)
                Tab.KEYBOARD -> KeyboardScreen(vm::sendText, vm::press)
            }
        }
    }

    if (showInputs) {
        ModalBottomSheet(onDismissRequest = { showInputs = false }, sheetState = rememberModalBottomSheetState()) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Entradas e canais", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InputChip("TV ao vivo") { vm.press(RemoteKey.INPUT_TUNER) }
                    InputChip("HDMI 1") { vm.press(RemoteKey.INPUT_HDMI1) }
                    InputChip("HDMI 2") { vm.press(RemoteKey.INPUT_HDMI2) }
                    InputChip("HDMI 3") { vm.press(RemoteKey.INPUT_HDMI3) }
                    InputChip("AV") { vm.press(RemoteKey.INPUT_AV1) }
                    InputChip("Canal +") { vm.press(RemoteKey.CHANNEL_UP) }
                    InputChip("Canal −") { vm.press(RemoteKey.CHANNEL_DOWN) }
                }
                Text(
                    "Ajustes da TV: abra pelo botão Início e navegue com o direcional — a Roku não expõe um atalho direto de Configurações pela rede.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InputChip(label: String, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick) { Text(label) }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.NavItem(
    selected: Boolean, onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, label) },
        label = { Text(label) }
    )
}

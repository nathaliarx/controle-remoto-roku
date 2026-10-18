package com.nathalia.rokuremote.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@Composable
fun ConnectScreen(
    state: UiState,
    onDiscover: () -> Unit,
    onConnect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var ip by remember { mutableStateOf(state.lastHost.orEmpty()) }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp).widthIn(max = 480.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(Modifier.size(72.dp), CircleShape, color = MaterialTheme.colorScheme.primary) {
            Row(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterVertically) {
                Icon(Icons.Default.Cast, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(34.dp))
            }
        }
        Text("RokuControl", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Conecte-se à sua Roku TV na mesma rede Wi-Fi.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(onClick = onDiscover, enabled = !state.discovering, modifier = Modifier.fillMaxWidth()) {
            if (state.discovering) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(Icons.Default.Wifi, null, modifier = Modifier.size(18.dp))
                Text("  Procurar na rede")
            }
        }

        state.lastHost?.let { last ->
            OutlinedButton(onClick = { onConnect(last) }, modifier = Modifier.fillMaxWidth()) {
                Text("Reconectar em $last")
            }
        }

        LazyColumn(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.discovered) { host ->
                Surface(
                    onClick = { onConnect(host) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text("Roku em $host")
                        Text("Conectar", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        OutlinedTextField(
            value = ip,
            onValueChange = { ip = it },
            label = { Text("Ou informe o IP da TV") },
            placeholder = { Text("192.168.0.2") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { if (ip.isNotBlank()) onConnect(ip.trim()) }, modifier = Modifier.fillMaxWidth()) {
            Text("Conectar")
        }
    }
}

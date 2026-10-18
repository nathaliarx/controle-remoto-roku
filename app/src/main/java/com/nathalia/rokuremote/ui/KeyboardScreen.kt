package com.nathalia.rokuremote.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.nathalia.rokuremote.R
import com.nathalia.rokuremote.model.RemoteKey

@Composable
fun KeyboardScreen(
    onSendText: (String) -> Unit,
    onKey: (RemoteKey) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(R.string.keyboard_hint)) },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { if (text.isNotEmpty()) { onSendText(text); text = "" } },
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.send)) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = { onKey(RemoteKey.BACKSPACE) }) { Text("⌫") }
            OutlinedButton(onClick = { onKey(RemoteKey.ENTER) }) { Text("Enter") }
            OutlinedButton(onClick = { onKey(RemoteKey.SEARCH) }) { Text("Busca/Voz") }
        }
    }
}

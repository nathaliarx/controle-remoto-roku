package com.nathalia.rokuremote.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.nathalia.rokuremote.model.RemoteKey

@Composable
fun RemoteControls(onKey: (RemoteKey) -> Unit, modifier: Modifier = Modifier) {
    val view = LocalView.current
    val tap: (RemoteKey) -> Unit = { key ->
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        onKey(key)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoundKey(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") { tap(RemoteKey.BACK) }
            RoundKey(Icons.Default.Home, "Início") { tap(RemoteKey.HOME) }
            RoundKey(Icons.Default.StarOutline, "Opções") { tap(RemoteKey.INFO) }
            RoundKey(Icons.Default.PowerSettingsNew, "Desligar", danger = true) { tap(RemoteKey.POWER_OFF) }
        }

        DPadRing(onKey = tap)

        Row(
            modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoundKey(Icons.Default.Replay, "Repetir") { tap(RemoteKey.INSTANT_REPLAY) }
            RoundKey(Icons.Default.FastRewind, "Voltar") { tap(RemoteKey.REWIND) }
            RoundKey(Icons.Default.PlayArrow, "Reproduzir/Pausar", primary = true) { tap(RemoteKey.PLAY) }
            RoundKey(Icons.Default.FastForward, "Avançar") { tap(RemoteKey.FORWARD) }
        }

        VolumeRocker(onKey = tap)
    }
}

@Composable
private fun DPadRing(onKey: (RemoteKey) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().widthIn(max = 360.dp).aspectRatio(1f).padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
            tonalElevation = 2.dp
        ) {}
        EdgeKey(Icons.Default.KeyboardArrowUp, "Cima", Alignment.TopCenter) { onKey(RemoteKey.UP) }
        EdgeKey(Icons.Default.KeyboardArrowDown, "Baixo", Alignment.BottomCenter) { onKey(RemoteKey.DOWN) }
        EdgeKey(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Esquerda", Alignment.CenterStart) { onKey(RemoteKey.LEFT) }
        EdgeKey(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Direita", Alignment.CenterEnd) { onKey(RemoteKey.RIGHT) }
        Surface(
            modifier = Modifier.fillMaxWidth(0.4f).aspectRatio(1f).clip(CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            onClick = { onKey(RemoteKey.SELECT) }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("OK", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.EdgeKey(
    icon: ImageVector, desc: String, align: Alignment, onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.align(align).padding(6.dp).size(56.dp).clip(CircleShape),
        shape = CircleShape,
        color = androidx.compose.ui.graphics.Color.Transparent,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, desc, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun VolumeRocker(onKey: (RemoteKey) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PillKey(Icons.Default.Remove, "Diminuir volume", Modifier.weight(1f)) { onKey(RemoteKey.VOLUME_DOWN) }
        RoundKey(Icons.Default.VolumeOff, "Mudo") { onKey(RemoteKey.VOLUME_MUTE) }
        PillKey(Icons.Default.Add, "Aumentar volume", Modifier.weight(1f)) { onKey(RemoteKey.VOLUME_UP) }
    }
}

@Composable
private fun RoundKey(
    icon: ImageVector, desc: String, primary: Boolean = false, danger: Boolean = false, onClick: () -> Unit
) {
    val color = when {
        primary -> MaterialTheme.colorScheme.primary
        danger -> MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val tint = when {
        primary -> MaterialTheme.colorScheme.onPrimary
        danger -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
    Surface(modifier = Modifier.size(56.dp).clip(CircleShape), shape = CircleShape, color = color, onClick = onClick) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, desc, tint = tint) }
    }
}

@Composable
private fun PillKey(icon: ImageVector, desc: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(56.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, desc, tint = MaterialTheme.colorScheme.onSurface) }
    }
}

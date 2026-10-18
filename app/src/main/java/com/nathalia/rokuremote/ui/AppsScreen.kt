package com.nathalia.rokuremote.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nathalia.rokuremote.model.RokuApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun AppsScreen(
    apps: List<RokuApp>,
    activeAppId: String?,
    iconUrl: (String) -> String?,
    onLaunch: (RokuApp) -> Unit,
    modifier: Modifier = Modifier
) {
    val launchable = apps.filter { !it.isTvInput }
    if (launchable.isEmpty()) {
        Box(modifier.fillMaxSize().padding(24.dp), Alignment.Center) {
            Text("Nenhum app carregado ainda. Toque em atualizar no topo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
        }
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(104.dp),
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(launchable, key = { it.id }) { app ->
            AppCell(app, app.id == activeAppId, iconUrl(app.id)) { onLaunch(app) }
        }
    }
}

@Composable
private fun AppCell(app: RokuApp, active: Boolean, url: String?, onClick: () -> Unit) {
    val border = if (active) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = border
    ) {
        Column(
            Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val bitmap by produceState<ImageBitmap?>(null, url) { value = url?.let { loadIcon(it) } }
            val b = bitmap
            Box(Modifier.fillMaxWidth().aspectRatio(1.4f), Alignment.Center) {
                if (b != null) {
                    Image(b, app.name, Modifier.clip(RoundedCornerShape(10.dp)))
                } else {
                    Icon(Icons.Default.Tv, app.name, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(app.name, style = MaterialTheme.typography.labelSmall, maxLines = 1,
                overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        }
    }
}

private suspend fun loadIcon(url: String): ImageBitmap? = withContext(Dispatchers.IO) {
    runCatching { URL(url).openStream().use { BitmapFactory.decodeStream(it) }?.asImageBitmap() }.getOrNull()
}

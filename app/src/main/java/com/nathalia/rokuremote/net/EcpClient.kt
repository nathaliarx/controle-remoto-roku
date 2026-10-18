package com.nathalia.rokuremote.net

import com.nathalia.rokuremote.model.RemoteKey
import com.nathalia.rokuremote.model.RokuApp
import com.nathalia.rokuremote.model.RokuDevice
import com.nathalia.rokuremote.model.TvChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class EcpClient(
    private val client: OkHttpClient = defaultClient()
) {
    companion object {
        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .build()
    }

    private val empty = ByteArray(0).toRequestBody()

    suspend fun keypress(base: String, key: RemoteKey): Result<Unit> = post("$base/keypress/${key.ecp}")

    suspend fun keydown(base: String, key: RemoteKey): Result<Unit> = post("$base/keydown/${key.ecp}")

    suspend fun keyup(base: String, key: RemoteKey): Result<Unit> = post("$base/keyup/${key.ecp}")

    suspend fun typeChar(base: String, char: Char): Result<Unit> {
        val encoded = URLEncoder.encode(char.toString(), "UTF-8")
        return post("$base/keypress/Lit_$encoded")
    }

    suspend fun typeText(base: String, text: String): Result<Unit> {
        text.forEach { c ->
            val r = typeChar(base, c)
            if (r.isFailure) return r
        }
        return Result.success(Unit)
    }

    suspend fun launchApp(base: String, appId: String): Result<Unit> = post("$base/launch/$appId")

    suspend fun deviceInfo(host: String, base: String): Result<RokuDevice> =
        get("$base/query/device-info").map { RokuXml.parseDeviceInfo(host, it) }

    suspend fun apps(base: String): Result<List<RokuApp>> =
        get("$base/query/apps").map { RokuXml.parseApps(it) }

    suspend fun activeAppId(base: String): Result<String?> =
        get("$base/query/active-app").map { RokuXml.parseActiveAppId(it) }

    suspend fun tvChannels(base: String): Result<List<TvChannel>> =
        get("$base/query/tv-channels").map { RokuXml.parseTvChannels(it) }

    fun iconUrl(base: String, appId: String): String = "$base/query/icon/$appId"

    private suspend fun post(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        retry {
            client.newCall(Request.Builder().url(url).post(empty).build()).execute().use { resp ->
                if (!resp.isSuccessful) throw IOException("HTTP ${resp.code}")
            }
        }
    }

    private suspend fun get(url: String): Result<String> = withContext(Dispatchers.IO) {
        retry {
            client.newCall(Request.Builder().url(url).get().build()).execute().use { resp ->
                if (!resp.isSuccessful) throw IOException("HTTP ${resp.code}")
                resp.body?.string().orEmpty()
            }
        }
    }

    private inline fun <T> retry(attempts: Int = 2, block: () -> T): Result<T> {
        var last: Throwable? = null
        repeat(attempts) {
            try {
                return Result.success(block())
            } catch (e: IOException) {
                last = e
            }
        }
        return Result.failure(last ?: IOException("falha de rede"))
    }
}

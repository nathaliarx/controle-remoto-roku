package com.nathalia.rokuremote

import com.nathalia.rokuremote.model.RemoteKey
import com.nathalia.rokuremote.net.EcpClient
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EcpClientTest {

    private lateinit var server: MockWebServer
    private val client = EcpClient()

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun base() = "http://${server.hostName}:${server.port}"

    @Test
    fun keypressPostsToExpectedPath() = runTest {
        server.enqueue(MockResponse().setResponseCode(200))
        val result = client.keypress(base(), RemoteKey.HOME)
        assertTrue(result.isSuccess)
        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/keypress/Home", request.path)
    }

    @Test
    fun typeTextEncodesCharsAsLit() = runTest {
        repeat(4) { server.enqueue(MockResponse().setResponseCode(200)) }
        client.typeText(base(), "roku")
        assertEquals("/keypress/Lit_r", server.takeRequest().path)
        assertEquals("/keypress/Lit_o", server.takeRequest().path)
        assertEquals("/keypress/Lit_k", server.takeRequest().path)
        assertEquals("/keypress/Lit_u", server.takeRequest().path)
    }

    @Test
    fun failedKeypressReturnsFailure() = runTest {
        server.enqueue(MockResponse().setResponseCode(403))
        val result = client.keypress(base(), RemoteKey.POWER_OFF)
        assertTrue(result.isFailure)
    }

    @Test
    fun appsQueryParsed() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """<apps><app id="dev" type="appl" version="2.0.0">Twitch TV</app></apps>"""
            )
        )
        val apps = client.apps(base()).getOrThrow()
        assertEquals(1, apps.size)
        assertTrue(apps.first().isSideloaded)
    }
}

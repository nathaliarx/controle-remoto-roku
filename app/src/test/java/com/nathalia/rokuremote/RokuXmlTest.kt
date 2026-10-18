package com.nathalia.rokuremote

import com.nathalia.rokuremote.net.RokuXml
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RokuXmlTest {

    @Test
    fun parsesDeviceInfo() {
        val xml = """
            <device-info>
              <model-name>43R6500</model-name>
              <software-version>16.0.4</software-version>
              <is-tv>true</is-tv>
              <supports-find-remote>false</supports-find-remote>
              <friendly-device-name>43" SEMP Roku TV</friendly-device-name>
              <power-mode>PowerOn</power-mode>
            </device-info>
        """.trimIndent()
        val d = RokuXml.parseDeviceInfo("192.168.0.2", xml)
        assertEquals("43R6500", d.modelName)
        assertEquals("16.0.4", d.softwareVersion)
        assertTrue(d.isTv)
        assertFalse(d.supportsFindRemote)
        assertEquals("http://192.168.0.2:8060", d.baseUrl)
    }

    @Test
    fun parsesAppsAndFlagsSideload() {
        val xml = """
            <apps>
              <app id="tvinput.hdmi1" type="tvin" version="1.0.0">HDMI 1</app>
              <app id="837" type="appl" version="2.0">YouTube</app>
              <app id="817397" type="appl" version="2.6.0">Stitch (Twitch)</app>
              <app id="dev" type="appl" version="2.0.0">Twitch TV</app>
            </apps>
        """.trimIndent()
        val apps = RokuXml.parseApps(xml)
        assertEquals(4, apps.size)
        val dev = apps.first { it.id == "dev" }
        assertTrue(dev.isSideloaded)
        assertEquals("Twitch TV", dev.name)
        val stitch = apps.first { it.id == "817397" }
        assertFalse(stitch.isSideloaded)
        assertTrue(apps.first { it.id == "tvinput.hdmi1" }.isTvInput)
    }

    @Test
    fun parsesTvChannelsSkippingEmpty() {
        val xml = """
            <tv-channels>
              <channel><number>4.1</number><name>TV GAZETA HD</name><type>air-digital</type><user-hidden>false</user-hidden></channel>
              <channel><number>2.1</number><name>TVE HD</name><type>air-digital</type><user-hidden>true</user-hidden></channel>
            </tv-channels>
        """.trimIndent()
        val ch = RokuXml.parseTvChannels(xml)
        assertEquals(2, ch.size)
        assertEquals("TV GAZETA HD", ch.first().name)
        assertTrue(ch[1].hidden)
    }

    @Test
    fun parsesActiveAppId() {
        val xml = """<active-app><app id="562859" type="home">Roku</app></active-app>"""
        assertEquals("562859", RokuXml.parseActiveAppId(xml))
    }
}

package com.nathalia.rokuremote

import com.nathalia.rokuremote.net.WakeOnLan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class WakeOnLanTest {

    @Test
    fun magicPacketHasSyncStreamAndSixteenRepeats() {
        val packet = WakeOnLan.buildMagicPacket("64:57:25:84:d4:42")
        assertEquals(102, packet.size)
        for (i in 0 until 6) assertEquals(0xFF.toByte(), packet[i])
        assertEquals(0x64.toByte(), packet[6])
        assertEquals(0x42.toByte(), packet[101])
    }

    @Test
    fun rejectsInvalidMac() {
        assertThrows(IllegalArgumentException::class.java) {
            WakeOnLan.buildMagicPacket("64:57:25")
        }
    }
}

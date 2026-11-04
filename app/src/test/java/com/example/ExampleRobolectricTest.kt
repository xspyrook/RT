package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ir.IrSignalBuilder
import com.example.model.BrandCatalog
import com.example.model.DeviceType
import com.example.model.IrProtocol
import com.example.model.RemoteButtonType
import com.example.mouse.AirMouseController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Smart IR Remote", appName)
    }

    @Test
    fun `test NEC protocol builds valid pulse timing array`() {
        val pattern = IrSignalBuilder.buildNecPattern(address = 0x04, command = 0x08)
        // Leader (2) + 32 bits * 2 + stop bit (1) = 67 elements
        assertTrue("Pattern length must be 67 for NEC", pattern.size == 67)
        assertEquals(9000, pattern[0]) // Leader ON
        assertEquals(4500, pattern[1]) // Leader OFF
        assertEquals(560, pattern.last()) // Stop bit
    }

    @Test
    fun `test Samsung protocol builds valid pulse timing array`() {
        val pattern = IrSignalBuilder.buildSamsungPattern(customCode = 0x07, dataCode = 0x02)
        assertTrue("Pattern length must be 67 for Samsung", pattern.size == 67)
        assertEquals(4500, pattern[0]) // Samsung Leader ON
        assertEquals(4500, pattern[1]) // Samsung Leader OFF
    }

    @Test
    fun `test BrandCatalog contains supported manufacturers and key mappings`() {
        assertTrue(BrandCatalog.supportedBrands.contains("Samsung"))
        assertTrue(BrandCatalog.supportedBrands.contains("LG"))
        assertTrue(BrandCatalog.supportedBrands.contains("Sony"))
        assertTrue(BrandCatalog.supportedBrands.contains("TCL"))

        val samsungPreset = BrandCatalog.getPreset("Samsung", DeviceType.TV)
        assertEquals(IrProtocol.SAMSUNG, samsungPreset.protocol)
        assertNotNull(samsungPreset.keyCodes[RemoteButtonType.POWER])
        assertNotNull(samsungPreset.keyCodes[RemoteButtonType.VOL_UP])
        assertNotNull(samsungPreset.keyCodes[RemoteButtonType.OK])

        val lgPreset = BrandCatalog.getPreset("LG", DeviceType.TV)
        assertEquals(IrProtocol.NEC, lgPreset.protocol)
        assertNotNull(lgPreset.keyCodes[RemoteButtonType.POWER])
    }

    @Test
    fun `test AirMouse touch drag tracking and gestures`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val controller = AirMouseController(context)
        controller.setEnabled(true)
        controller.setSensitivity(1.5f)

        controller.onMove(50f, 0f)
        val state = controller.mouseState.value
        assertTrue("Cursor X moved to the right", state.cursorX > 0.5f)

        val step = controller.consumeDirectionalStep()
        assertEquals("RIGHT", step)

        val click = controller.onSingleClick()
        assertEquals("CLICK", click)

        val back = controller.onRightClick()
        assertEquals("BACK", back)
    }
}

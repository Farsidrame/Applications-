package com.example

import com.example.ui.util.PrescriptionQrParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PrescriptionQrParserTest {

    @Test
    fun testParseJsonPrescription() {
        val json = """
            {
                "doctor": "Dr. Aminata Diallo",
                "patient": "Mamadou Dramé",
                "date": "08/09/2026",
                "medicines": [
                    {"name": "Doliprane 1000 mg", "qty": 2, "posology": "1 cp toutes les 8h"},
                    {"name": "Vitamine C 1000 mg", "qty": 1, "posology": "1 cp le matin"}
                ]
            }
        """.trimIndent()

        val parsed = PrescriptionQrParser.parse(json)
        assertEquals("Dr. Aminata Diallo", parsed.doctorName)
        assertEquals("Mamadou Dramé", parsed.patientName)
        assertEquals(2, parsed.items.size)
        assertEquals("Doliprane 1000 mg", parsed.items[0].medicineName)
        assertEquals(2, parsed.items[0].quantity)
        assertEquals(3, parsed.totalItemsCount)
    }

    @Test
    fun testParseStructuredTextPrescription() {
        val text = """
            Dr: Cheikh Anta Ndiaye
            Patient: Aissatou Ba
            Date: 08/09/2026
            Amoxicilline 500 mg
            Sirop Toplexil
        """.trimIndent()

        val parsed = PrescriptionQrParser.parse(text)
        assertEquals("Cheikh Anta Ndiaye", parsed.doctorName)
        assertEquals("Aissatou Ba", parsed.patientName)
        assertTrue(parsed.items.isNotEmpty())
    }

    @Test
    fun testDemoPresets() {
        val presets = PrescriptionQrParser.demoPresets
        assertTrue(presets.isNotEmpty())
        for (preset in presets) {
            val parsed = PrescriptionQrParser.parse(preset.jsonPayload)
            assertNotNull(parsed.doctorName)
            assertTrue(parsed.items.isNotEmpty())
        }
    }
}

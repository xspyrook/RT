package com.example.ir

import com.example.model.IrProtocol

object IrSignalBuilder {

    /**
     * Builds NEC protocol pulse/space timing array in microseconds.
     * Carrier: 38,000 Hz
     * Address (16-bit or 8-bit + ~8-bit), Command (8-bit + ~8-bit)
     */
    fun buildNecPattern(address: Int, command: Int): IntArray {
        val pattern = ArrayList<Int>()
        // Leader code
        pattern.add(9000)
        pattern.add(4500)

        // 32 bits total: address (8), inverse address (8), command (8), inverse command (8)
        val data: Long = ((address and 0xFF).toLong()) or
                (((address.inv()) and 0xFF).toLong() shl 8) or
                (((command and 0xFF).toLong()) shl 16) or
                ((((command.inv()) and 0xFF).toLong()) shl 24)

        for (i in 0 until 32) {
            val bit = (data shr i) and 1L
            pattern.add(560)
            if (bit == 1L) {
                pattern.add(1690)
            } else {
                pattern.add(560)
            }
        }
        // Stop bit
        pattern.add(560)
        return pattern.toIntArray()
    }

    /**
     * Builds Samsung 32-bit protocol pulse/space pattern.
     * Carrier: 38,000 Hz
     * Leader: 4500us ON, 4500us OFF
     */
    fun buildSamsungPattern(customCode: Int, dataCode: Int): IntArray {
        val pattern = ArrayList<Int>()
        // Samsung Leader
        pattern.add(4500)
        pattern.add(4500)

        // 32 bits: customCode (8), customCode (8), dataCode (8), ~dataCode (8)
        val fullData: Long = ((customCode and 0xFF).toLong()) or
                (((customCode and 0xFF).toLong()) shl 8) or
                (((dataCode and 0xFF).toLong()) shl 16) or
                ((((dataCode.inv()) and 0xFF).toLong()) shl 24)

        for (i in 0 until 32) {
            val bit = (fullData shr i) and 1L
            pattern.add(560)
            if (bit == 1L) {
                pattern.add(1690)
            } else {
                pattern.add(560)
            }
        }
        // Stop bit
        pattern.add(560)
        return pattern.toIntArray()
    }

    /**
     * Builds Sony SIRC 12-bit / 15-bit pattern.
     * Carrier: 40,000 Hz
     * Leader: 2400us ON, 600us OFF
     */
    fun buildSonyPattern(command: Int, address: Int, bits: Int = 12): IntArray {
        val pattern = ArrayList<Int>()
        // Leader
        pattern.add(2400)
        pattern.add(600)

        // 7 bits command (LSB first)
        for (i in 0 until 7) {
            val bit = (command shr i) and 1
            if (bit == 1) {
                pattern.add(1200)
            } else {
                pattern.add(600)
            }
            pattern.add(600)
        }

        // Remaining bits for address
        val addressBits = bits - 7
        for (i in 0 until addressBits) {
            val bit = (address shr i) and 1
            if (bit == 1) {
                pattern.add(1200)
            } else {
                pattern.add(600)
            }
            pattern.add(600)
        }
        return pattern.toIntArray()
    }

    /**
     * Builds RC5 pulse/space pattern (Philips).
     * Carrier: 36,000 Hz
     * 14 bits bi-phase Manchester encoding (bit time 1778us, half-bit 889us)
     */
    fun buildRc5Pattern(address: Int, command: Int, toggle: Boolean = false): IntArray {
        val pattern = ArrayList<Int>()
        val half = 889
        // 14 bits: 2 start bits (1, 1), 1 toggle bit, 5 address bits, 6 command bits
        val frame = (1 shl 13) or (1 shl 12) or
                ((if (toggle) 1 else 0) shl 11) or
                ((address and 0x1F) shl 6) or
                (command and 0x3F)

        var lastLevel = 0
        for (i in 13 downTo 0) {
            val bit = (frame shr i) and 1
            // In RC5:
            // Bit 1: low for half, then high for half
            // Bit 0: high for half, then low for half
            if (bit == 1) {
                pattern.add(half) // OFF / Space
                pattern.add(half) // ON / Mark
            } else {
                pattern.add(half) // ON
                pattern.add(half) // OFF
            }
        }
        return pattern.toIntArray()
    }

    /**
     * Generates a pulse pattern according to selected protocol and codes.
     */
    fun build(protocol: IrProtocol, primaryCode: Int, secondaryCode: Int = 0): Pair<Int, IntArray> {
        return when (protocol) {
            IrProtocol.SAMSUNG -> 38000 to buildSamsungPattern(primaryCode, secondaryCode)
            IrProtocol.SONY -> 40000 to buildSonyPattern(secondaryCode, primaryCode, 12)
            IrProtocol.RC5 -> 36000 to buildRc5Pattern(primaryCode, secondaryCode)
            IrProtocol.NEC, IrProtocol.RAW -> 38000 to buildNecPattern(primaryCode, secondaryCode)
        }
    }
}

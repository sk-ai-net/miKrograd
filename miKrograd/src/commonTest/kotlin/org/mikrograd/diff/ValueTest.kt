package org.mikrograd.diff


import kotlin.test.Test

class ValueTest {
    @Test
    fun testSanityCheck() {
        // micrograd equivalent
        val xmg = Value(-4.0)
        val zmg = 2.0 * xmg + 2 + xmg
        val qmg = zmg.relu() + zmg * xmg
        val hmg = (zmg * zmg).relu()
        val ymg = hmg + qmg + qmg * xmg
        ymg.backward()

        // Ktorch equivalent (imaginary API)
        /*
        val xpt = Ktorch.tensor(arrayOf(-4.0), dtype = Ktorch.Double)
        xpt.requiresGrad = true
        val zpt = 2 * xpt + 2 + xpt
        val qpt = zpt.relu() + zpt * xpt
        val hpt = (zpt * zpt).relu()
        val ypt = hpt + qpt + qpt * xpt
        ypt.backward()

        // forward pass
        assertEquals(ymg.data, ypt.item())
        // backward pass
        assertEquals(xmg.grad, xpt.grad().item())

         */
    }

    @Test
    fun testMoreOps() {
        // micrograd equivalent
        val amg = Value(-4.0)
        val bmg = Value(2.0)
        var cmg = amg + bmg
        var dmg = amg * bmg + bmg.pow(3.0)
        cmg += cmg + 1
        cmg += 1 + cmg + (-amg)
        dmg += dmg * 2 + (bmg + amg).relu()
        dmg += 3.0 * dmg + (bmg - amg).relu()
        val emg = cmg - dmg
        val fmg = emg.pow(2.0)
        var gmg = fmg / 2
        gmg += 10.0 / fmg
        gmg.backward()

        // Ktorch equivalent (imaginary API)
        /*
        val apt = Ktorch.tensor(arrayOf(-4.0), dtype = Ktorch.Double)
        val bpt = Ktorch.tensor(arrayOf(2.0), dtype = Ktorch.Double)
        apt.requiresGrad = true
        bpt.requiresGrad = true
        var cpt = apt + bpt
        var dpt = apt * bpt + bpt.pow(3)
        cpt = cpt + cpt + 1
        cpt = cpt + 1 + cpt + (-apt)
        dpt = dpt + dpt * 2 + (bpt + apt).relu()
        dpt = dpt + 3 * dpt + (bpt - apt).relu()
        val ept = cpt - dpt
        val fpt = ept.pow(2)
        val gpt = fpt / 2.0
        gpt = gpt + 10.0 / fpt
        gpt.backward()

        val tol = 1e-6
        // forward pass
        assert(abs(gmg.data - gpt.item()) < tol)
        // backward pass
        assert(abs(amg.grad - apt.grad().item()) < tol)
        assert(abs(bmg.grad - bpt.grad().item()) < tol)

         */
    }
}

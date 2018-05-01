package io.improbable.swayze.finiteDifference

import junit.framework.TestCase
import org.junit.Test
import java.sql.DriverManager

class BoundaryConditionTest {

    @Test
    fun checkBoundaryConditionApplication() {
        var fieldOfValueTwo = makeUnitSquareOnesField(10) * 2.0
        var shiftedN = ShiftN(fieldOfValueTwo)
        DriverManager.println("Check that southern boundary takes Dirichlet bc value of 1")
        for (i in 0 until shiftedN.iSize()) {
            for (j in 0 until shiftedN.jSize()) {
                if (j == 0) {
                    TestCase.assertEquals(1.0, shiftedN[i, j].value)
                } else {
                    TestCase.assertEquals(2.0, shiftedN[i, j].value)
                }
            }
        }
        var shiftedS = ShiftS(fieldOfValueTwo)
        DriverManager.println("Check that northern boundary takes Dirichlet bc value of 1")
        for (i in 0 until shiftedS.iSize()) {
            for (j in 0 until shiftedS.jSize()) {
                if (j == shiftedS.jSize() - 1) {
                    TestCase.assertEquals(1.0, shiftedS[i, j].value)
                } else {
                    TestCase.assertEquals(2.0, shiftedS[i, j].value)
                }
            }
        }
        var shiftedE = ShiftE(fieldOfValueTwo)
        DriverManager.println("Check that western boundary takes Dirichlet bc value of 1")
        for (i in 0 until shiftedE.iSize()) {
            for (j in 0 until shiftedE.jSize()) {
                if (i == 0) {
                    TestCase.assertEquals(1.0, shiftedE[i, j].value)
                } else {
                    TestCase.assertEquals(2.0, shiftedE[i, j].value)
                }
            }
        }
        var shiftedW = ShiftW(fieldOfValueTwo)
        DriverManager.println("Check that eastern boundary takes Dirichlet bc value of 1")
        for (i in 0 until shiftedW.iSize()) {
            for (j in 0 until shiftedW.jSize()) {
                if (i == shiftedW.iSize() - 1) {
                    TestCase.assertEquals(1.0, shiftedW[i, j].value)
                } else {
                    TestCase.assertEquals(2.0, shiftedW[i, j].value)
                }
            }
        }
        var shiftedWShiftedW = ShiftW(shiftedW)
        for (i in 0 until shiftedWShiftedW.iSize()) {
            for (j in 0 until shiftedWShiftedW.jSize()) {
                if (i == shiftedWShiftedW.iSize() - 1 || i == shiftedWShiftedW.iSize() - 2) {
                    TestCase.assertEquals(1.0, shiftedWShiftedW[i, j].value)
                } else {
                    TestCase.assertEquals(2.0, shiftedWShiftedW[i, j].value)
                }
            }
        }
    }


    @Test
    fun checkPeriodicConditionApplication() {
        var testField = makeUniqueValuedField(10)
        var shiftedN = ShiftN(testField)
        var shiftedS = ShiftS(testField)
        var shiftedE = ShiftE(testField)
        var shiftedW = ShiftW(testField)
        for (i in 0 until shiftedN.iSize()) {
            for (j in 0 until shiftedN.jSize()) {
                if (j == 0) {
                    TestCase.assertEquals(testField[i, testField.jSize() - 1].value, shiftedN[i, j].value)
                } else {
                    TestCase.assertEquals(testField[i, j - 1].value, shiftedN[i, j].value)
                }
            }
        }
        for (i in 0 until shiftedS.iSize()) {
            for (j in 0 until shiftedS.jSize()) {
                if (j == shiftedS.jSize() - 1) {
                    TestCase.assertEquals(testField[i, 0].value, shiftedS[i, j].value)
                } else {
                    TestCase.assertEquals(testField[i, j + 1].value, shiftedS[i, j].value)
                }
            }
        }
        for (i in 0 until shiftedE.iSize()) {
            for (j in 0 until shiftedE.jSize()) {
                if (i == 0) {
                    TestCase.assertEquals(testField[testField.iSize() - 1, j].value, shiftedE[i, j].value)
                } else {
                    TestCase.assertEquals(testField[i - 1, j].value, shiftedE[i, j].value)
                }
            }
        }
        for (i in 0 until shiftedW.iSize()) {
            for (j in 0 until shiftedW.jSize()) {
                if (i == shiftedW.iSize() - 1) {
                    TestCase.assertEquals(testField[0, j].value, shiftedW[i, j].value)
                } else {
                    TestCase.assertEquals(testField[i + 1, j].value, shiftedW[i, j].value)
                }
            }
        }
    }


    @Test
    fun checkMixedBoundaryApplication() {
        var testField = makeMixedBoundaryField(10)
        var alteredField = ((ShiftN(testField) + ShiftS(testField)) / (testField.domain.DY * testField.domain.DY)
                + (ShiftE(testField) + ShiftW(testField)) / (testField.domain.DX * testField.domain.DX))
        for (i in 0 until testField.iSize()) {
            for (j in 0 until testField.jSize()) {
                var DX = 1.0 / 9.0
                var DY = 1.0 / 9.0
                var i_plus_1 = i + 1
                var i_minus_1 = i - 1
                var j_plus_1 = j + 1
                var j_minus_1 = j - 1
                var bc_top = 1
                var bc_bot = 1
                if (i == 0) {
                    i_minus_1 = testField.iSize() - 1
                }
                if (j == 0) {
                    j_minus_1 = testField.jSize() - 1; bc_bot = 0
                }
                if (i == testField.iSize() - 1) {
                    i_plus_1 = 0
                }
                if (j == testField.jSize() - 1) {
                    j_plus_1 = 0; bc_top = 0
                }
                var expectedVal = (((bc_bot * (i * 100 + j_minus_1) + bc_top * (i * 100 + j_plus_1)) / (DY * DY))
                        + (((i_minus_1 * 100 + j) + (i_plus_1 * 100 + j)) / (DX * DX)))
                TestCase.assertEquals(expectedVal, alteredField[i, j].value, 1e4)
            }
        }
    }
}
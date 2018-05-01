package io.improbable.swayze.finiteDifference

import junit.framework.TestCase
import org.junit.Test

class ArrayRotationTest {

    fun makeTestArray(): Array2D<Double> {
        var testArray = Array2D(10, 10, { i, j ->
            when (i) {
                0 -> when (j) {
                    0 -> 1.0
                    else -> 0.0
                }
                else -> 0.0
            }
        })
        return testArray
    }

    @Test
    fun checkClockwise90() {
        var testArray = makeTestArray()
        var clock90 = Array2D.RotateClockwise90(testArray)
        TestCase.assertEquals(1.0, clock90[0, 9])
    }

    @Test
    fun checkAntiClockwise90() {
        var testArray = makeTestArray()
        var antiClock90 = Array2D.RotateAntiClockwise90(testArray)
        TestCase.assertEquals(1.0, antiClock90[9, 0])
    }

    @Test
    fun checkRotate180() {
        var testArray = makeTestArray()
        var rotate180 = Array2D.Rotate180(testArray)
        TestCase.assertEquals(1.0, rotate180[9, 9])
    }
}
package io.improbable.swayze.finiteDifference

import junit.framework.TestCase
import org.junit.Test
import java.sql.DriverManager

class DifferentialOperatorsTest {

    @Test
    fun checkDifferentialOperators() {
        var fieldOfValueTwo = makeUnitSquareOnesField(10) * 2.0
        var d_dx = d_dx(fieldOfValueTwo)
        DriverManager.println("Check the bc moves inwards")
        TestCase.assertEquals(-5.0, d_dx[9, 5].value)
        TestCase.assertEquals(5.0, d_dx[0, 5].value)
        var d_dy = d_dy(fieldOfValueTwo)
        TestCase.assertEquals(-5.0, d_dy[5, 9].value)
        TestCase.assertEquals(5.0, d_dy[5, 0].value)
        var d2_dx2 = d2_dx2(fieldOfValueTwo)
        TestCase.assertEquals(-100.0, d2_dx2[9, 5].value, 1.0e-5)
        TestCase.assertEquals(-100.0, d2_dx2[0, 5].value, 1.0e-5)
        var d2_dy2 = d2_dy2(fieldOfValueTwo)
        TestCase.assertEquals(-100.0, d2_dy2[5, 9].value, 1.0e-5)
        TestCase.assertEquals(-100.0, d2_dy2[5, 0].value, 1.0e-5)
    }
}
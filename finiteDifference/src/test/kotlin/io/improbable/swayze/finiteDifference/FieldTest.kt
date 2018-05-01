package io.improbable.swayze.finiteDifference

import io.improbable.keanu.kotlin.ArithmeticDouble
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.sql.DriverManager.println

class FieldTest {

    @Test
    fun checkAddition () {
        var t = ArithmeticDouble(1.0)
        var t2 = ArithmeticDouble(2.0)
        t2 += ArithmeticDouble(1.0)
        assertEquals(t.value, 1.0)
        assertEquals(t2.value, 3.0)

        var fieldOfValueOne = makeUnitSquareOnesField(10)
        println("A sample from a field of ones... " + fieldOfValueOne[5,5])
        assertEquals(1.0, fieldOfValueOne[5,5].value)
        assertEquals(fieldOfValueOne[5,5]!!.value, 1.0)

        var fieldOfValueTwo = fieldOfValueOne + fieldOfValueOne
        println("Check that summing two fields works, we should get two... " + fieldOfValueTwo[5,5])
        assertEquals(2.0, fieldOfValueTwo[5,5].value)
        assertEquals(1.0, fieldOfValueOne[5,5].value)

        var fieldOfValueThree = fieldOfValueTwo + fieldOfValueOne
        println("Check that summing two fields works, we should get two... " + fieldOfValueTwo[5,5])
        assertEquals(3.0, fieldOfValueThree[5,5].value)
    }


    @Test
    fun checkScaling () {
        var fieldOfValueOne = makeUnitSquareOnesField(10)
        var fieldOfValueTwo = fieldOfValueOne * 2.0
        println("Check that doubling by multiplying by a Double works... " + fieldOfValueTwo[5,5])
        assertEquals(2.0, fieldOfValueTwo[5,5].value)

        var fieldOfValueFour = fieldOfValueTwo * 2.0
        println("Check that doubling by multiplying by a cfd.ArithmeticDouble works... " + fieldOfValueFour[5,5])
        assertEquals(4.0, fieldOfValueFour[5,5].value)

        var fieldOfValueEight = fieldOfValueFour * fieldOfValueTwo
        println("Check that doubling by multiplying by a cfd.field.Field works... " + fieldOfValueEight[5,5])
        assertEquals(8.0, fieldOfValueEight[5,5].value)

        assertEquals(6.0, (ArithmeticDouble(3.0) * ArithmeticDouble(2.0)).value)
    }


    @Test
    fun checkSubtraction () {
        var fieldOfValueOne = makeUnitSquareOnesField(10)
        var fieldOfValueTen = fieldOfValueOne * 10.0
        var fieldOfValueNine = fieldOfValueTen - fieldOfValueOne
        println("Check that subtracting on cfd.field.Field by another works... " + fieldOfValueNine[5,5])
        assertEquals(9.0, fieldOfValueNine[5,5].value)
        assertEquals(9.0, (ArithmeticDouble(11.0) - ArithmeticDouble(2.0)).value)
        assertEquals(90.0, (fieldOfValueTen * (11.0 - 2.0))[5,5].value)
        assertEquals(1.736111111111, (ArithmeticDouble(2.0) / (ArithmeticDouble(2.4) * ArithmeticDouble(2.4)) + ArithmeticDouble(2.0) / (ArithmeticDouble(1.2) * ArithmeticDouble(1.2))).value, 1e6)
        assertEquals(1.0, (ArithmeticDouble(3.0) - ArithmeticDouble(2.0)).value)
    }

    @Test
    fun checkDivision () {
        var fieldOfValueOne = makeUnitSquareOnesField(10)
        var fieldOfValueForty = fieldOfValueOne * 40.0
        var fieldOfValueTwenty = fieldOfValueForty / 2.0
        println("Check that halving by dividing by a Double works... " + fieldOfValueTwenty[5,5])
        assertEquals(20.0, fieldOfValueTwenty[5,5].value)
        var fieldOfValueTwo = fieldOfValueForty / fieldOfValueTwenty
        println("Check that halving by dividing by another field works... " + fieldOfValueTwo[5,5])
        assertEquals(2.0, fieldOfValueTwo[5,5].value)
        var fieldOfValueTen = fieldOfValueTwenty / 2.0
        println("Check that halving by dividing by an cfd.ArithmeticDouble works... " + fieldOfValueTen[5,5])
        assertEquals(10.0, fieldOfValueTen[5,5].value)
        assertEquals(6.0, (ArithmeticDouble(42.0) / ArithmeticDouble(7.0)).value)
    }






}

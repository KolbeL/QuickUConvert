package com.sodamoney.quickuconvert

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DerivedUnitsUnitTests {

    @Test
    fun lengthConversion_isCorrect() {
        val expected = 10.0
        val actual = Millimeter.convertTo(100.0, Centimeter)
        assertEquals(expected, actual, 1E-6)
    }

    @Test
    fun lengthConvertBetweenSystems() {
        val expected = 38.1
        val actual = Inch.convertTo(1.5, Millimeter)
        assertEquals(expected, actual,1E-6)
    }

    @Test
    fun convertBetweenUnitTypes_isBad() {
        assertThrows(
            "Can't convert from LENGTH to FORCE. Base units are mismatched m and N",
            IllegalConversionException::class.java,
            { Meter.convertTo(10.0, Newton) })
    }

    @Test
    fun convertBetweenBaseUnitAndTemperature_isBad() {
        assertThrows(
            "Can't convert from LENGTH to FORCE. Base units are mismatched m and N",
            IllegalConversionException::class.java,
            { Kelvin.convertTo(10.0, Kilogram) })
        assertThrows(
            "Can't convert from LENGTH to FORCE. Base units are mismatched m and N",
            IllegalConversionException::class.java,
            { Kilogram.convertTo(10.0, Kelvin) })
    }

    @Test
    fun temperatureConversion_isCorrect() {
        val expected = 104.0
        val kelvin = 313.15
        assertEquals(kelvin, Centigrade.standardize(40.0) ,1E-6)
        assertEquals(kelvin, Fahrenheit.standardize(104.0), 1E-6)
        val actual = Centigrade.convertTo(40.0, Fahrenheit)
        assertEquals(expected, actual, 1E-6)
    }

    @Test
    fun forceConversion() {
        var expected = 22.48
        var actual = Newton.convertTo(100.0, PoundForce)
        assertEquals(expected, actual, 0.01)
        expected = 723.3
        actual = Newton.convertTo(100.0, Poundal)
        assertEquals(expected, actual, 0.01)
    }

    @Test
    fun converToLightYears() {
        val expected = SPEED_OF_LIGHT.toDouble() * 60.0 * 60.0 * 24.0 * 365.25
        val actual = LightYear.convertTo(1.0, Meter)
        assertEquals(expected, actual, 10.0)
    }
}
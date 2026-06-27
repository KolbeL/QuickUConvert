package com.sodamoney.quickuconvert

/*
 * Copyright 2026 David Weiss
 *
 * Collection of Derived to be used.
 */

const val Kilo = 1_000
const val Mega = Kilo * Kilo
const val Giga = Kilo * Mega

// region Acceleration
val InchPerSecondSquared =
    Units("in/s²", { it * Meter.convertTo(EARTH_GRAVITY, Inch) }, Category.ACCELERATION)
// endregion

// region Forces
val Newton = Units("N", { it }, Category.FORCE)
val Kilonewton = Units("kN", { Newton.standardize(it) / Kilo }, Category.FORCE)
val KilogramForce =
    Units(
        "kgf",
        { it * EARTH_GRAVITY },
        Category.FORCE
    )
val Dyne = Units("dyn", { Newton.standardize(it) * 100_000 }, Category.FORCE)
val PoundForce =
    Units("lbf", { it * (EARTH_GRAVITY * PoundMass.standardize(1.0)) }, Category.FORCE)
val Kips = Units("kip", { PoundForce.standardize(it) * Kilo }, Category.FORCE)
val Poundal = Units(
    "pdl",
    { PoundForce.standardize(it) / Meter.convertTo(EARTH_GRAVITY, Foot) },
    Category.FORCE
)
// endregion

// region Length
val Kilometer = Units("km", { it * Kilo }, Category.LENGTH)
val Centimeter = Units("cm", { it / 100 }, Category.LENGTH)
val Millimeter = Units("mm", { it / Kilo }, Category.LENGTH)
val Micrometer = Units("μm", { it / Mega }, Category.LENGTH)
val Nanometer = Units("nm", { it / Giga }, Category.LENGTH)
val Inch = Units("in", { it * 0.0254 }, Category.LENGTH)
val Foot = Units("ft", { it * 0.3048 }, Category.LENGTH)
val Yard = Units("yd", { it * 0.3048 * 3 }, Category.LENGTH)
val Mile = Units("mi", { it * 0.3048 * 5_280 }, Category.LENGTH)
val Thou = Units("mils", { it * 0.0254 / Kilo }, Category.LENGTH)
val NauticalMile = Units("NM", { it * 1852 }, Category.LENGTH)
val Fathom = Units("fathom", { Foot.standize(it) * 6}, Category.LENGTH)
val Rod = Units("rd", { Foot.standardize(it) * 16.5}, Category.LENGTH)
val Chain = Units("ch", { Rod.standardize(it) * 4}, Category.LENGTH)
val Furlong = Units("fur", { Mile.standize(it) / 8}, Category.LENGTH)
val Angstrom = Units("Å", { it / 1E-10 }, Category.LENGTH)	
val LightYear =
    Units(
        "ly",
        { it * (SPEED_OF_LIGHT * Year.convertTo(1.0, Second)) },
        Category.LENGTH
    )
val Lengths = arrayOf(
    Kilometer,
    Meter,
    Centimeter,
    Millimeter,
    Micrometer,
    Nanometer,
    Inch,
    Thou,
    Foot,
    Yard,
    Mile,
    Rod,
    Chain,
    Furlong,
    NauticalMile,
    Fathom,
    Angstrom,
    LightYear
)
// endregion

// region Mass
val PoundMass = Units("lb", { it * 0.45359237 }, Category.MASS)
// endregion

// region Temperatures
class TemperatureUnits(
    symbol: String,
    standardize: (Double) -> Double,
    val fromStandard: (Double) -> Double
) : Units(
    symbol,
    standardize,
    Category.TEMPERATURE
) {
    fun convertTo(inputValue: Double, other: TemperatureUnits): Double {
        if (this.category != other.category) {
            throw IllegalConversionException(
                "Can't convert from ${this.symbol} to ${other.symbol}. Base units are mismatched ${this.category.baseUnits()} and ${other.category.baseUnits()}"
            )
        }
        return other.fromStandard(this.standardize(inputValue))
    }
}

val Centigrade = TemperatureUnits("°C", { 273.15 + it }, { it - 273.15 })
val Fahrenheit = TemperatureUnits("°F", { (459.67 + it) / 1.8 }, { (it * 1.8) - 459.67 })
val Rankine = TemperatureUnits("°R", { it / 1.8 }, { it * 1.8 })
val Temperatures = arrayOf(Centigrade, Fahrenheit, Kelvin, Rankine)
// endregion

// region Time
val Millisecond = Units("ms", { it / Kilo }, Category.TIME)
val Microsecond = Units("μs", { it / Mega }, Category.TIME)
val Nanosecond = Units("ns", { it / Giga }, Category.TIME)
val Minute = Units("m", { it * 60 }, Category.TIME)
val Hour = Units("hr", { Minute.standardize(it) * 60 }, Category.TIME)
val Day = Units("d", { Hour.standardize(it) * 24 }, Category.TIME)
val Year = Units("yr", { Day.standardize(it) * 365.25 }, Category.TIME)
val Week = Units("wk", { Day.standardize(it) * 7 }, Category.TIME)
val Month = Units("mo", { Year.standardize(it) / 12 }, Category.TIME)
val Decade = Units("dec", { Year.standardize(it) * 10 }, Category.TIME)
val Century = Units("cen", { Year.standardize(it) * 100 }, Category.TIME)
// endregion

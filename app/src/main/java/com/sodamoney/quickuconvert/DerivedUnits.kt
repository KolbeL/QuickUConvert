package com.sodamoney.quickuconvert

import androidx.annotation.StringRes

/*
 * Copyright 2026 David Weiss
 *
 * Collection of Derived to be used.
 */


// Lengths
val Centimeter = Units("Centimeter", R.string.centimeter,"cm", { it / 100 }, Category.LENGTH)
val Millimeter = Units("Millimeter", R.string.millimeter,"mm", { it / 1000 }, Category.LENGTH)

val Kilometer = Units(name = "Kilometer",R.string.kilogram, "km", { it * 1000}, Category.LENGTH)

// Forces
val Newton = Units("Newton", R.string.newton, "N", { it }, Category.FORCE)
val KilogramForce =
    Units("Kilogram Force", R.string.kilogram_force, "kgf", { it * GRAVITATIONAL_ACCELERATION }, Category.FORCE)


// Temperatures
class TemperatureUnits(
    name: String,
    @StringRes stringId: Int,
    symbol: String,
    standardize: (Double) -> Double,
    val fromStandard: (Double) -> Double
) : Units(
    name,
    stringId,
    symbol,
    standardize,
    Category.TEMPERATURE
) {
    fun convertTo(inputValue: Double, other: TemperatureUnits): Double {
        if (this.category != other.category) {
            throw IllegalConversionException(
                "Can't convert from ${this.name} to ${other.name}. Base units are mismatched ${this.category.baseUnits()} and ${other.category.baseUnits()}"
            )
        }
        println("Standardized = ${this.standardize(inputValue)}")
        return other.fromStandard(this.standardize(inputValue))
    }
}

val Centigrade = TemperatureUnits("Centigrade", R.string.centigrade, "°C", { 273.15 + it }, { it - 273.15 })
val Fahrenheit =
    TemperatureUnits("Fahrenheit", R.string.fahrenheit, "°F", { (459.67 + it) / 1.8 }, { (it * 1.8) - 459.67 })

val Rankine = TemperatureUnits("Rankine", R.string.rankine, "°R", { it / 1.8 }, { it * 1.8 })
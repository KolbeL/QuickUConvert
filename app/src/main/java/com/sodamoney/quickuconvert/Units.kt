package com.sodamoney.quickuconvert

import androidx.annotation.StringRes

open class Units (
    /**
     * The (US) name of the unit
     */
    val name: String,

    /**
     * Localization string resource ID.
     */
    @StringRes val stringId: Int,

    /**
     * Symbol used in equations
     */
    val symbol: String,

    /**
     * The formula to convert to a standard (base) unit.
     * 1 BASE UNIT = X UNITS
     */
    val standardize: (Double) -> Double,

    /**
     * The category of unit for conversion.
     */
    val category: Category
){
    /**
     * Convert from one unit to another
     */
    open fun convertTo(inputValue: Double, other: Units): Double {
        if (this.category != other.category) {
            throw IllegalConversionException(
                "Can't convert from ${this.name} to ${other.name}. Base units are mismatched ${this.category.baseUnits()} and ${other.category.baseUnits()}"
            )
        }
        return 1 / other.standardize(1 / this.standardize(inputValue))
    }
}


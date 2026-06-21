package com.sodamoney.quickuconvert

/*
 * Copyright 2026 David Weiss
 *
 * Unit conversion categories.
 */

enum class Category {
    LENGTH,
    AREA,
    WET_VOLUME,
    DRY_VOLUME,
    MASS,
    FORCE,
    MOMENT,
    PRESSURE,
    UNIFORM_LOAD,
    TEMPERATURE,
    SPEED,
    ACCELERATION,
    STIFFNESS,
    MASS_FLOW,
    VOL_FLOW,
    POWER,
    ENERGY,
    DENSITY;


    fun baseUnits(): String {
        return when (this) {
            LENGTH -> "m"
            AREA -> "m²"
            WET_VOLUME -> "m³"
            DRY_VOLUME -> "m³"
            MASS -> "kg"
            FORCE -> "N"
            MOMENT -> "Nm"
            PRESSURE -> "Pa"
            UNIFORM_LOAD -> "N/mm"
            TEMPERATURE -> "K"
            SPEED -> "m/s"
            ACCELERATION -> "m/s²"
            STIFFNESS -> "N/mm"
            MASS_FLOW -> "kg/s"
            VOL_FLOW -> "m³/hr"
            POWER -> "W"
            ENERGY -> "J"
            DENSITY -> "kg/m³"
        }
    }
}
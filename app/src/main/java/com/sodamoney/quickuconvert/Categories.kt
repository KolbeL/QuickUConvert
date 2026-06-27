package com.sodamoney.quickuconvert

/*
 * Copyright 2026 David Weiss
 *
 * Unit conversion categories.
 */

enum class Category {
    ACCELERATION,
    AREA,
    DENSITY,
    ENERGY,
    FORCE,
    LENGTH,
    MASS,
    MASS_FLOW,
    MOMENT,
    POWER,
    PRESSURE,
    SPEED,
    STIFFNESS,
    TEMPERATURE,
    TIME,
    UNIFORM_LOAD,
    VOL_FLOW,
    VOLUME;


    fun baseUnits(): String {
        return when (this) {
            ACCELERATION -> "m/s²"
            AREA -> "m²"
            DENSITY -> "kg/m³"
            ENERGY -> "J"
            FORCE -> "N"
            LENGTH -> "m"
            MASS -> "kg"
            MASS_FLOW -> "kg/s"
            MOMENT -> "Nm"
            POWER -> "W"
            PRESSURE -> "Pa"
            SPEED -> "m/s"
            STIFFNESS -> "N/mm"
            TEMPERATURE -> "K"
            TIME -> "s"
            UNIFORM_LOAD -> "N/mm"
            VOL_FLOW -> "m³/hr"
            VOLUME -> "m³"
        }
    }

    fun resource(): Int {
        return when (this) {
            ACCELERATION -> R.string.acceleration
            AREA -> R.string.area
            DENSITY -> R.string.density
            ENERGY -> R.string.energy
            FORCE -> R.string.force
            LENGTH -> R.string.length
            MASS -> R.string.mass
            MASS_FLOW -> R.string.mass_flow_rate
            MOMENT -> R.string.moment
            POWER -> R.string.power
            PRESSURE -> R.string.pressure
            SPEED -> R.string.speed
            STIFFNESS -> R.string.stiffness
            TEMPERATURE -> R.string.temperature
            TIME -> R.string.time
            UNIFORM_LOAD -> R.string.uniform_load
            VOLUME -> R.string.volume
            VOL_FLOW -> R.string.volumetric_flow_rate
        }
    }
}

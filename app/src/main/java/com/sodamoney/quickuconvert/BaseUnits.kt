package com.sodamoney.quickuconvert

/*
 * Copyright 2026 David Weiss
 *
 * Collection of Base Units to be used.
 */

const val GRAVITATIONAL_ACCELERATION = 9.80655 // m/s^2
const val SPEED_OF_LIGHT = 299_792_458 // m/s

val Meter = Units(name="Meter", stringId = R.string.meter, symbol = "m", standardize= {it}, category=Category.LENGTH)
val Second = Units(name="Second", stringId = R.string.second, symbol = "s", standardize= {it}, category=Category.LENGTH)
val Kilogram = Units(name="Kilogram", stringId = R.string.kilogram, symbol = "kg", standardize= {it}, category=Category.LENGTH)
val Kelvin = TemperatureUnits(name="Kelvin", stringId = R.string.kelvin, symbol = "K", standardize= {it}, fromStandard = {it})
//val AMP = Units(name="Amp", stringId = R.string.amp, symbol = "A", standardize= {it}, category = Category.CURRENT)
//val Candela = Units(name="Candela", stringId = R.string.candela, symbol = "cs", standardize= {it}, category=Category.LENGTH)
//val MOL = Units(name="Mol", stringId = R.string.mol, symbol = "mol", standardize= {it}, category=Category.LENGTH)

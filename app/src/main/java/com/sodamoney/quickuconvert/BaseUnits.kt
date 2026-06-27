package com.sodamoney.quickuconvert

/*
 * Copyright 2026 David Weiss
 *
 * Collection of Base Units to be used.
 */

const val EARTH_GRAVITY = 9.80655 // m/s^2
const val SPEED_OF_LIGHT = 299_792_458 // m/s

val Meter = Units("m", {it}, category=Category.LENGTH)
val Second = Units("s", {it}, category=Category.TIME)
val Kilogram = Units("kg", {it}, category=Category.MASS)
val Kelvin = TemperatureUnits("K", {it}, fromStandard = {it})
//val AMP = Units("A", {it}, category = Category.CURRENT)
//val Candela = Units("cs", {it}, category=Category.LENGTH)
//val MOL = Units("mol", {it}, category=Category.LENGTH)

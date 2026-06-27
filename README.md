# UConvert
Android Unit Converter


All input units will first be converted to SI then to the desired output unit.
This will introduce small conversion errors but generally those are not an issue.


## Categories
Categories of units for conversion.

- Acceleration
- Area
- Density
- Energy¹
- Force
- Length
- Mass
- Mass Flow Rate
- Moment (Torque)¹
- Power
- Pressure
- Speed
- Stiffness
- Temperature
- Time
- Uniform Load
- Volume
- Volumetric Flow Rate

Equivalent Units Notes:
1. Energy and Torque are ultimately the same SI units but are treated differently.


All units first convert to SI then to the chosen unit. Conversion errors may compound.

### Acceleration
- [ ] in/s^2
- [ ] ft/s^2
- [ ] m/s^2
- [ ] cm/s^2
- [ ] mm/s^2
- [ ] g

### Area
- [ ] sq in
- [ ] sq mi
- [ ] sq km
- [ ] sq ft
- [ ] sq mm
- [ ] sq yd
- [ ] sq cm
- [ ] sq m
- [ ] hectare
- [ ] acre

### Density
- [ ] lbm/in^3
- [ ] lbm/ft^3
- [ ] lbm/yd^3
- [ ] lbm/gal
- [ ] oz/gal
- [ ] kg/L
- [ ] kg/m^3
- [ ] kg/cm^3
- [ ] g/cm^3
- [ ] mg/dL


### Energy
- [ ] calorie
- [ ] btu
- [ ] hp-hr
- [ ] kcal
- [ ] kbtu
- [ ] joule
- [ ] erg
- [ ] kilojoule
- [ ] hw-hr

### Force
- [x] N
- [x] kN
- [x] poundal (1lbm * ft / s^2)
- [x] lbf
- [x] kips
- [x] dyne
- [x] kgf

### Length
- [x] inch
- [ ] rod
- [x] mm
- [x] km
- [x] mils (thou)
- [x] ft
- [x] US statute mile
- [x] cm
- [x] micron (micrometer)
- [x] yd
- [x] Nautical mile
- [ ] dm
- [ ] angstrom
- [ ] fathom
- [ ] league
- [x] m
- [ ] furlong

### Mass
- [ ] oz
- [ ] grain
- [ ] tonne
- [x] lbm
- [ ] mg
- [ ] stone
- [ ] ton (short)
- [ ] ton (long)
- [ ] g
- [x] kg
- [ ] hundredweight (short)
- [ ] hundredweight (long)
- [ ] slug

### Mass Flow rate
- [ ] lbm/s
- [ ] lbm/min
- [ ] lbm/hr
- [ ] oz/s
- [ ] oz/min
- [ ] kg/s
- [ ] kg/min
- [ ] kg/hr
- [ ] g/s
- [ ] g/min

### Moment / Torque
- [ ] in-lb
- [ ] ft-lb
- [ ] in-kips
- [ ] ft-kips
- [ ] N-m
- [ ] kN-m
- [ ] N-cm
- [ ] N-mm
- [ ] kgf-M

### Power
- [ ] cal / sec
- [ ] joule / sec
- [ ] erg / sec
- [ ] kjoule / sec
- [ ] kW
- [ ] BTU / sec
- [ ] ft-lb / min
- [ ] hp - British
- [ ] hp - metric
- [ ] ft-lb / sec

### Pressure 
- [ ] psi(g)
- [ ] psi(a)
- [ ] ksi
- [ ] kn/m^2
- [ ] mpa
- [ ] bar(g)
- [ ] bar(a)
- [ ] atm
- [ ] psf
- [ ] g/cm^2
- [ ] dyne/cm^2
- [ ] kgf/cm^2
- [ ] N/mm^2
- [ ] in of H20 @ 60°F
- [ ] ft of H20 @ 60°F
- [ ] mm of Hg
- [ ] mm of Hg (abs)
- [ ] in of Hg @ 32°F
- [ ] in of Hg @ 32°F (abs)

### Speed
- [ ] in/s
- [ ] ft/s
- [ ] ft/min
- [ ] yd/min
- [ ] mph
- [ ] kph
- [ ] m/s
- [ ] m/min
- [ ] cm/s

### Stiffness
- [ ] N/cm
- [ ] N/mm
- [ ] kN/m
- [ ] kN/mm
- [ ] kgf/m
- [ ] kgf/mm
- [ ] lbf/ft
- [ ] lbf/in

### Temperature
- [x] °F
- [x] °C
- [x] °R
- [x] K

### Time
- [x] sec
- [x] min
- [x] hour
- [x] year
- [x] day
- [x] ms
- [x] microsecond
- [x] nanosecond
- [x] week (7 day)
- [x] month (yr / 12)
- [x] decade (10 yr)
- [x] century (100 yr)

### Uniform load
- [ ] lbf/in
- [ ] lbf/ft
- [ ] kip/in
- [ ] kip/ft
- [ ] kgf/m
- [ ] N/m

### Volume
- [ ] cu in
- [ ] quart (dry)
- [ ] cu cm
- [ ] cu ft
- [ ] peck (dry)
- [ ] cu m
- [ ] cu yd
- [ ] bushel (dry)
- [ ] liter
- [ ] pint (dry)
- [ ] cu mm
- [ ] quart (wet)
- [ ] fl oz (wet)
- [ ] gallon (wet)
- [ ] barrel (wet)

### Volumetric Flow rate
- [ ] cu.ft/min
- [ ] cu.ft/hr
- [ ] gal/min
- [ ] gal/hr
- [ ] L/min
- [ ] L/hr
- [ ] cu.m/min
- [ ] cu.m/hr
- [ ] barrel/hr

# Contributing
If you find units within categories that should be included or new categories please fork the repo and submit a pull request.

## TODO:
Allow for user added units if they provide the conversion factor.


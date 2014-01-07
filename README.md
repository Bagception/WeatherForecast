# Weatherforecast Service API

Nutzt die Openweathermap.org API, um eine Wetterabfrage von einer Koordinate (lat,lng) zu generieren.

## Anfrageparameter

lat      Latitude als Double-Wert        mandatory
lng      Longitude als Double-Wert       mandatory
unit     "metric" oder "imperial"        optional

## JSON Antwort

temp      Temperatur in °C               mandatory    
tempMin   min Temperatur in °C           optional    
tempMax   max Temperatur in °C           optional    
wind      Windgeschwindigkeit in km/s    mandatory   
clouds    Bewökung in %                  mandatory   
rain      Regenwahrscheinlichkeit in %   optional    

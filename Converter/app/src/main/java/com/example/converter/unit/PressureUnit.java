package com.example.converter.unit;

import java.util.HashMap;
import java.util.Map;

public class PressureUnit extends Unit {
    public PressureUnit()
    {
        CoeffToStandart = new HashMap<>();
        CoeffToStandart.put("Pascal", 1.0);
        CoeffToStandart.put("Atmosphere", 101325.);
        CoeffToStandart.put("Torr", 133.322);
    }
}
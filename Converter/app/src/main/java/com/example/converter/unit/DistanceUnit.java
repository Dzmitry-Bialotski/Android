package com.example.converter.unit;

import java.util.HashMap;
import java.util.Map;

public class DistanceUnit extends Unit {
    public DistanceUnit()
    {
        CoeffToStandart = new HashMap<>();
        CoeffToStandart.put("Meter", 1.0);
        CoeffToStandart.put("Foot", 0.3048);
        CoeffToStandart.put("Inch", 0.0254);
    }
}

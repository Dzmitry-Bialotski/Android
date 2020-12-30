package com.example.converter.unit;

import java.util.HashMap;
import java.util.Map;

public class WeightUnit extends Unit {
    public WeightUnit()
    {
        CoeffToStandart = new HashMap<>();
        CoeffToStandart.put("Gram", 1.0);
        CoeffToStandart.put("Ounce", 28.3495); //унция
        CoeffToStandart.put("Pound", 453.592); //фунт
    }
}
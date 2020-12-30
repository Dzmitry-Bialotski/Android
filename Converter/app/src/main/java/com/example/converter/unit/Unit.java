package com.example.converter.unit;

import java.util.Map;

public abstract class Unit {
    protected Map<String, Double> CoeffToStandart;
    public double GetCoeff(String str)
    {
        if(CoeffToStandart.containsKey(str))
            return CoeffToStandart.get(str);
        else throw new IllegalArgumentException();
    }
}

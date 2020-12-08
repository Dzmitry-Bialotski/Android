package com.example.converter.unit;

public enum UnitCategory {
    DISTANCE,
    PRESSURE,
    WEIGHT;
    public static Unit GetUnit(UnitCategory category) {
        switch (category)
        {
            case DISTANCE:
                return new DistanceUnit();
            case PRESSURE:
                return new PressureUnit();
            case WEIGHT:
                return new WeightUnit();
            default:
                throw new EnumConstantNotPresentException(UnitCategory.class, category.toString());
        }
    }
}

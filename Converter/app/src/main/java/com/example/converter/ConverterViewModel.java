package com.example.converter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.converter.unit.Unit;
import com.example.converter.unit.UnitCategory;

import java.util.Locale;

public class ConverterViewModel extends ViewModel {

    public MutableLiveData<String> initialValue;
    public MutableLiveData<String> convertedValue;
    public MutableLiveData<String> initialUnit;
    public MutableLiveData<String> convertedUnit;
    public UnitCategory unitCategory;

    public ConverterViewModel() {
        initialValue = new MutableLiveData<>("");
        convertedValue = new MutableLiveData<>("");
        initialUnit = new MutableLiveData<>("Foot");
        convertedUnit = new MutableLiveData<>("Meter");
        unitCategory = UnitCategory.DISTANCE;
    }
    public void SetUnitCategory(UnitCategory unitCategory) {
        this.unitCategory= unitCategory;
        switch (unitCategory) {
            case DISTANCE:
                initialUnit.setValue("Meter");
                convertedUnit.setValue("Foot");
                break;
            case WEIGHT:
                initialUnit.setValue("Gram");
                convertedUnit.setValue("Pound");
                break;
            case PRESSURE:
                initialUnit.setValue("Pascal");
                convertedUnit.setValue("Torr");
        }
    }
    public void Calc() {
        String initialString = initialValue.getValue();
        assert initialString != null;
        if (initialString.endsWith(".")) {
            initialString = initialString.substring(0, initialString.length() - 1);
        }
        double initial = Double.parseDouble(initialString);

        Unit basicConverter = UnitCategory.GetUnit(unitCategory);
        double converted = initial * basicConverter.GetCoeff(initialUnit.getValue()) /
                basicConverter.GetCoeff(convertedUnit.getValue());

        convertedValue.setValue(String.format(Locale.US,"%.4f", converted));
    }
}

package com.example.converter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

public class KeyboardFragment extends Fragment
{
    private ConverterViewModel viewModel;

    public static KeyboardFragment newInstance() {
        return new KeyboardFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.keyboard_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ConverterViewModel.class);

        setDigitButtonClickListener((Button) view.findViewById(R.id.button1), "1");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button2), "2");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button3), "3");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button4), "4");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button5), "5");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button6), "6");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button7), "7");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button8), "8");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button9), "9");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button0), "0");
        setDigitButtonClickListener((Button) view.findViewById(R.id.button_dot), ".");

        Button buttonClear = view.findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.initialValue.setValue("");
                viewModel.convertedValue.setValue("");
            }
        });

    }

    void setDigitButtonClickListener(Button button, final String buttonValue) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MutableLiveData<String> currentVal = viewModel.initialValue;
                if (Objects.requireNonNull(currentVal.getValue()).length() > 10)
                    return;
                currentVal.setValue(currentVal.getValue() + buttonValue);
            }
        });
    }
}
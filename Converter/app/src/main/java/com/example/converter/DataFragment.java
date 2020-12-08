package com.example.converter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

public class DataFragment extends Fragment {
    private ConverterViewModel viewModel;

    private TextView textViewInitial;
    private TextView textViewConverted;

    public static DataFragment newInstance() {
        DataFragment fragment = new DataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ConverterViewModel.class);
        textViewInitial = view.findViewById(R.id.text_initial);
        textViewConverted = view.findViewById(R.id.text_converted);

        final Observer<String> valueInitialObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewInitial.setText(s);
            }
        };

        final Observer<String> valueConvertedObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewConverted.setText(s);
            }
        };

        viewModel.initialValue.observe(getViewLifecycleOwner(), valueInitialObserver);
        viewModel.convertedValue.observe(getViewLifecycleOwner(), valueConvertedObserver);

        Button convertButton = view.findViewById(R.id.button_convert);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(viewModel.initialValue.getValue(), ""))
                    return;
                viewModel.Calc();
            }
        });

        Button changeUnitInitial = view.findViewById(R.id.button_unit_initial);
        Button changeUnitConverted = view.findViewById(R.id.button_unit_converted);
        setChangeUnitButton(changeUnitInitial, viewModel.initialUnit);
        setChangeUnitButton(changeUnitConverted, viewModel.convertedUnit);
    }

    private void setChangeUnitButton (final Button button, final MutableLiveData<String> unit) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), button);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                switch (viewModel.unitCategory) {
                    case DISTANCE:
                        popup.getMenu().setGroupVisible(R.id.group_distance, true);
                        break;
                    case WEIGHT:
                        popup.getMenu().setGroupVisible(R.id.group_weight, true);
                        break;
                    case PRESSURE:
                        popup.getMenu().setGroupVisible(R.id.group_pressure, true);
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        unit.setValue((String) item.getTitle());
                        return true;
                    }
                });
                popup.show();
            }
        });

        final Observer<String> unitObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                button.setText(s);
            }
        };
        unit.observe(getViewLifecycleOwner(), unitObserver);
    }
}

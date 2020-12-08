package com.example.converter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.converter.unit.UnitCategory;

public class MainActivity extends AppCompatActivity {
    private ConverterViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(ConverterViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.converter_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_distance:
                viewModel.SetUnitCategory(UnitCategory.DISTANCE);
                return true;
            case R.id.item_weight:
                viewModel.SetUnitCategory(UnitCategory.WEIGHT);
                return true;
            case R.id.item_pressure:
                viewModel.SetUnitCategory(UnitCategory.PRESSURE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void copyInitialValue(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied text", ((TextView)findViewById(R.id.text_initial)).getText());
        clipboard.setPrimaryClip(clip);
    }

    public void copyConvertedValue(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied text", ((TextView)findViewById(R.id.text_converted)).getText());
        clipboard.setPrimaryClip(clip);
    }

    public void swapValues(View view) {
        String temp = viewModel.convertedValue.getValue();
        viewModel.convertedValue.setValue(viewModel.initialValue.getValue());
        viewModel.initialValue.setValue(temp);
    }
}
package by.belotskiy.tabatatimer.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import by.belotskiy.tabatatimer.R;
import by.belotskiy.tabatatimer.model.SequenceControllerModel;
import by.belotskiy.tabatatimer.model.WorkoutModel;
import by.belotskiy.tabatatimer.activity.SplashScreenActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SequenceAdapter extends ArrayAdapter<SequenceControllerModel> {

    private Context context;
    private ArrayList<SequenceControllerModel> rowElements = new ArrayList<SequenceControllerModel>();
    private WorkoutModel workoutModel;
    private String workingModel;

    public SequenceAdapter(ArrayList<SequenceControllerModel> items, WorkoutModel workoutModel, Context context, String workingMode) {
        super(context, R.layout.create_sequence_list_item, items);
        this.context = context;
        this.rowElements = items;
        this.workoutModel = workoutModel;
        this.workingModel = workingMode;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.create_sequence_list_item, null);
        }

        final SequenceControllerModel rowData = getItem(position);

        if (rowData != null) {
            TextView title = (TextView) view.findViewById(R.id.sequenceItemTitle);
            title.setTextSize(SplashScreenActivity.font_size);
            ImageView image = (ImageView) view.findViewById(R.id.sequenceItemImage);
            final EditText value = (EditText) view.findViewById(R.id.sequenceItemValue);
            value.setTextSize(SplashScreenActivity.font_size);

            if (title != null) {
                title.setText(rowData.getTitle());
            }

            if (value != null) {
                value.setText(rowData.getBaseValue().toString());
            }

            if (image != null) {
                image.setImageResource(rowData.getActivityImage());
            }

            ImageView addValue = (ImageView) view.findViewById(R.id.addValue);
            addValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int number = Integer.parseInt(value.getText().toString());
                    rowData.setBaseValue(number + 1);
                    value.setText(String.valueOf(number + 1));
                }
            });

            ImageView deleteValue = (ImageView) view.findViewById(R.id.deleteValue);
            deleteValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int number = Integer.parseInt(value.getText().toString());
                    rowData.setBaseValue(number - 1);
                    value.setText(String.valueOf(number - 1));
                }
            });

            value.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        rowData.setBaseValue(Integer.parseInt(value.getText().toString()));
                    } catch(Exception ex) {
                        Log.d("Exception", ex.toString());
                    }
                }
            });
        }
        return view;
    }
}

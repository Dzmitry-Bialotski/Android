package by.belotskiy.tabatatimer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import by.belotskiy.tabatatimer.database.DatabaseHelper;
import by.belotskiy.tabatatimer.R;

import java.util.ArrayList;
import java.util.List;

import by.belotskiy.tabatatimer.model.WorkoutModel;
import by.belotskiy.tabatatimer.activity.MainActivity;
import by.belotskiy.tabatatimer.activity.SplashScreenActivity;
import by.belotskiy.tabatatimer.activity.TimerActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

public class CustomAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private List<WorkoutModel> items = new ArrayList<WorkoutModel>();

    public CustomAdapter(List<WorkoutModel> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public WorkoutModel getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.program_list_item, null);
        }

        final WorkoutModel obj = items.get(position);
        if (obj != null) {
            TextView title = (TextView) view.findViewById(R.id.titleTextView);
            TextView description = (TextView) view.findViewById(R.id.descriptionTextView);
            title.setTextSize(SplashScreenActivity.font_size);
            description.setTextSize(SplashScreenActivity.font_size);

            if (title != null) {
                title.setText(obj.getWorkoutName());
            }
            if (description != null) {
                description.setText(obj.getDescription());
            }
            GradientDrawable backgroundGradient = (GradientDrawable)view.getBackground();
            backgroundGradient.setColor(obj.getColor());

            ImageButton startProgram = (ImageButton) view.findViewById(R.id.startProgram);
            startProgram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TimerActivity.class);
                    intent.putExtra("SEQUENCE", obj);
                    context.startActivity(intent);
                }
            });

            ImageView configButton = (ImageView) view.findViewById(R.id.configButton);
            configButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(view, position);
                }
            });
        }


        return view;
    }

    public void showPopup(final View v, final int position) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.first_item:
                        getItem(position).deleteWorkout(MainActivity.workoutDatabase);
                        items.remove(position);
                        notifyDataSetChanged();
                        return true;
                    case R.id.second_item:
                        final WorkoutModel obj = items.get(position);
                        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(context, obj.getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {

                            }

                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                obj.setColor(color);
                                DatabaseHelper dbHelper = new DatabaseHelper(context);
                                obj.updateWorkout(MainActivity.workoutDatabase);
                                notifyDataSetChanged();
                            }
                        });
                        colorPicker.show();
                        return true;
                    case R.id.thirt_item:
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);

                        alert.setTitle("Change workout title");
                        alert.setMessage("Workout title: " + getItem(position).getWorkoutName());

                        final EditText input = new EditText(context);
                        alert.setView(input);

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                WorkoutModel item = getItem(position);
                                item.setWorkoutName(input.getText().toString());
                                DatabaseHelper dbHelper = new DatabaseHelper(context);
                                item.updateWorkout(MainActivity.workoutDatabase);
                                notifyDataSetChanged();
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });

                        alert.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }
}

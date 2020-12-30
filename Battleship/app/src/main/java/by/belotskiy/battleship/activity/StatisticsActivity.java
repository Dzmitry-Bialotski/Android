package by.belotskiy.battleship.activity;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.statistics.StatisticsAdapter;
import by.belotskiy.battleship.statistics.StatisticsItem;
import by.belotskiy.battleship.view_model.StatisticsViewModel;

public class StatisticsActivity extends AppCompatActivity {
    StatisticsViewModel viewModel;
    private List<StatisticsItem> statisticsList;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.NewInstanceFactory())
                .get(StatisticsViewModel.class);
        listView = findViewById(R.id.statistics_list_view);
        statisticsList = new ArrayList<>();

    }

}

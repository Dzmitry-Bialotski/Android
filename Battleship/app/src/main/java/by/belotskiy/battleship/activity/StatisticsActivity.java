package by.belotskiy.battleship.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.statistics.StatisticsAdapter;
import by.belotskiy.battleship.statistics.StatisticsItem;

public class StatisticsActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference statisticsRef;
    private FirebaseAuth mAuth;
    private List<StatisticsItem> statisticsList;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        mAuth = FirebaseAuth.getInstance();
        statisticsRef = database.getReference("statistics").child(mAuth.getUid());
        listView = findViewById(R.id.statistics_list_view);
        statisticsList = new ArrayList<>();
        statisticsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                StatisticsItem statisticsItem = snapshot.getValue(StatisticsItem.class);
                statisticsItem.setTime(snapshot.getKey());
                statisticsList.add(statisticsItem);
                update();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void update(){
        StatisticsAdapter adapter = new StatisticsAdapter(getApplicationContext(), R.layout.item_statistics, statisticsList);
        listView.setAdapter(adapter);
    }
}

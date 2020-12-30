package by.belotskiy.battleship.view_model;

import android.content.Context;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.statistics.StatisticsAdapter;
import by.belotskiy.battleship.statistics.StatisticsItem;

public class StatisticsViewModel extends ViewModel {
    FirebaseDatabase database;
    DatabaseReference statisticsRef;
    private FirebaseAuth mAuth;
    public StatisticsViewModel(){
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        statisticsRef = database.getReference("statistics").child(mAuth.getUid());
    }
    public void AddStatListener(final Context context,final List<StatisticsItem> statisticsList,final ListView listView){
        statisticsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                StatisticsItem statisticsItem = snapshot.getValue(StatisticsItem.class);
                statisticsItem.setTime(snapshot.getKey());
                statisticsList.add(statisticsItem);
                StatisticsAdapter adapter = new StatisticsAdapter(context, R.layout.item_statistics, statisticsList);
                listView.setAdapter(adapter);
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
}

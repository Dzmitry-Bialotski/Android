package by.belotskiy.battleship.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.adapter.MatchDataAdapter;
import by.belotskiy.battleship.data.MatchData;
import by.belotskiy.battleship.receiver.NetworkStateReceiver;
import by.belotskiy.battleship.state_macnine.StateMachine;


public class LobbyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private EditText connectRoomInput;
    private TextView roomIdTextView;
    private boolean isGameStarting;

    public static String host;
    public static String hostRoomId;

    private DatabaseReference roomRef;
    private ValueEventListener statusListener;
    private ValueEventListener onGameLoadedListener;

    private ListView matchesHistory;
    private MatchDataAdapter dataAdapter;
    private static NetworkStateReceiver stateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        if(stateReceiver == null){
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            stateReceiver = new NetworkStateReceiver();
            registerReceiver(stateReceiver, filter);
        }

        matchesHistory = findViewById(R.id.historyView);
        dataAdapter = new MatchDataAdapter(this, new ArrayList<MatchData>());
        matchesHistory.setAdapter(dataAdapter);

        roomIdTextView = findViewById(R.id.roomIdText);
        roomIdTextView.setVisibility(View.INVISIBLE);
        connectRoomInput = findViewById(R.id.roomIdInput);
        Button btn = findViewById(R.id.signOutBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });
        btn = findViewById(R.id.connectRoomBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectRoom();
            }
        });
        btn = findViewById(R.id.createRoomBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateRoom();
            }
        });
        btn = findViewById(R.id.user_settings_button);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ToSettingsActivity();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        isGameStarting = false;
        roomIdTextView.setVisibility(View.INVISIBLE);
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            int roomId = currentUser.getUid().hashCode() % 1000000;
            StateMachine.ToOffline(mReference, currentUser, Integer.toString(roomId));
        }

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot history = dataSnapshot.child("Users").child("History");
                if(history == null)
                    return;

                DataSnapshot userHistory = history.child(currentUser.getUid());
                if(userHistory == null)
                    return;

                ArrayList<MatchData> adapterData = new ArrayList<>();
                Iterable<DataSnapshot> matchData = userHistory.getChildren();
                for (DataSnapshot current: matchData
                ) {
                    Gson gson = new Gson();
                    String jsonString = current.getValue(String.class);
                    adapterData.add(gson.fromJson(jsonString, MatchData.class));
                }

                dataAdapter = new MatchDataAdapter(getBaseContext(), adapterData);
                matchesHistory.setAdapter(dataAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    protected void onStop(){
        if(statusListener != null){
            mReference.removeEventListener(statusListener);
        }
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onConfigurationChanged(newConfig);
    }


    private void CreateRoom() {
        if(!NetworkStateReceiver.IsConnected()) {
            Toast.makeText(LobbyActivity.this, "Internet connection lost",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            return;
        }
        final int roomId = currentUser.getUid().hashCode() % 1000000;
        final String roomIdText = Integer.toString(roomId);
        StateMachine.ToOnline(mReference, mAuth.getCurrentUser(), roomIdText);
        roomIdTextView.setText("Your room id: " + roomIdText);
        roomIdTextView.setVisibility(View.VISIBLE);
        statusListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String roomStatus = dataSnapshot.child("Users").child(roomIdText).child("Status").getValue(String.class);
                if(roomStatus.equals("Filled")){
                    host = currentUser.getUid();
                    hostRoomId = roomIdText;
                    StateMachine.ToFilled(mReference, mAuth.getCurrentUser(), roomIdText);
                    StartGame();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        mReference.addValueEventListener(statusListener);
    }

    private void ConnectRoom() {
        if(!NetworkStateReceiver.IsConnected()) {
            Toast.makeText(LobbyActivity.this, "Internet connection lost",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            return;
        }
        final String roomId = connectRoomInput.getText().toString();
        try {
            int temp = Integer.parseInt(roomId);
        }
        catch (Exception e){
            Toast.makeText(LobbyActivity.this, "Room with this id doesn't exist",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        roomRef = mReference.child("Users").child(roomId);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String roomStatus = dataSnapshot.child("Status").getValue(String.class);
                if(roomStatus == null){
                    Toast.makeText(LobbyActivity.this, "Room with this id doesn't exist",
                            Toast.LENGTH_SHORT).show();

                    return;
                }
                String userId = dataSnapshot.child("OtherPlayer").getValue(String.class);
                if(userId == null){
                    Toast.makeText(LobbyActivity.this, "Room with this id doesn't exist",
                            Toast.LENGTH_SHORT).show();

                    return;
                }
                if(roomStatus.equals("Online") && !userId.equals(currentUser.getUid())){
                    hostRoomId = roomId;
                    host = userId;
                    mReference.child("Users").child(roomId).child("Status").setValue("Filled");
                    onGameLoadedListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String roomStatus = dataSnapshot.child("Status").getValue(String.class);
                            if(roomStatus == null){
                                return;
                            }
                            if(roomStatus.equals("Game")){
                                mReference.child("Users").child(roomId).child("OtherPlayer").setValue(currentUser.getEmail());
                                StartGame();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {  }
                    };
                    roomRef.addValueEventListener(onGameLoadedListener);
                }
                else {
                    if(userId.equals(currentUser.getUid())){
                        Toast.makeText(LobbyActivity.this, "You can't connect to this room",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LobbyActivity.this, "Room is unavailable to connect",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void SignOut() {
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void StartGame() {
        if(isGameStarting){
            return;
        }
        if(onGameLoadedListener != null){
            roomRef.removeEventListener(onGameLoadedListener);
        }
        isGameStarting = true;
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
    private void ToSettingsActivity(){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
        finish();
    }

}

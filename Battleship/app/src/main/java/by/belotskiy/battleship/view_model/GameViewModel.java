package by.belotskiy.battleship.view_model;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import by.belotskiy.battleship.game.Cell;
import by.belotskiy.battleship.game.Field;
import by.belotskiy.battleship.game.Ship;
import by.belotskiy.battleship.game.enums.CellType;
import by.belotskiy.battleship.game.enums.FieldType;
import by.belotskiy.battleship.game.service.FleetService;

public class GameViewModel extends ViewModel {
    public final String WIN = "Win";
    public final String LOSE = "Lose";
    public final String CURRENT_PLAYER = "CurrentPlayer";
    public FirebaseDatabase database;
    public DatabaseReference roomRef;
    public DatabaseReference profileRef;
    public DatabaseReference statisticsRef;
    public FirebaseAuth mAuth;
    public FleetService fleetService;
    public String playerId;
    public String opponentId;
    ChildEventListener opponentFieldListener;
    ChildEventListener myFieldListener;
    ChildEventListener childEventListener;
    ValueEventListener currentPlayerListener;
    ChildEventListener opponentProfileListener;

    public GameViewModel(String roomId){
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        roomRef = database.getReference("rooms").child(roomId);
        profileRef = database.getReference("profiles");
        statisticsRef = database.getReference("statistics").child(mAuth.getUid());
        fleetService = new FleetService();
    }
    public void saveStatistics(String result, String userName, String opponentName){
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss-dd-MM-yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        statisticsRef = statisticsRef.child(dateText);
        statisticsRef.child("result").setValue(result);
        statisticsRef.child("curUsername").setValue(userName);
        statisticsRef.child("opponentUsername").setValue(opponentName);
    }
    public void setCell(String key, CellType cellType, Field field){
        int i = Integer.parseInt(String.valueOf(key.charAt(0)));
        int j = Integer.parseInt(String.valueOf(key.charAt(1)));
        field.setCell(i, j, cellType);
        if(cellType == CellType.SUNK){
            FleetService fleetService = new FleetService();
            Ship ship = fleetService.findShip(i, j, field.getFleet());
            ship.setCell(new Cell(i, j, cellType));
        }
    }
    public void initFieldListeners(final Field opponentField, final Field myField,final Context context) {
        opponentFieldListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                setCell(snapshot.getKey(), CellType.valueOf(snapshot.getValue().toString()), opponentField);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                setCell(snapshot.getKey(), CellType.valueOf(snapshot.getValue().toString()), opponentField);
                if (fleetService.fleetIsSunk(myField.getFleet()) || fleetService.fleetIsSunk(opponentField.getFleet())) {
                    roomRef.child(CURRENT_PLAYER).setValue("Stop");
                    return;
                }
                if (opponentField.shipIsSunk) {
                    Ship ship = opponentField.lastSunkShip;
                    ArrayList<Cell> borderCells = new FleetService().findBorderCells(ship, opponentField);
                    for (Cell cell : borderCells) {
                        roomRef.child(opponentId).child("field").child(String.valueOf(cell.getX()) + String.valueOf(cell.getY()))
                                .setValue(cell.getCellType().toString());
                    }
                }
                if (!(snapshot.getValue().toString().equals(CellType.SUNK.toString())) && !(opponentField.shipIsSunk)) {
                    roomRef.child(CURRENT_PLAYER).setValue(opponentId);
                } else {
                    roomRef.child(CURRENT_PLAYER).setValue(playerId);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "opponent field error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        myFieldListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                setCell(snapshot.getKey(), CellType.valueOf(snapshot.getValue().toString()), myField);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                setCell(snapshot.getKey(), CellType.valueOf(snapshot.getValue().toString()), myField);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "my field error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }
    public void initCurPlayerListener(final Field opponentField, final Context context, final TextView user_name_tv, final TextView opponent_name_tv) {
        currentPlayerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals(mAuth.getUid())) {
                    opponentField.setFieldType(FieldType.OPPONENT_FIELD);
                    //Toast.makeText(GameActivity.this, "Your turn", Toast.LENGTH_SHORT).show();
                } else {
                    opponentField.setFieldType(FieldType.BLOCKED);
                }
                if (snapshot.getValue().toString().equals("Stop")) {
                    FleetService fleetService = new FleetService();
                    if (fleetService.fleetIsSunk(opponentField.getFleet())) {
                        Toast.makeText(context, "You win!!", Toast.LENGTH_SHORT).show();
                        saveStatistics(WIN,user_name_tv.getText().toString(), opponent_name_tv.getText().toString());
                    } else {
                        Toast.makeText(context, "You lose :(", Toast.LENGTH_SHORT).show();
                        saveStatistics(LOSE,user_name_tv.getText().toString(), opponent_name_tv.getText().toString());
                    }
                    roomRef.child(playerId).child("field").removeEventListener(myFieldListener);
                    roomRef.child(opponentId).child("field").removeEventListener(opponentFieldListener);
                    roomRef.child(CURRENT_PLAYER).removeEventListener(currentPlayerListener);
                    roomRef.removeEventListener(childEventListener);
                    roomRef.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Current player error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }
    public void initMyProfileListener(final ImageView user_avatar_view, final TextView user_name_tv) {
        profileRef = FirebaseDatabase.getInstance().getReference("profiles");
        profileRef.child(mAuth.getUid()).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals("image")) {
                    Picasso.get().load(snapshot.getValue().toString()).into(user_avatar_view);
                }
                if (snapshot.getKey().equals("username")) {
                    user_name_tv.setText(snapshot.getValue().toString());
                }
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
    public void initOpponentProfileListener(final ImageView opponent_avatar_view, final TextView opponent_name_tv){
        opponentProfileListener = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals("image")){
                    Picasso.get().load(snapshot.getValue().toString()).into(opponent_avatar_view);
                }
                if (snapshot.getKey().equals("username")){
                    opponent_name_tv.setText(snapshot.getValue().toString());
                }
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
        };
    }

    public void AddChildListeners(final Context context){

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                addListeners(key);
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
                Toast.makeText(context, "child error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        roomRef.addChildEventListener(childEventListener);
    }
    private void addListeners(String key){
        if(!key.equals(CURRENT_PLAYER)){
            if(key.equals(mAuth.getUid())){
                playerId = key;
                roomRef.child(playerId).child("field").addChildEventListener(myFieldListener);
            }else{
                opponentId = key;
                roomRef.child(opponentId).child("field").addChildEventListener(opponentFieldListener);
                profileRef.child(opponentId).addChildEventListener(opponentProfileListener);
            }
        }
        if(playerId != null && opponentId != null){
            roomRef.child(CURRENT_PLAYER).addValueEventListener(currentPlayerListener);
        }
    }
}

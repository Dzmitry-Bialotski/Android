package by.belotskiy.battleship.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.game.Cell;
import by.belotskiy.battleship.game.Field;
import by.belotskiy.battleship.game.Fleet;
import by.belotskiy.battleship.game.Ship;
import by.belotskiy.battleship.game.enums.CellType;
import by.belotskiy.battleship.game.enums.FieldType;
import by.belotskiy.battleship.game.service.FleetService;

public class GameActivity extends AppCompatActivity {
    private final String WIN = "Win";
    private final String LOSE = "Lose";
    private final String CURRENT_PLAYER = "CurrentPlayer";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference roomRef;
    private DatabaseReference profileRef;
    private DatabaseReference statisticsRef;
    private FirebaseAuth mAuth;
    private Field myField;
    private Field opponentField;
    private String playerId;
    private String opponentId;

    ImageView user_avatar_view;
    ImageView opponent_avatar_view;
    TextView user_name_tv;
    TextView opponent_name_tv;
    ChildEventListener opponentFieldListener;
    ChildEventListener myFieldListener;
    ChildEventListener childEventListener;
    ValueEventListener currentPlayerListener;
    ChildEventListener opponentProfileListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle bundle = getIntent().getExtras();
        String roomId = bundle.getString("roomId");
        myField = (Field) findViewById(R.id.field_mine);
        myField.setFieldType(FieldType.MY_FIELD);
        opponentField = (Field) findViewById(R.id.field_opponent);
        opponentField.setFieldType(FieldType.OPPONENT_FIELD);
        mAuth = FirebaseAuth.getInstance();
        roomRef = database.getReference("rooms").child(roomId);
        profileRef = database.getReference("profiles");
        statisticsRef = database.getReference("statistics").child(mAuth.getUid());
        FleetService fleetService = new FleetService();
        user_avatar_view = findViewById(R.id.image_mine);
        opponent_avatar_view = findViewById(R.id.image_opponent);
        user_name_tv = findViewById(R.id.username_mine);
        opponent_name_tv = findViewById(R.id.username_opponent);;

        opponentField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opponentField.getFieldType() != FieldType.BLOCKED) {
                    Cell cell = opponentField.getCurrentCell();
                    if (!opponentField.isAlreadyChecked) {
                        roomRef.child(opponentId).child("field").child(String.valueOf(cell.getX()) + String.valueOf(cell.getY()))
                                .setValue(cell.getCellType().toString());
                    }
                }
            }
        });
        myField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        opponentFieldListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                setCell(snapshot.getKey(), CellType.valueOf(snapshot.getValue().toString()), opponentField);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                setCell(snapshot.getKey(), CellType.valueOf(snapshot.getValue().toString()), opponentField);
                FleetService fleetService = new FleetService();
                if(fleetService.fleetIsSunk(myField.getFleet()) || fleetService.fleetIsSunk(opponentField.getFleet())){
                    roomRef.child(CURRENT_PLAYER).setValue("Stop");
                    return;
                }
                if(opponentField.shipIsSunk){
                    Ship ship = opponentField.lastSunkShip;
                    ArrayList<Cell> borderCells = new FleetService().findBorderCells(ship, opponentField);
                    for(Cell cell : borderCells){
                        roomRef.child(opponentId).child("field").child(String.valueOf(cell.getX()) + String.valueOf(cell.getY()))
                                .setValue(cell.getCellType().toString());
                    }
                }
                if (!(snapshot.getValue().toString().equals(CellType.SUNK.toString()))  && !(opponentField.shipIsSunk)  ){
                    roomRef.child(CURRENT_PLAYER).setValue(opponentId);
                }else{
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
                Toast.makeText(getApplicationContext(), "opponent field error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "my field error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        currentPlayerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals(mAuth.getUid())){
                    opponentField.setFieldType(FieldType.OPPONENT_FIELD);
                    //Toast.makeText(GameActivity.this, "Your turn", Toast.LENGTH_SHORT).show();
                } else{
                    opponentField.setFieldType(FieldType.BLOCKED);
                }
                if(snapshot.getValue().toString().equals("Stop")){
                    FleetService fleetService = new FleetService();
                    if(fleetService.fleetIsSunk(opponentField.getFleet())){
                        Toast.makeText(GameActivity.this, "You win!!", Toast.LENGTH_SHORT).show();
                        saveStatistics(WIN);
                    } else{
                        Toast.makeText(GameActivity.this, "You lose :(", Toast.LENGTH_SHORT).show();
                        saveStatistics(LOSE);
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
                Toast.makeText(getApplicationContext(), "Current player error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
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
                Toast.makeText(getApplicationContext(), "child error " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        roomRef.addChildEventListener(childEventListener);
        //
        profileRef = FirebaseDatabase.getInstance().getReference("profiles");
        profileRef.child(mAuth.getUid()).addChildEventListener(new ChildEventListener(){

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals("image")){
                    Picasso.get().load(snapshot.getValue().toString()).into(user_avatar_view);
                }
                if (snapshot.getKey().equals("username")){
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
        //profileRef.child(opponentId).addChildEventListener(new ChildEventListener(){
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
    private void saveStatistics(String result){
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH-mm-ss-dd-MM-yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        statisticsRef = statisticsRef.child(dateText);
        statisticsRef.child("result").setValue(result);
        statisticsRef.child("curUsername").setValue(user_name_tv.getText().toString());
        statisticsRef.child("opponentUsername").setValue(opponent_name_tv.getText().toString());
    }
    private void setCell(String key, CellType cellType, Field field){
        int i = Integer.parseInt(String.valueOf(key.charAt(0)));
        int j = Integer.parseInt(String.valueOf(key.charAt(1)));
        field.setCell(i, j, cellType);
        if(cellType == CellType.SUNK){
            FleetService fleetService = new FleetService();
            Ship ship = fleetService.findShip(i, j, field.getFleet());
            ship.setCell(new Cell(i, j, cellType));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FleetService fleetService= new FleetService();
        myField.setFleet(fleetService.createFleet(myField));
        opponentField.setFleet(fleetService.createFleet(opponentField));
    }

}

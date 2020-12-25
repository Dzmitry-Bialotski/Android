package by.belotskiy.battleship.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.game.Field;
import by.belotskiy.battleship.game.Fleet;
import by.belotskiy.battleship.game.enums.FieldType;
import by.belotskiy.battleship.game.service.FleetService;
import by.belotskiy.battleship.util.IdGenerator;

public class RoomActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private Field field;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //database.getReference("rooms").removeValue();
        field = (Field) findViewById(R.id.field_create);
        field.setFieldType(FieldType.CREATE_FIELD);
        field.generateFleet();
        Button create_btn = findViewById(R.id.create_button);
        Button connect_btn = findViewById(R.id.connect_button);
        Button play_btn = findViewById(R.id.start_playing_button);
        final EditText roomId_et = findViewById(R.id.write_room_id_edit_text);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomId = IdGenerator.generateRoomId();
                roomId_et.setText(roomId);
                saveRoomToDb(roomId);
            }
        });
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomId = roomId_et.getText().toString();
                saveConnectedUserToDb(roomId);
            }
        });
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FleetService fleetService = new FleetService();
                Fleet fleet = fleetService.createFleetIfLegal(field);
                if(fleet != null){
                    Toast.makeText(getApplicationContext(), "Fleet is nice", Toast.LENGTH_SHORT).show();
                    String roomId = roomId_et.getText().toString();
                    saveFieldToDb(field, roomId);
                    Intent intent = new Intent(getApplicationContext() , GameActivity.class);
                    intent.putExtra("roomId", roomId);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Fleet is bad!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void saveRoomToDb(String roomId){
        DatabaseReference roomReference = database.getReference("rooms").child(roomId);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        roomReference.setValue(currentUser.getUid());
    }
    private void saveConnectedUserToDb(String roomId){
        DatabaseReference roomReference = database.getReference("rooms").child(roomId);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        roomReference.setValue(currentUser.getUid());
    }
    private void saveFieldToDb(Field field, String roomId){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference userFieldReference = database.getReference("rooms")
                                                .child(roomId).child(currentUser.getUid()).child("field");
        for(int i = 0; i < field.getSize(); i++) {
            for(int j = 0; j < field.getSize(); j++){
                userFieldReference.child(String.valueOf(i) + String.valueOf(j)).setValue(field.getCells()[i][j]);
            }
        }
        database.getReference("rooms")
                .child(roomId).child("CurrentPlayer").setValue(mAuth.getCurrentUser().getUid());
    }
}

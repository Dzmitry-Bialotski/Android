package by.belotskiy.battleship.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.game.Field;
import by.belotskiy.battleship.game.Fleet;
import by.belotskiy.battleship.game.enums.FieldType;
import by.belotskiy.battleship.game.service.FleetService;
import by.belotskiy.battleship.util.IdGenerator;
import by.belotskiy.battleship.view_model.RoomViewModel;

public class RoomActivity extends AppCompatActivity {
    RoomViewModel viewModel;
    private Field field;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.NewInstanceFactory())
                .get(RoomViewModel.class);
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
                viewModel.saveRoomToDb(roomId);
            }
        });
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomId = roomId_et.getText().toString();
                viewModel.saveConnectedUserToDb(roomId);
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
                    viewModel.saveFieldToDb(field, roomId);
                    Intent intent = new Intent(getApplicationContext() , GameActivity.class);
                    intent.putExtra("roomId", roomId);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Fleet is bad!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

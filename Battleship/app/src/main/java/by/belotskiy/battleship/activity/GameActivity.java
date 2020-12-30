package by.belotskiy.battleship.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.game.Field;
import by.belotskiy.battleship.game.enums.FieldType;
import by.belotskiy.battleship.game.service.FleetService;
import by.belotskiy.battleship.view_model.GameViewModel;
import by.belotskiy.battleship.view_model.factory.GameFactory;

public class GameActivity extends AppCompatActivity {
    private Field myField;
    private Field opponentField;
    GameViewModel viewModel;
    ImageView user_avatar_view;
    ImageView opponent_avatar_view;
    TextView user_name_tv;
    TextView opponent_name_tv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle bundle = getIntent().getExtras();
        String roomId = bundle.getString("roomId");
        viewModel = ViewModelProviders.of(this, new GameFactory(roomId))
                .get(GameViewModel.class);
        myField = (Field) findViewById(R.id.field_mine);
        myField.setFieldType(FieldType.MY_FIELD);
        opponentField = (Field) findViewById(R.id.field_opponent);
        opponentField.setFieldType(FieldType.OPPONENT_FIELD);
        user_avatar_view = findViewById(R.id.image_mine);
        opponent_avatar_view = findViewById(R.id.image_opponent);
        user_name_tv = findViewById(R.id.username_mine);
        opponent_name_tv = findViewById(R.id.username_opponent);;

        opponentField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opponentField.updateValue(viewModel);
            }
        });
        myField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        viewModel.initFieldListeners(opponentField, myField,getApplicationContext());
        viewModel.initCurPlayerListener(opponentField, getApplicationContext(),user_name_tv, opponent_name_tv);
        viewModel.initMyProfileListener(user_avatar_view, user_name_tv);
        viewModel.initOpponentProfileListener(opponent_avatar_view, opponent_name_tv);
        viewModel.AddChildListeners(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FleetService fleetService = new FleetService();
        myField.setFleet(fleetService.createFleet(myField));
        opponentField.setFleet(fleetService.createFleet(opponentField));
    }

}

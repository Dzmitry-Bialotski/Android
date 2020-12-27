package by.belotskiy.battleship.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.view_model.LoginViewModel;


public class LoginActivity extends AppCompatActivity {
    LoginViewModel viewModel;
    private EditText email_et;
    private EditText password_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.NewInstanceFactory())
                .get(LoginViewModel.class);
        Button login_btn = findViewById(R.id.login_button);
        Button register_btn = findViewById(R.id.register_button);
        email_et = findViewById(R.id.login_edit_text);
        password_et = findViewById(R.id.password_edit_text);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.login(email_et.getText().toString(), password_et.getText().toString(), getApplicationContext());
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.register(email_et.getText().toString(), password_et.getText().toString(), getApplicationContext());
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        if (viewModel.currentUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}

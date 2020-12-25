package by.belotskiy.battleship.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Toast;

import by.belotskiy.battleship.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String WRITE_EMAIL_MESSAGE = "Write email and password, please";
    private static final String INCORRECT_INPUT_MESSAGE = "Email or password is incorrect";
    private static final String REGISTRATION_COMPLETED_MESSAGE = "Registration completed. Login, please";
    private static final String REGISTRATION_ERROR_MESSAGE = "Something went wrong...";
    private FirebaseAuth mAuth;
    private EditText email_et;
    private EditText password_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        Button login_btn = findViewById(R.id.login_button);
        Button register_btn = findViewById(R.id.register_button);
        email_et = findViewById(R.id.login_edit_text);
        password_et = findViewById(R.id.password_edit_text);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void Login() {
        String email = email_et.getText().toString();
        String password = password_et.getText().toString();
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, INCORRECT_INPUT_MESSAGE, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(LoginActivity.this, WRITE_EMAIL_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    private void Register() {
        String email = email_et.getText().toString();
        String password = password_et.getText().toString();
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, REGISTRATION_COMPLETED_MESSAGE, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, REGISTRATION_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(LoginActivity.this, WRITE_EMAIL_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }
}

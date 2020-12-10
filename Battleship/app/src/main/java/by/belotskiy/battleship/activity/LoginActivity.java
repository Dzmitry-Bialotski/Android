package by.belotskiy.battleship.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.receiver.NetworkStateReceiver;

public class LoginActivity extends AppCompatActivity {
    private static NetworkStateReceiver stateReceiver;
    private FirebaseAuth mAuth;
    private EditText loginInput;
    private EditText passwordInput;
    private EditText userInput;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        loginInput = findViewById(R.id.loginInput);
        userInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.passwordInput);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button btn = findViewById(R.id.signUpBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });
        btn = findViewById(R.id.signInBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(stateReceiver == null){
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            stateReceiver = new NetworkStateReceiver();
            registerReceiver(stateReceiver, filter);
        }
        UpdateUI();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onConfigurationChanged(newConfig);
    }


    public void UpdateUI(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Intent intent = new Intent(this, LobbyActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private String ValidateEmail(String email){
        if(email.isEmpty()){
            return "Input e-mail";
        }
        Pattern emailPattern = Pattern.compile("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        Matcher matcher = emailPattern.matcher(email);
        if(!matcher.matches()) {
            return "Invalid e-mail";
        }
        return null;
    }

    private String ValidatePassword(String password) {
        if(password.isEmpty()){
            return "Input e-mail";
        }
        Pattern lengthPattern = Pattern.compile("(?=^.{8,}$).*$");
        Matcher matcher = lengthPattern.matcher(password);
        if(!matcher.matches()) {
            return "Password must have at least 8 symbols";
        }
        Pattern digitPattern = Pattern.compile("(?=.*\\d).*$");
        matcher = digitPattern.matcher(password);
        if(!matcher.matches()) {
            return "Password must have at least one digit";
        }
        Pattern uppercasePattern = Pattern.compile("(?=.*[A-Z]).*$");
        matcher = uppercasePattern.matcher(password);
        if(!matcher.matches()) {
            return "Password must have at least one uppercase letter";
        }
        Pattern lowercasePattern = Pattern.compile("(?=.*[a-z]).*$");
        matcher = lowercasePattern.matcher(password);
        if(!matcher.matches()) {
            return "Password must have at least one lowercase letter";
        }
        return null;
    }

    private void SignIn() {
        if(!NetworkStateReceiver.IsConnected()) {
            Toast.makeText(LoginActivity.this, "Internet connection lost",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        final String email = loginInput.getText().toString();
        String password = passwordInput.getText().toString();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Input credentials",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UpdateUI();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid e-mail or/and password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SignUp() {
        if(!NetworkStateReceiver.IsConnected()) {
            Toast.makeText(LoginActivity.this, "Internet connection lost",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        final String email = loginInput.getText().toString();
        String validateResult = ValidateEmail(email);
        if(validateResult != null){
            Toast.makeText(LoginActivity.this, validateResult,
                    Toast.LENGTH_SHORT).show();

            return;
        }
        String password = passwordInput.getText().toString();
        validateResult = ValidatePassword(password);
        if(validateResult != null){
            Toast.makeText(LoginActivity.this, validateResult,
                    Toast.LENGTH_SHORT).show();

            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String username = userInput.getText().toString();
                            mReference.child("Users").child(email).child("username").setValue(username);
                            UpdateUI();
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

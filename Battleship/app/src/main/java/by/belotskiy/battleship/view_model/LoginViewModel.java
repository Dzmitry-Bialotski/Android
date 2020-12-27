package by.belotskiy.battleship.view_model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import by.belotskiy.battleship.activity.MainActivity;

public class LoginViewModel extends ViewModel {
    private static final String WRITE_EMAIL_MESSAGE = "Write email and password, please";
    private static final String INCORRECT_INPUT_MESSAGE = "Email or password is incorrect";
    private static final String REGISTRATION_COMPLETED_MESSAGE = "Registration completed. Login, please";
    private static final String REGISTRATION_ERROR_MESSAGE = "Something went wrong...";
    private FirebaseAuth mAuth;
    public FirebaseUser currentUser;
    public LoginViewModel(){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }
    public void login(String email, String password, final Context context) {
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, INCORRECT_INPUT_MESSAGE, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(context, WRITE_EMAIL_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    public void register(String email, String password,final Context context) {
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, REGISTRATION_COMPLETED_MESSAGE, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, REGISTRATION_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(context, WRITE_EMAIL_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }
}

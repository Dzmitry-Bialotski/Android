package by.belotskiy.battleship.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarRating;

import by.belotskiy.battleship.R;

public class UserActivity extends AppCompatActivity {
    private static final int PICK_PHOTO_FOR_AVATAR = 228;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private EditText userInput;
    private ImageView imageView;
    final Context context = this;
    private String image_path;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        userInput = findViewById(R.id.username_input);
        imageView = findViewById(R.id.avatar);
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        Button btn = findViewById(R.id.user_settings_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNickName();
            }
        });
        btn = findViewById(R.id.use_gravatar_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAvatar();
            }
        });
        btn = findViewById(R.id.change_avatar_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        ImageButton img_btn = findViewById(R.id.backButton);
        img_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ToLobbyActivity();
            }
        });
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot avatar = dataSnapshot.child("Users").child("image_path");
                if(avatar.getValue() == null){
                    Gravatar gravatar = new Gravatar().setSize(100).setRating(GravatarRating.GENERAL_AUDIENCES)
                            .setDefaultImage(Gravatar.DEFAULT_DEFAULT_IMAGE);
                    String url = gravatar.getUrl(currentUser.getEmail());
                    try{
                        Picasso.with(context).load(url).into(imageView);
                    }
                    catch(Exception e){
                        Toast.makeText(getApplicationContext(), "No Such gravatar profile or image doesn't exist!", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    image_path = avatar.getValue().toString();
                }
                DataSnapshot username = dataSnapshot.child("Users").child("username");
                ((TextView)findViewById(R.id.username_label)).setText(username.getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        if(image_path != null){
            //загрузка по пути
            Bitmap bitmap = BitmapFactory.decodeFile(image_path);
            imageView.setImageBitmap(bitmap);
        }
        else{
            Gravatar gravatar = new Gravatar().setSize(100).setRating(GravatarRating.GENERAL_AUDIENCES)
                    .setDefaultImage(Gravatar.DEFAULT_DEFAULT_IMAGE);
            String url = gravatar.getUrl(currentUser.getEmail());
            try{
                Picasso.with(this).load(url).into(imageView);
            }
            catch(Exception e){
                Toast.makeText(getApplicationContext(), "No Such gravatar profile or image doesn't exist!!", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imageView.setImageBitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                final FirebaseUser currentUser = mAuth.getCurrentUser();
                                final String email = currentUser.getEmail();
                                mReference.child("Users").child(email).child("image_path").setValue(picturePath);
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }
    private void changeNickName(){
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            final String email = currentUser.getEmail();
            String username = userInput.getText().toString();
            mReference.child("Users").child(email).child("username").setValue(username);
        }
    }
    private void deleteAvatar(){
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            final String email = currentUser.getEmail();
            mReference.child("Users").child(email).child("image_path").setValue(null);
        }
    }

    private void ToLobbyActivity(){
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
        finish();
    }
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }
}

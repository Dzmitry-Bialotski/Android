package by.belotskiy.battleship.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import by.belotskiy.battleship.R;

public class ProfileActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 1;
    private static final String WRITE_NAME_MESSAGE = "Write username";
    private static final String CHANGES_SAVED_MESSAGE = "Changes saved";
    private static final String IMAGE_LOADED_MESSAGE = "Image loaded";
    private static final String CHOOSE_FILE_MESSAGE = "Choose file";
    private static final String LOAD_IMAGE_MESSAGE = "Image is not loaded yet";
    private FirebaseAuth mAuth;

    private ImageView img_view;
    private EditText username_et;
    private Uri imageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username_et = findViewById(R.id.name_edit_text);
        img_view = findViewById(R.id.user_image);
        Button choose_btn = findViewById(R.id.choose_image_button);
        Button upload_btn = findViewById(R.id.upload_image_button);
        Button save_btn = findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("profiles");

        choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ProfileActivity.this, LOAD_IMAGE_MESSAGE, Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username_et.getText().toString().trim();
                if (name.equals("")){
                    Toast.makeText(ProfileActivity.this, WRITE_NAME_MESSAGE, Toast.LENGTH_SHORT).show();
                }
                else{
                    mDatabaseRef.child(mAuth.getUid()).child("username").setValue(name);
                    Toast.makeText(ProfileActivity.this, CHANGES_SAVED_MESSAGE, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDatabaseRef.child(mAuth.getUid()).addChildEventListener(new ChildEventListener(){

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals("image")){
                    Picasso.get().load(snapshot.getValue().toString()).into(img_view);
                }
                if (snapshot.getKey().equals("username")){
                    username_et.setText(snapshot.getValue().toString());
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
    private void uploadFile(){
        if(imageUri != null){
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            StorageReference tempRef = mStorageRef.child(System.currentTimeMillis() + "." + mime.getExtensionFromMimeType(cR.getType(imageUri)));
            mUploadTask = tempRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileActivity.this, IMAGE_LOADED_MESSAGE, Toast.LENGTH_LONG).show();
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String upload = task.getResult().toString();
                                    mDatabaseRef.child(mAuth.getUid()).child("image").setValue(upload);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
        else {
            Toast.makeText(this, CHOOSE_FILE_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(img_view);
        }
    }
}
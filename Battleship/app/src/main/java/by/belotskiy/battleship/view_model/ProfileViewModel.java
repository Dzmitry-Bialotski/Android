package by.belotskiy.battleship.view_model;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

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


public class ProfileViewModel extends ViewModel {
    public static final int IMAGE_REQUEST = 1;
    private static final String WRITE_NAME_MESSAGE = "Write username";
    private static final String CHANGES_SAVED_MESSAGE = "Changes saved";
    private static final String IMAGE_LOADED_MESSAGE = "Image loaded";
    private static final String CHOOSE_FILE_MESSAGE = "Choose file";
    private static final String LOAD_IMAGE_MESSAGE = "Image is not loaded yet";

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    public Uri imageUri;
    public ProfileViewModel(){
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("profiles");
    }
    public void uploadFile(final Context context){
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(context, LOAD_IMAGE_MESSAGE, Toast.LENGTH_SHORT).show();
        } else {
            if(imageUri != null){
                ContentResolver cR = context.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                StorageReference tempRef = mStorageRef.child(System.currentTimeMillis() + "." + mime.getExtensionFromMimeType(cR.getType(imageUri)));
                mUploadTask = tempRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(context, IMAGE_LOADED_MESSAGE, Toast.LENGTH_LONG).show();
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
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(context, CHOOSE_FILE_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void saveName(String name, Context context){
        if (name.equals("")){
            Toast.makeText(context, WRITE_NAME_MESSAGE, Toast.LENGTH_SHORT).show();
        }
        else{
            mDatabaseRef.child(mAuth.getUid()).child("username").setValue(name);
            Toast.makeText(context, CHANGES_SAVED_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }
    public void addChildListener(final ImageView img_view,final EditText username_et){
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
}

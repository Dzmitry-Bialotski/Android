package by.belotskiy.battleship.view_model;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import by.belotskiy.battleship.game.Field;

public class RoomViewModel extends ViewModel {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    public  RoomViewModel(){
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }
    public void saveRoomToDb(String roomId){
        DatabaseReference roomReference = database.getReference("rooms").child(roomId);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        roomReference.setValue(currentUser.getUid());
    }
    public void saveConnectedUserToDb(String roomId){
        DatabaseReference roomReference = database.getReference("rooms").child(roomId);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        roomReference.setValue(currentUser.getUid());
    }
    public void saveFieldToDb(Field field, String roomId){
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

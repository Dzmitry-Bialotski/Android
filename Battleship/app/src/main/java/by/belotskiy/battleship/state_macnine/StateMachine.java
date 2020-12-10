package by.belotskiy.battleship.state_macnine;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import by.belotskiy.battleship.activity.LobbyActivity;

public class StateMachine {
    private StateEnum previousState;
    private StateEnum currentState;

    private static StateMachine instance;
    private static StateMachine getInstance(){
        if(instance == null)
        {
            instance = new StateMachine();
        }

        return instance;
    }


    public static void ToOffline(DatabaseReference mReference, FirebaseUser currentUser, String roomId){
        if(getInstance().currentState == StateEnum.Online){
            mReference.child("Users").child(roomId).child("Status").setValue("Offline");
            mReference.child("Users").child(roomId).child("OtherPlayer").setValue(null);
            getInstance().currentState = StateEnum.Offline;
        }
        else if(getInstance().currentState == StateEnum.Game){
            mReference.child("Users").child(roomId).child("Status").setValue("Offline");
            mReference.child("Users").child(roomId).child("OtherPlayer").setValue(null);
            getInstance().currentState = StateEnum.Offline;
        }
    }

    public static void ToOnline(DatabaseReference mReference, FirebaseUser currentUser, String roomId){
        if(getInstance().currentState == StateEnum.Offline){
            mReference.child("Users").child(roomId).child("Status").setValue("Online");
            mReference.child("Users").child(roomId).child("OtherPlayer").setValue(currentUser.getUid());
            getInstance().currentState = StateEnum.Online;
        }
    }

    public static void ToFilled(DatabaseReference mReference, FirebaseUser currentUser, String roomId){
        if(getInstance().currentState == StateEnum.Online){
            mReference.child("Users").child(roomId).child("host").setValue(currentUser.getUid());
            getInstance().currentState = StateEnum.Filled;
        }
    }

    public static void ToGame(DatabaseReference mReference, FirebaseUser currentUser, String roomId){
        if(getInstance().currentState == StateEnum.Filled){
            mReference.child("Users").child(roomId).child("Status").setValue("Game");
            mReference.child("Users").child(roomId).child("GameStatus").setValue("Progress");
            mReference.child("Users").child(roomId).child("CurrentMove").setValue("0");
            mReference.child("Users").child(roomId).child("NumberMove").setValue("0");
            mReference.child("Users").child(LobbyActivity.hostRoomId).child("Type").setValue("Waiting");
            getInstance().currentState = StateEnum.Game;
        }
    }


    private StateMachine(){
        previousState = StateEnum.Offline;
        currentState = StateEnum.Offline;
    }
}

package by.belotskiy.battleship.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import by.belotskiy.battleship.R;
import by.belotskiy.battleship.adapter.TileAdapter;
import by.belotskiy.battleship.data.MatchData;
import by.belotskiy.battleship.game_logic.Board;
import by.belotskiy.battleship.game_logic.Cell;
import by.belotskiy.battleship.game_logic.Tile;
import by.belotskiy.battleship.state_macnine.StateMachine;

public class GameActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private boolean isHost;
    private String currentUid;
    private boolean isGameStarted;
    private boolean isInProgress;
    private boolean isFinished;
    private String currentMove;
    private boolean isUserTurn;
    private boolean isWaitingForSync;
    private boolean isMoveAllowed;

    private Board userBoard;
    private Board enemyBoard;

    private GridView userGrid;
    private GridView enemyGrid;
    private TileAdapter userAdapter;
    private TileAdapter enemyAdapter;

    private ValueEventListener statusListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        GenerateShips();
        userGrid = (GridView) findViewById(R.id.userGrid);
        userAdapter = new TileAdapter(getApplicationContext(), userBoard, 70);
        userGrid.setAdapter(userAdapter);

        enemyGrid = (GridView) findViewById(R.id.enemyGrid);
        enemyAdapter = new TileAdapter(getApplicationContext(), enemyBoard, 70);
        enemyGrid.setAdapter(enemyAdapter);
        enemyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(isUserTurn && isInProgress && isMoveAllowed){
                    int size = userBoard.getBoardSize();
                    int row = position / size;
                    int col = position % size;
                    Cell cell = new Cell(row, col);
                    PlayTile(cell);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if(newConfig.orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        super.onStart();
        isInProgress = false;
        isGameStarted = false;
        isFinished = false;
        isWaitingForSync = false;
        isMoveAllowed = true;
        currentMove = "0";

        currentUid = mAuth.getCurrentUser().getUid();
        isHost = currentUid.equals(LobbyActivity.host);
        if(isHost){
            StateMachine.ToGame(mReference, mAuth.getCurrentUser(), LobbyActivity.hostRoomId);
            GenerateShips();
        }
        if(isHost) {
            mReference.child("Users").child(LobbyActivity.hostRoomId).child("GameStatus").onDisconnect().setValue("Host");
        }
        else {
            mReference.child("Users").child(LobbyActivity.hostRoomId).child("GameStatus").onDisconnect().setValue("Client");
        }

        statusListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("Users").child(LobbyActivity.hostRoomId).child("Status").getValue(String.class);
                if(status == null){
                    return;
                }
                if(!status.equals("Game") && !status.equals("Offline"))
                {
                    return;
                }
                if(status.equals("Game") && !isGameStarted){
                    if(!isHost){
                        Gson gson = new Gson();
                        String jsonString = dataSnapshot.child("Users").child(LobbyActivity.hostRoomId).child("ClientFleet").getValue(String.class);
                        userBoard = gson.fromJson(jsonString, Board.class);
                        userBoard.fixAfterJson();
                        userAdapter = new TileAdapter(getApplicationContext(), userBoard, 70);
                        userGrid.setAdapter(userAdapter);

                        jsonString = dataSnapshot.child("Users").child(LobbyActivity.hostRoomId).child("HostFleet").getValue(String.class);
                        enemyBoard = gson.fromJson(jsonString, Board.class);
                        enemyBoard.fixAfterJson();
                        enemyAdapter = new TileAdapter(getApplicationContext(), enemyBoard, 70);
                        enemyGrid.setAdapter(enemyAdapter);
                    }
                    isInProgress = true;
                    isGameStarted = true;
                    isUserTurn = isHost;
                    if(isUserTurn){
                        ((TextView) findViewById(R.id.turnText)).setText(R.string.user_turn);
                    }
                    else{
                        ((TextView) findViewById(R.id.turnText)).setText(R.string.enemy_turn);
                    }
                }
                String gameStatus = dataSnapshot.child("Users").child(LobbyActivity.hostRoomId).child("GameStatus").getValue(String.class);
                if(gameStatus == null){
                    return;
                }
                if(gameStatus.equals("Progress")){
                    String numberMove = dataSnapshot.child("Users").child(LobbyActivity.hostRoomId).child("NumberMove").getValue(String.class);
                    String moveStatus = dataSnapshot.child("Users").child(LobbyActivity.hostRoomId).child("Type").getValue(String.class);
                    if(!numberMove.equals(currentMove) && moveStatus.equals("Request")){
                        int temp = Integer.parseInt(currentMove);
                        temp++;
                        currentMove = Integer.toString(temp);

                        Gson gson = new Gson();
                        String jsonString = dataSnapshot.child("Users").child(LobbyActivity.hostRoomId).child("Move").getValue(String.class);
                        Cell currentMove = gson.fromJson(jsonString, Cell.class);
                        int result = updateTile(currentMove, userBoard);
                        isUserTurn = result != 1 && result != 2;
                        isMoveAllowed = false;

                        userAdapter = new TileAdapter(getApplicationContext(), userBoard, 70);
                        userGrid.setAdapter(userAdapter);
                        enemyAdapter = new TileAdapter(getApplicationContext(), enemyBoard, 70);
                        enemyGrid.setAdapter(enemyAdapter);
                        mReference.child("Users").child(LobbyActivity.hostRoomId).child("Type").setValue("Synced");
                        if(isUserTurn){
                            ((TextView) findViewById(R.id.turnText)).setText(R.string.user_turn);
                        }
                        else{
                            ((TextView) findViewById(R.id.turnText)).setText(R.string.enemy_turn);
                        }
                        if(CheckIfLose()){
                            if(isHost)
                                mReference.child("Users").child(LobbyActivity.hostRoomId).child("GameStatus").setValue("Host");
                            else
                                mReference.child("Users").child(LobbyActivity.hostRoomId).child("GameStatus").setValue("Client");
                        }
                    }
                    else if(moveStatus.equals("Synced") && isWaitingForSync) {
                        Gson gson = new Gson();
                        String jsonString = dataSnapshot.child("Users").child(LobbyActivity.hostRoomId).child("Move").getValue(String.class);
                        Cell currentMove = gson.fromJson(jsonString, Cell.class);
                        int result = updateTile(currentMove, enemyBoard);
                        isUserTurn = result == 1 || result == 2;
                        isMoveAllowed = false;
                        if(isUserTurn){
                            ((TextView) findViewById(R.id.turnText)).setText(R.string.user_turn);
                        }
                        else{
                            ((TextView) findViewById(R.id.turnText)).setText(R.string.enemy_turn);
                        }

                        mReference.child("Users").child(LobbyActivity.hostRoomId).child("Type").setValue("Waiting");
                        userAdapter = new TileAdapter(getApplicationContext(), userBoard, 70);
                        userGrid.setAdapter(userAdapter);
                        enemyAdapter = new TileAdapter(getApplicationContext(), enemyBoard, 70);
                        enemyGrid.setAdapter(enemyAdapter);
                        isWaitingForSync = false;
                    }
                    else if(moveStatus.equals("Waiting")){
                        isMoveAllowed = true;
                    }
                }
                else {
                    isInProgress = false;
                    MatchData data = new MatchData();
                    data.setDate();
                    if(gameStatus.equals("Host")){
                        if(isHost){
                            data.setResult("Lose");
                            Toast.makeText(GameActivity.this, "You lose!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            data.setResult("Win");
                            Toast.makeText(GameActivity.this, "YOU WIN!!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        if(isHost){
                            data.setResult("Win");
                            Toast.makeText(GameActivity.this, "YOU WIN!!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            data.setResult("Lose");
                            Toast.makeText(GameActivity.this, "You lose!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    data.setShipRemains(userBoard.getNumOfShipsLeft());
                    Gson gson = new Gson();
                    String jsonData = gson.toJson(data);
                    mReference.child("Users").child("History").child(currentUid).child(data.getDate()).setValue(jsonData);

                    FinishGame();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        mReference.addValueEventListener(statusListener);
    }

    @Override
    protected void onStop(){
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gameStatus = dataSnapshot.child("Users").child(LobbyActivity.hostRoomId).child("GameStatus").getValue(String.class);
                if(gameStatus.equals("Progress")){
                    String result = "Client";
                    if(isHost){
                        result = "Host";
                    }
                    mReference.child("Users").child(LobbyActivity.hostRoomId).child("GameStatus").setValue(result);
                    FinishGame();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        if(statusListener != null){
            mReference.removeEventListener(statusListener);
        }
        mReference.child("Users").child(LobbyActivity.hostRoomId).child("Status").setValue("Offline");
        super.onStop();
    }


    private void GenerateShips(){
        if(isHost){
            userBoard = new Board(new int[] {4, 3, 3, 2, 2, 2, 1, 1, 1, 1});
            enemyBoard = new Board(new int[] {4, 3, 3, 2, 2, 2, 1, 1, 1, 1});

            userAdapter = new TileAdapter(getApplicationContext(), userBoard, 70);
            userGrid.setAdapter(userAdapter);
            enemyAdapter = new TileAdapter(getApplicationContext(), enemyBoard, 70);
            enemyGrid.setAdapter(enemyAdapter);

            Gson gson = new Gson();
            String board = gson.toJson(enemyBoard);
            mReference.child("Users").child(LobbyActivity.hostRoomId).child("ClientFleet").setValue(board);
            board = gson.toJson(userBoard);
            mReference.child("Users").child(LobbyActivity.hostRoomId).child("HostFleet").setValue(board);
        }
        else{
            userBoard = new Board(new int[] {});
            enemyBoard = new Board(new int[] {});
        }
    }

    private void FinishGame(){
        if(isFinished){
            return;
        }
        if(isHost){
            StateMachine.ToOffline(mReference, mAuth.getCurrentUser(), LobbyActivity.hostRoomId);
        }
        isFinished = true;
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean CheckIfLose(){
        return userBoard.getNumOfShipsLeft() == 0;
    }

    private void PlayTile(Cell cell){
        if(isClosedTile(cell, enemyBoard)){
            return;
        }
        isUserTurn = false;
        int temp = Integer.parseInt(currentMove);
        temp++;
        currentMove = Integer.toString(temp);
        if(isHost){
            mReference.child("Users").child(LobbyActivity.hostRoomId).child("CurrentMove").setValue("0");
        }
        else {
            mReference.child("Users").child(LobbyActivity.hostRoomId).child("CurrentMove").setValue("1");
        }
        Gson gson = new Gson();
        String move = gson.toJson(cell);
        mReference.child("Users").child(LobbyActivity.hostRoomId).child("Move").setValue(move);

        mReference.child("Users").child(LobbyActivity.hostRoomId).child("NumberMove").setValue(currentMove);
        mReference.child("Users").child(LobbyActivity.hostRoomId).child("Type").setValue("Request");
        isWaitingForSync = true;
    }

    private boolean isClosedTile(Cell cell, Board board){
        return board.getTile(cell.getRow(), cell.getCol()).isFired();
    }

    private int updateTile(Cell cell, Board board){
        boolean sunk;
        Tile.Status tileStatus = board.getTile(cell.getRow(), cell.getCol()).getTileStatus();
        //in case player MISS
        if (tileStatus == Tile.Status.NONE || tileStatus == Tile.Status.NONE_X) {
            board.getTile(cell.getRow(), cell.getCol()).setTileStatus(Tile.Status.MISS);
            board.countDownOneTileFromBoard();
            return 0;
        }
        //in case player HIT
        else if (tileStatus == Tile.Status.SHIP) {
            sunk = board.getTile(cell.getRow(), cell.getCol()).hitTile();
            if (sunk) {
                Cell[] p = board.getTile(cell.getRow(), cell.getCol()).getShipPoints();
                for (int i = 0; i < p.length; i++){
                    board.getTile(p[i].getRow(), p[i].getCol()).setTileStatus(Tile.Status.SUNK);
                    board.getTile(p[i].getRow(), p[i].getCol()).setFired(true);
                    board.countDownOneTileFromBoard();
                }
                for(int k = 0; k < p.length; k++){
                    for(int i = -1; i <= 1; i++){
                        for (int j = -1; j <= 1; j++){
                            if(p[k].getRow() + i >= 0 && p[k].getRow() + i < board.getBoardSize() &&
                                    p[k].getCol() + j >= 0 && p[k].getCol() + j < board.getBoardSize()){
                                if(!board.getTile(p[k].getRow() + i, p[k].getCol() + j).isFired()){
                                    board.getTile(p[k].getRow() + i, p[k].getCol() + j).setTileStatus(Tile.Status.MISS);
                                    board.countDownOneTileFromBoard();
                                }
                            }
                        }
                    }
                }
                board.setNumOfShipsLeft(board.getNumOfShipsLeft() - 1);
                return 2;
            } else {
                board.getTile(cell.getRow(), cell.getCol()).setTileStatus(Tile.Status.HIT);
                board.countDownOneTileFromBoard();
                return 1;
            }
        }
        else {
            return -1;
        }
    }
}

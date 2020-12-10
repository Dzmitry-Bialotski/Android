package by.belotskiy.battleship.game_logic;

public class Tile {
    private Status tileStatus;
    private Ship ship;
    private boolean isFired;
    private boolean isSunk;


    public Tile()
    {
        this.tileStatus = Status.NONE;
        this.ship = null;
        isFired = false;
        isSunk = false;
    }


    public void setShip(Ship ship){
        this.ship = ship;
        setTileStatus(Tile.Status.SHIP);
    }

    public boolean hitTile(){
        this.ship.hitShip();
        return this.ship.isSunk();
    }

    public Cell[] getShipPoints(){
        return this.ship.getPointsOnBoard();
    }

    public void setTileStatus(Status tileStatus) {
        this.tileStatus = tileStatus;
    }
    public Status getTileStatus() {
        return tileStatus;
    }

    public boolean isSunk() {
        return isSunk;
    }
    public void setIsSunk(boolean isSunk) {
        this.isSunk = isSunk;
    }

    public boolean isFired(){
        return isFired;
    }
    public void setFired(boolean isFired){
        this.isFired = isFired;
    }


    public enum Status {
        NONE,NONE_X,HIT,MISS,SHIP,SUNK
    }
}

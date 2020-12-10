package by.belotskiy.battleship.game_logic;

public class Ship {
    private int size, hitPoints;
    public String direction;
    private boolean isSunk;
    private Cell pointsOnBoard[];
    private Cell headCell;

    public Ship(int size){
        this.size = size;
        this.hitPoints = size;
        this.isSunk = false;
        this.headCell = new Cell();
        pointsOnBoard = new Cell[size];
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction){
        this.direction = direction;
    }

    public void hitShip() {
        this.hitPoints--;
        if (this.hitPoints == 0 )
            isSunk = true;
    }

    public int getSize() {
        return size;
    }

    public boolean isSunk() {
        return isSunk;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public Cell[] getPointsOnBoard() {
        return pointsOnBoard;
    }

    public void setPointsOnBoard(Cell[] pointsOnBoard) {
        this.pointsOnBoard = pointsOnBoard;
    }

    public void setHeadPoint(int row, int col) {
        this.headCell.setRow(row);
        this.headCell.setCol(col);
    }

    public Cell getHeadCell() {
        return headCell;
    }
}

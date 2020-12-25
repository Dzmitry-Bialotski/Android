package by.belotskiy.battleship.game;

import by.belotskiy.battleship.game.enums.CellType;

public class Cell {
    private int x;
    private int y;
    private CellType cellType;

    public Cell(int x, int y, CellType cellType){
        this.x = x;
        this.y = y;
        this.cellType = cellType;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

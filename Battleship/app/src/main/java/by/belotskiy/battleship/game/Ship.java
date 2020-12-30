package by.belotskiy.battleship.game;

import java.util.ArrayList;
import java.util.Collections;

import by.belotskiy.battleship.game.enums.CellType;
import by.belotskiy.battleship.game.enums.Orientation;

public class Ship {
    private ArrayList<Cell> cells;
    private Orientation orientation;
    private int size;
    public Ship(ArrayList<Cell> cells, Orientation orientation, int size){
        this.cells = new ArrayList<>();
        for(int i = 0; i < size; i++){
            this.cells.add(cells.get(i));
        }
        //Collections.copy(this.cells, cells);
        this.orientation = orientation;
        this.size = size;
    }

    public ArrayList<Cell> getCells() {
        return cells;
    }

    public void setCells(ArrayList<Cell> cells) {
        this.cells = cells;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    public void setCell(Cell newCell){
        for(Cell cell : cells){
            if(cell.getX() == newCell.getX() && cell.getY() == newCell.getY()){
                cell.setCellType(newCell.getCellType());
            }
        }
    }
}

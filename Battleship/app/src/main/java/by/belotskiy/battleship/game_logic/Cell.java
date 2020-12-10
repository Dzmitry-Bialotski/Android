package by.belotskiy.battleship.game_logic;

public class Cell {
    private int row;
    private int col;


    public Cell(int row, int col){
        setRow(row);
        setCol(col);
    }

    public Cell(){
        setRow(0);
        setCol(0);
    }


    public void setRow(int row){
        this.row = row;
    }
    public int getRow(){
        return row;
    }

    public void setCol(int col){
        this.col = col;
    }
    public int getCol(){
        return col;
    }
}

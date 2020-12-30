package by.belotskiy.battleship.game.util;

import by.belotskiy.battleship.game.enums.CellType;

public class FleetGenerator {
    private final int size;

    public FleetGenerator(int size){
        this.size = size;
    }
    public CellType[][] generateCells(){
        CellType[][] cells = new CellType[size][size];
        for(int i = 0; i < size; i ++){
            for(int j =0; j < size; j ++){
                cells[i][j] = CellType.EMPTY;
            }
        }
        cells[0][7] = CellType.SHIP;
        cells[0][8] = CellType.SHIP;
        cells[1][1] = CellType.SHIP;
        cells[1][4] = CellType.SHIP;
        cells[2][4] = CellType.SHIP;
        cells[2][8] = CellType.SHIP;
        cells[4][6] = CellType.SHIP;
        cells[4][7] = CellType.SHIP;
        cells[4][8] = CellType.SHIP;
        cells[5][0] = CellType.SHIP;
        cells[5][2] = CellType.SHIP;
        cells[6][0] = CellType.SHIP;
        cells[6][2] = CellType.SHIP;
        cells[7][2] = CellType.SHIP;
        cells[7][6] = CellType.SHIP;
        cells[8][2] = CellType.SHIP;
        cells[8][6] = CellType.SHIP;
        cells[8][9] = CellType.SHIP;
        cells[9][0] = CellType.SHIP;
        cells[9][6] = CellType.SHIP;
        return cells;
    }
}

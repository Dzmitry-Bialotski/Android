package by.belotskiy.battleship.game.service;

import java.util.ArrayList;

import by.belotskiy.battleship.game.Cell;
import by.belotskiy.battleship.game.Field;
import by.belotskiy.battleship.game.Fleet;
import by.belotskiy.battleship.game.Ship;
import by.belotskiy.battleship.game.enums.CellType;
import by.belotskiy.battleship.game.enums.FieldType;
import by.belotskiy.battleship.game.enums.Orientation;

public class FleetService {
    public boolean fleetIsSunk(Fleet fleet){
        for(Ship ship : fleet.getShips()){
            if(!shipIsSunk(ship))
            {
                return false;
            }
        }
        return true;
    }
    public boolean shipIsSunk(Ship ship){
        for(Cell cell : ship.getCells()){
            if(cell.getCellType() != CellType.SUNK){
                return false;
            }
        }
        return true;
    }
    public Fleet createFleetIfLegal(Field field){
        ArrayList<Ship> ships = new ArrayList<>(10);
        for(int i = 0; i < field.getSize(); i++) {
            for(int j =0; j < field.getSize(); j++){
                if(hasToBeChecked(field.getCells(), i, j)){
                    ArrayList<Cell> horizontal_Cells = new ArrayList<>();
                    int horizontal_size = 0;
                    int I = i-1;
                    do{
                        horizontal_size++;
                        I++;
                        horizontal_Cells.add(new Cell(I, j, CellType.SHIP));
                    }while (checkRightCellIsShip(field, I, j));

                    ArrayList<Cell> vertical_Cells = new ArrayList<>();
                    int vertical_size = 0;
                    int J = j-1;
                    do{
                        vertical_size++;
                        J++;
                        vertical_Cells.add(new Cell(i, J, CellType.SHIP));
                    }while (checkBottomCellIsShip(field, i, J));

                    if(vertical_size == 1){
                        Ship ship = new Ship(horizontal_Cells, Orientation.HORIZONTAL, horizontal_Cells.size());
                        if(checkBorderCells(ship,field)){
                            ships.add(ship);
                        }
                    }else if(horizontal_size == 1){
                        Ship ship = new Ship(vertical_Cells, Orientation.VERTICAL, vertical_Cells.size());
                        if(checkBorderCells(ship,field)){
                            ships.add(ship);
                        }
                    }
                }
            }
        }
        Fleet fleet = new Fleet(ships);
        if(fleetIsLegal(fleet)){
            return fleet;
        }else{
            return null;
        }
    }
    public Ship findShip(int i, int j, Fleet fleet){
        for (Ship ship : fleet.getShips()){
            if(hasCell(i, j, ship)){
                return ship;
            }
        }
        return null;
    }

    /**
     *     helper methods
     */
    private boolean hasCell(int i, int j, Ship ship){
        for(Cell cell : ship.getCells()){
            if(cell.getX() == i && cell.getY() == j){
                return true;
            }
        }
        return false;
    }
    //check if left and right cells are empty or border and cell is ship
    private boolean hasToBeChecked(CellType[][] cells, int i, int j)
    {
        if(cells[i][j] == CellType.SHIP || cells[i][j] == CellType.SUNK) {
            return checkLeftCell(cells,i,j) && checkTopCell(cells,i,j);
        } else{
            return false;
        }
    }

    //check if left cell is empty or border
    private boolean checkLeftCell(CellType[][] cells, int i, int j){
        if(i-1 >= 0) {
            return (cells[i-1][j] == CellType.EMPTY) || (cells[i-1][j] == CellType.MISS);
        } else{
            return true;
        }
    }

    //check if top cell is empty or border
    private boolean checkTopCell(CellType[][] cells, int i, int j){
        if(j-1 >= 0) {
            return (cells[i][j-1] == CellType.EMPTY) || (cells[i][j-1] == CellType.MISS);
        } else{
            return true;
        }
    }
    //check if right cell is ship
    private boolean checkRightCellIsShip(Field field, int i, int j){
        if(i+1 < field.getSize()){
            return (field.getCells()[i+1][j] == CellType.SHIP) || (field.getCells()[i+1][j] == CellType.SUNK);
        }else{
            return false;
        }
    }
    //check if bottom cell is ship
    private boolean checkBottomCellIsShip(Field field, int i, int j){
        if(j+1 < field.getSize()){
            return (field.getCells()[i][j+1] == CellType.SHIP) || (field.getCells()[i][j+1] == CellType.SUNK);
        }else{
            return false;
        }
    }

    private boolean checkBorderCells(Ship ship, Field field){
        Cell firstCell = ship.getCells().get(0);
        int size = ship.getSize();
        Cell lastCell = ship.getCells().get(size-1);
        int x_1 = firstCell.getX() - 1;
        int y_1 = firstCell.getY() - 1;
        int x_2 = lastCell.getX() + 1;
        int y_2 = lastCell.getY() + 1;
        int fieldSize = field.getSize();
        for(int i = x_1; i <= x_2; i++){
            for(int j = y_1; j <= y_2; j++){
                if(i < 0 || i >= fieldSize || j < 0 || j >= fieldSize){
                    continue;
                }
                if(i == x_1 || i == x_2 || j == y_1 || j == y_2){
                    if((field.getCells()[i][j] != CellType.EMPTY) && (field.getCells()[i][j] != CellType.MISS) ){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    //checks number of different-size ships
    private boolean fleetIsLegal(Fleet fleet){
        int four_deck_ships = 0;
        int three_deck_ships = 0;
        int two_deck_ships = 0;
        int one_deck_ships = 0;
        for(Ship ship : fleet.getShips()){
            switch (ship.getSize()){
                case 1:
                    one_deck_ships++;
                    break;
                case 2:
                    two_deck_ships++;
                    break;
                case 3:
                    three_deck_ships++;
                    break;
                case 4:
                    four_deck_ships++;
                    break;
                default:
                    return false;
            }
        }
        return four_deck_ships == 1 && three_deck_ships == 2 && two_deck_ships == 3 && one_deck_ships == 4;
    }
    public ArrayList<Cell> findBorderCells(Ship ship, Field field){
        ArrayList<Cell> result = new ArrayList<>();
        Cell firstCell = ship.getCells().get(0);
        int size = ship.getSize();
        Cell lastCell = ship.getCells().get(size-1);
        int x_1 = firstCell.getX() - 1;
        int y_1 = firstCell.getY() - 1;
        int x_2 = lastCell.getX() + 1;
        int y_2 = lastCell.getY() + 1;
        int fieldSize = field.getSize();
        for(int i = x_1; i <= x_2; i++){
            for(int j = y_1; j <= y_2; j++){
                if(i < 0 || i >= fieldSize || j < 0 || j >= fieldSize){
                    continue;
                }
                if(i == x_1 || i == x_2 || j == y_1 || j == y_2){
                    result.add(new Cell(i,j,field.getCells()[i][j]));
                }
            }
        }
        return result;
    }
    public Fleet createFleet(Field field){
        ArrayList<Ship> ships = new ArrayList<>(10);
        for(int i = 0; i < field.getSize(); i++) {
            for(int j =0; j < field.getSize(); j++){
                if(hasToBeChecked(field.getCells(), i, j)){
                    ArrayList<Cell> horizontal_Cells = new ArrayList<>();
                    int horizontal_size = 0;
                    int I = i-1;
                    do{
                        horizontal_size++;
                        I++;
                        horizontal_Cells.add(new Cell(I, j, field.getCells()[i][j]));
                    }while (checkRightCellIsShip(field, I, j));

                    ArrayList<Cell> vertical_Cells = new ArrayList<>();
                    int vertical_size = 0;
                    int J = j-1;
                    do{
                        vertical_size++;
                        J++;
                        vertical_Cells.add(new Cell(i, J, field.getCells()[i][j]));
                    }while (checkBottomCellIsShip(field, i, J));

                    if(vertical_size == 1){
                        ships.add(new Ship(horizontal_Cells, Orientation.HORIZONTAL, horizontal_Cells.size()));
                    }else if(horizontal_size == 1){
                        ships.add(new Ship(vertical_Cells, Orientation.VERTICAL, vertical_Cells.size()));
                    }
                }
            }
        }
        return new Fleet(ships);
    }
}

package by.belotskiy.battleship.game_logic;

import java.util.Random;

public class Board {
    public final static String HORIZONTAL = "horizontal";
    public final static String VERTICAL = "vertical";
    public final static int BOARD_SIZE = 10;

    private Tile[][] tiles;
    private Ship[] fleet;
    private int numOfShipsLeft;
    private Random random = new Random();
    private int numOfTilesLeft;


    public Board(int[] shipsInFleetSizes) {
        tiles = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                tiles[i][j] = new Tile();
            }
        }
        fleet = new Ship[shipsInFleetSizes.length];
        for (int i = 0; i < shipsInFleetSizes.length; i++) {
            this.fleet[i] = new Ship(shipsInFleetSizes[i]);
            placeShipOnBoard(this.fleet[i]);
        }
        setNumOfShipsLeft(shipsInFleetSizes.length);
        numOfTilesLeft = getTotalBoardSize();
    }


    public void fixAfterJson(){
        tiles = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                tiles[i][j] = new Tile();
            }
        }
        for (int i = 0; i < fleet.length; i++) {
            setShipOnBoard(this.fleet[i]);
        }
        setNumOfShipsLeft(this.fleet.length);
        numOfTilesLeft = getTotalBoardSize();
    }

    private void setShipOnBoard(Ship ship){
        place(ship.getHeadCell().getRow(), ship.getHeadCell().getCol(), ship);
    }

    public void placeShipOnBoard(Ship ship) {
        boolean flag = true;
        int randomRow, randomCol, randomDirectionInt;
        String randomDirection;
        do {
            randomRow = random.nextInt(BOARD_SIZE);   //[0-9]
            randomCol = random.nextInt(BOARD_SIZE);
            randomDirectionInt = random.nextInt(2);        //[0-1]
            if (randomDirectionInt == 1)
                randomDirection = VERTICAL;
            else
                randomDirection = HORIZONTAL;
            flag = checkValidLocation(randomRow, randomCol, randomDirection, ship.getSize());
        } while (!flag);
        ship.setDirection(randomDirection);
        ship.setHeadPoint(randomRow, randomCol);
        place(randomRow, randomCol, ship);
    }

    public boolean checkValidLocation(int row, int col, String direction, int shipSize) {
        if (direction.compareTo(HORIZONTAL) == 0) {
            //check if the ship in the bounds of the board
            if (col + shipSize - 1 > BOARD_SIZE - 1){
                return false;
            }
            for (int i = 0; i < shipSize; i++) {
                //check for collision
                if (tiles[row][col + i].getTileStatus() != Tile.Status.NONE){
                    return false;
                }
            }
        }
        else {
            //vertical case
            if (row + shipSize - 1 > BOARD_SIZE - 1){
                return false;
            }
            for (int i = 0; i < shipSize; i++) {
                //check for collision
                if (tiles[row + i][col].getTileStatus() != Tile.Status.NONE){
                    return false;
                }
            }
        }

        return true;
    }

    public void place(int row, int col, Ship ship) {
        int size = ship.getSize();
        Cell[] shipCells = new Cell[ship.getSize()];

        if (ship.getDirection().compareTo(HORIZONTAL) == 0) {
            for (int i = 0; i < size; i++) {
                tiles[row][col + i].setShip(ship);
                shipCells[i] = new Cell(row, col + i);
                if (row < BOARD_SIZE - 1)
                    tiles[row + 1][col + i].setTileStatus(Tile.Status.NONE_X);
                if (row > 0)
                    tiles[row - 1][col + i].setTileStatus(Tile.Status.NONE_X);
            }
            if (col > 0){
                tiles[row][col - 1].setTileStatus(Tile.Status.NONE_X);
                if (row < BOARD_SIZE - 1)
                    tiles[row + 1][col - 1].setTileStatus(Tile.Status.NONE_X);
                if (row > 0)
                    tiles[row - 1][col - 1].setTileStatus(Tile.Status.NONE_X);
            }
            if (col + size - 1 < BOARD_SIZE - 1){
                tiles[row][col + size].setTileStatus(Tile.Status.NONE_X);
                if (row < BOARD_SIZE - 1)
                    tiles[row + 1][col + size].setTileStatus(Tile.Status.NONE_X);
                if (row > 0)
                    tiles[row - 1][col + size].setTileStatus(Tile.Status.NONE_X);
            }
        }
        //vertical
        else {
            for (int i = 0; i < size; i++) {
                tiles[row + i][col].setShip(ship);
                shipCells[i] = new Cell(row + i, col);
                if (col < BOARD_SIZE - 1)
                    tiles[row + i][col + 1].setTileStatus(Tile.Status.NONE_X);
                if (col > 0)
                    tiles[row + i][col - 1].setTileStatus(Tile.Status.NONE_X);
            }
            if (row > 0){
                tiles[row - 1][col].setTileStatus(Tile.Status.NONE_X);
                if (col < BOARD_SIZE - 1)
                    tiles[row - 1][col + 1].setTileStatus(Tile.Status.NONE_X);
                if (col > 0)
                    tiles[row - 1][col - 1].setTileStatus(Tile.Status.NONE_X);
            }
            if (row + size - 1 < BOARD_SIZE - 1){
                tiles[row + size][col].setTileStatus(Tile.Status.NONE_X);
                if (col < BOARD_SIZE - 1)
                    tiles[row + size][col + 1].setTileStatus(Tile.Status.NONE_X);
                if (col > 0)
                    tiles[row + size][col - 1].setTileStatus(Tile.Status.NONE_X);
            }
        }
        ship.setPointsOnBoard(shipCells);
    }

    public int getBoardSize() {
        return BOARD_SIZE;
    }

    public Ship[] getFleet() {
        return fleet;
    }

    public int getNumOfShipsLeft() {
        return numOfShipsLeft;
    }

    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }


    public void setNumOfShipsLeft(int numOfShipsLeft) {
        this.numOfShipsLeft = numOfShipsLeft;
    }

    public void countDownOneTileFromBoard() {
        if (numOfTilesLeft > 0)
            numOfTilesLeft--;
    }

    public int getNumOfTilesLeft() {
        return numOfTilesLeft;
    }

    public int getTotalBoardSize() {
        return BOARD_SIZE * BOARD_SIZE;
    }
}

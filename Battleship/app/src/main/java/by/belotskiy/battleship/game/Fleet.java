package by.belotskiy.battleship.game;

import java.util.ArrayList;

public class Fleet {
    private ArrayList<Ship> ships;

    public Fleet(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }
}

package by.belotskiy.battleship.util;

import java.util.Random;

public class IdGenerator {
    private final static int minValue = 100000;
    private final static int maxValue = 999999;
    private final static Random random = new Random();
    public static String generateRoomId(){
        return ((Integer)(minValue + random.nextInt(maxValue + 1))).toString();
    }
}

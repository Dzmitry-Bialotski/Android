package by.belotskiy.tabatatimer.util;

public class Converter {
    private static String strSeparator = "->";

    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str + array[i];
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public static String[] convertStringToArray(String str){
        return str.split(strSeparator);
    }

    public static String convertIntArrayToString(Integer[] numberArray) {
        String[] array = new String[numberArray.length];

        for(int i = 0; i < numberArray.length; i++) {
            array[i] = numberArray[i].toString();
        }

        return convertArrayToString(array);
    }

    public static Integer[] convertStringToIntegerArray(String str) {
        String[] array = convertStringToArray(str);
        Integer[] numbers = new Integer[array.length];

        for(int i = 0; i < array.length; i++) {
            numbers[i] = Integer.parseInt(array[i]);
        }

        return numbers;
    }

    public static String getStrSeparator() {
        return strSeparator;
    }

    public static void setStrSeparator(String strSeparator) {
        Converter.strSeparator = strSeparator;
    }
}

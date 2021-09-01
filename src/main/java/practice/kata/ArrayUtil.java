package practice.kata;

import java.util.Arrays;

public class ArrayUtil {


    public static int[] flattenAndSort(int[][] array) {

        return Arrays
                .stream(array)
                .flatMapToInt(Arrays::stream)
                .sorted()
                .toArray();
    }


}

package practice.kata;

import java.util.Arrays;

public class Kata {
    public static String encryptThis(String text) {
        if (text.isBlank()) {
            return text;
        }

        String result = text;

        String[] splitInput = text.split(" ");
        String[] splitOutput = new String[]{};

        Object[] objects = Arrays.stream(splitInput).map(Kata::encryptWord).toArray();



        for (String current : splitInput) {
//            splitOutput.
        }


        return result;
    }

    private static String encryptWord(String inputWord) {
        String result = inputWord;


        return result;
    }

}

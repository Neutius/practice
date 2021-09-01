package practice.kata;

public class PerfectPower {

    public static int[] isPerfectPower(int input) {
        if (input < 4) {
            return null;
        }

        int largestPossibleFactor = input / 2;

        for (int base = 2; base <= largestPossibleFactor; base++) {

            int exponent = 2;
            int reproduction = base;
            while (reproduction < input) {
                reproduction *= base;
                if (reproduction == input) {
                    return new int[] {base, exponent};
                }
                exponent++;
            }

        }

        return null;
    }

}

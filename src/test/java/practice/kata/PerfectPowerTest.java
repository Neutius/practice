package practice.kata;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class PerfectPowerTest {

    @Test
    void aaa_basicTest_testZero() {
        int[] result = PerfectPower.isPerfectPower(0);

        assertThat(result).isNull();
    }


    @Test
    void aaa_basicTest_testFour() {
        int[] result = PerfectPower.isPerfectPower(4);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2).containsExactly(2, 2);

    }

    @Test
    void test_sevenToTheFourth() {
        int[] result = PerfectPower.isPerfectPower(2401);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2).containsExactly(7, 4);
    }

    @Test
    void huge_numbers() {
        int[] result = PerfectPower.isPerfectPower(5_764_801);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2).containsExactly(7, 8);
    }

    @Test
    void here_we_go() {
        int[] inputArray = new int[]{0, 1, 2, 3, 4, 5, 8, 9, 16, 25, 27, 32, 36, 49, 64, 81,
                100, 121, 125, 128, 144, 169, 196, 216, 225, 243, 256, 289, 324, 343, 361, 400, 441, 484};

        for (int input : inputArray) {
            System.out.println(input + " - " + Arrays.toString(PerfectPower.isPerfectPower(input)));
        }
    }

}


//    @Test
//    public void testRandomPerfectPowers() {
//        Random rnd = new Random();
//        for (int i = 0; i < 100; i++) {
//            int m = rnd.nextInt(254)+2;
//            int k = (int)(rnd.nextDouble()*(log(Integer.MAX_VALUE)/log(m)-2.0)+2.0);
//            int l = ipow(m, k);
//            int[] r = PerfectPower.isPerfectPower(l);
//            assertNotNull(l+" is a perfect power", r);
//            assertEquals(r[0]+"^"+r[1]+"!="+l, l, ipow(r[0], r[1]));
//        }
//    }
//
//    @Test
//    public void testRandomNumbers() {
//        Random rnd = new Random();
//        for (int i = 0; i < 100; i++) {
//            int l = rnd.nextInt(Integer.MAX_VALUE);
//            int[] r = PerfectPower.isPerfectPower(l);
//            if (r != null) assertEquals(r[0]+"^"+r[1]+"!="+l, l, ipow(r[0], r[1]));
//        }
//    }
//
//    private static int ipow(int b, int e) {
//        int p = 1;
//        for (; e > 0; e >>= 1) {
//            if ((e & 1) == 1) p *= b;
//            b *= b;
//        }
//        return p;
//    }
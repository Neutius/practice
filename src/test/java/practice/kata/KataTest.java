package practice.kata;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KataTest {

    @Test
    void firstExample() {
        String expected = "65 119esi 111dl 111lw 108dvei 105n 97n 111ka";
        String actual = Kata.encryptThis("A wise old owl lived in an oak");

        assertThat(actual).isEqualTo(expected);

    }

}
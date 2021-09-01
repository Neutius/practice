package com.github.neutius.reflect;

import com.github.neutius.kata.stuff.SecretPerson;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ReflectionPracticeTest {

    @Test
    void inspect() {
        SecretPerson person = new SecretPerson();
        ReflectionPractice testSubject = new ReflectionPractice();

        testSubject.inspect(person);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(testSubject.getFieldsFor(person)).isNotNull().isNotEmpty().hasSize(3);
        softly.assertThat(testSubject.getMethodsFor(person)).isNotNull().isNotEmpty().hasSize(2);
        softly.assertThat(testSubject.getConstructorsFor(person)).isNotNull().isNotEmpty().hasSize(4);
        softly.assertAll();
    }

    @Test
    void twoPersonsAreIdentical() {
        SecretPerson person1 = new SecretPerson("Boris", 55, List.of("Talks too much"));
        SecretPerson person2 = new SecretPerson("Boris", 55, List.of("Talks too much"));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(ReflectionPractice.objectsHaveTheSameValues(person1, person2)).isTrue();
        softly.assertThat(person1.equals(person2)).isFalse();
        softly.assertAll();
    }


}
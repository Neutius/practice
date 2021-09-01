package com.github.neutius.kata.stuff;

import java.util.List;

public class SecretPerson {
    private final String name;
    private final int age;
    private final List<String> secrets;

    public SecretPerson() {
        this("name", 345, List.of("secret", "secrets"));
    }

    public SecretPerson(String name) {
        this(name, 34, List.of("Whatever"));
    }

    public SecretPerson(int age) {
        this("lazy", age, List.of("bladiebla"));
    }

    public SecretPerson(String name, int age, List<String> secrets) {
        this.name = name;
        this.age = age;
        this.secrets = secrets;
    }

    public String getName() {
        return name;
    }

    private int getAge() {
        return age;
    }

}

package com.example.secret_santa.dto;


import java.util.Map;

public class Person {

    private final String name;

    private final String email;

    private String desiredGift;

    private Map.Entry<String, String> recipient;

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDesiredGift() {
        return desiredGift;
    }

    public Map.Entry<String, String> getRecipient() {
        return recipient;
    }

    public void setDesiredGift(String desiredGift) {
        this.desiredGift = desiredGift;
    }

    public void setRecipient(Map.Entry<String, String> recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", desiredGift='" + desiredGift + '\'' +
                ", recipient=" + recipient +
                '}';
    }

}

package com.example.secret_santa.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


public class SantasList {

    private final String name;
    private List<Person> people = new ArrayList<>();

    @Override
    public String toString() {
        return "SantasList{" +
                "name='" + name + '\'' +
                ", people=" + people +
                '}';
    }

    public String getName() {
        return name;
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }


    public SantasList(String name) {
        this.name = name;
    }


}

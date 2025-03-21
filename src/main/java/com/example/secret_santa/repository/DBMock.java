package com.example.secret_santa.repository;

import com.example.secret_santa.dto.Person;
import com.example.secret_santa.dto.SantasList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DBMock {

    public static void fillDB(){


        List<Person> list = IntStream.range(1, 8)
                .mapToObj(i -> {
                    return new Person("Person_" + i, i + "@email.com");
                })
                .toList();

        SantasList santasList = new SantasList("Test_List_1");
        santasList.setPeople(list);
        LISTS.add(santasList);
    }
    public static List<SantasList> LISTS = new ArrayList<>();
}

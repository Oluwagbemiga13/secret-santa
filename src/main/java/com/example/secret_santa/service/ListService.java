package com.example.secret_santa.service;

import com.example.secret_santa.dto.SantasList;
import com.example.secret_santa.repository.DBMock;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListService {

    private final List<SantasList> LISTS = DBMock.LISTS;

    public boolean saveList(SantasList list) {
        return LISTS.add(list);
    }

    public List<SantasList> getAll(){
        return LISTS;
    }

    public boolean delete(SantasList list){
        return LISTS.remove(list);
    }

}

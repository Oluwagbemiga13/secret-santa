package com.example.secret_santa.mapper;


import com.example.secret_santa.dto.UserDTO;
import com.example.secret_santa.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<User,UserDTO> {
}

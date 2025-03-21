package com.example.secret_santa.repository;

import com.example.secret_santa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

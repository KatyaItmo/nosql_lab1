package com.fitness.nosql_lab1.repository;

import com.fitness.nosql_lab1.objects.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
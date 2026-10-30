package com.fitness.nosql_lab1.repository;

import com.fitness.nosql_lab1.objects.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {}
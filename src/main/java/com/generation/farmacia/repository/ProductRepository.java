package com.generation.farmacia.repository;

import com.generation.farmacia.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    public List <Product> findAllByTitleContainingIgnoreCase(@Param("title") String title);

}

package com.demo.my_first_mcp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.my_first_mcp.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Product findByName(String name);

	List<Product> findByNameContainingIgnoreCase(String name);
}

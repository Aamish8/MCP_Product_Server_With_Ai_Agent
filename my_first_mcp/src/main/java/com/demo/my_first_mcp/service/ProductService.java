package com.demo.my_first_mcp.service;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import com.demo.my_first_mcp.entity.Product;
import com.demo.my_first_mcp.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository repository;
  @Tool(description="List all products in the database")
  public List<Product>listProducts(){
	  return repository.findAll();
  }
  @Tool(description="Add a new product with a name and price")
  public Product addProduct(@ToolParam(description="Name of the product")String name,
		  					@ToolParam(description="Price of the product")double price) {
	  return repository.save(new Product(null,name,price));
  }
  @Tool(description="Find the product by the name(partial,case-insensitive match)")
  public List<Product> findProductByName(@ToolParam(description="Name of the product")String name) {
	  return repository.findByNameContainingIgnoreCase(name);
  }
  @Tool(description="Update the price of the product")
  public Product updatePrice(@ToolParam(description="New price of the product to be updated")double price,
		  					@ToolParam(description="Id of the product to update")Long id) {
	  Product product=repository.findById(id).orElseThrow(()->new IllegalArgumentException("Product Not Found by the id: "+id));
	  if(price<=0)throw new IllegalArgumentException("Price should be greater than 0");
	  product.setPrice(price);
	  return repository.save(product);
  }
  @Tool(description="Delete the product by its id")
  public String deleteById(@ToolParam(description="Id of the product to delete")Long id) {
	  if(!repository.existsById(id))throw new IllegalArgumentException("Product not found with id: "+id);
	  repository.deleteById(id);
	  return "Deleted the product with id: "+id;
  }
}

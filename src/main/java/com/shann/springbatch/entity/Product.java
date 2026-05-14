package com.shann.springbatch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "batch_product")
@Data
public class Product {
  @Id private Long id;
  private String name;
  private String description;
  private String brand;
  private String category;
  private Double price;
  private String currency;
  private Double stock;
  private String ean;
  private String color;
  private String size;
  private String availability;
  private int internalID;
}

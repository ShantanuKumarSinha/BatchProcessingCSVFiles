package com.shann.springbatch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "batch_user")
@Data
public class User {

  @Id
  private Long id;
  private String name;
  private String email;
  private Integer age;
}

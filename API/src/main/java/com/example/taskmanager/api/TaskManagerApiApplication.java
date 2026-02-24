package com.example.taskmanager.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
    basePackages = {
      "com.example.taskmanager.api",
      "com.example.taskmanager.shared",
      "com.example.taskmanager.sqldatastore"
    })
public class TaskManagerApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(TaskManagerApiApplication.class, args);
  }
}

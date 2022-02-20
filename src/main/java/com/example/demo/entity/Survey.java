package com.example.demo.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Survey {
    private int id;
	private int age;
	private int satisfaction;
	private String comment;
	private LocalDateTime created;
}

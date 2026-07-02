package com.example.pojo.entity;

import lombok.Data;

import java.util.List;

@Data
public class Prompt {
    private String model;
    private List<Message> messages;
    private Boolean stream;
}

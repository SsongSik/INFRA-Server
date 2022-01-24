package com.example.demo.src.project.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProjectRes {
    private String pj_header;
    private String pj_field;
    private String pj_name;
    private String pj_progress;
    private String pj_deadline;
    private int pj_total_person;
    private int pj_recruit_person;
}
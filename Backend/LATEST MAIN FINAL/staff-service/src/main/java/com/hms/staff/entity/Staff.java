package com.hms.staff.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.GeneratedValue;


@Entity
@Data
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String address;
    private String nic;
    private Double salary;
    private Integer age;
    private String occupation;
    private String email;

    // Default constructor
    public Staff() {}

    // Parameterized constructor
    public Staff(Long id, String code, String name, String address, String nic, Double salary, Integer age, String occupation, String email) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.nic = nic;
        this.salary = salary;
        this.age = age;
        this.occupation = occupation;
        this.email = email;
    }
    
    public void setId(Long id) {
        this.id = id;
    }


    
    
}
package com.hms.rooms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
//    private int numberOfChildren;
//    private int numberOfAdults;
    private String status;
//    private String type;
//    private double rate;
    private String roomType;
    private Double price;
    private Integer capacity;

    public Room() {}
    
    

public Room(Long id, String code, String status, String roomType, Double price, Integer capacity) {
		super();
		this.id = id;
		this.code = code;
		this.status = status;
		this.roomType = roomType;
		this.price = price;
		this.capacity = capacity;
	}



//    public Room(Long id, String code, int numberOfChildren, int numberOfAdults, String status, String type, double rate) {
//        this.id = id;
//        this.code = code;
//        this.numberOfChildren = numberOfChildren;
//        this.numberOfAdults = numberOfAdults;
//        this.status = status;
//        this.type = type;
//        this.rate = rate;
//    }

	public String getRoomType() {
		return roomType;
	}



	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}



	public Double getPrice() {
		return price;
	}



	public void setPrice(Double price) {
		this.price = price;
	}



	public Integer getCapacity() {
		return capacity;
	}



	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
//
//	public int getNumberOfChildren() {
//		return numberOfChildren;
//	}
//
//	public void setNumberOfChildren(int numberOfChildren) {
//		this.numberOfChildren = numberOfChildren;
//	}
//
//	public int getNumberOfAdults() {
//		return numberOfAdults;
//	}
//
//	public void setNumberOfAdults(int numberOfAdults) {
//		this.numberOfAdults = numberOfAdults;
//	}
//
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
//
//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
//
//	public double getRate() {
//		return rate;
//	}
//
//	public void setRate(double rate) {
//		this.rate = rate;
//	}

    
    
}
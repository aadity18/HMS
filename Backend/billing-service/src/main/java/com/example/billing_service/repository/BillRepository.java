package com.example.billing_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.billing_service.model.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
	
	 Optional<Bill> findTopByOrderByIdDesc(); // New method for getting last staff
    
}
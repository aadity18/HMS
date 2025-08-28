package com.hms.transaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.transaction.model.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
	
	 Optional<Bill> findTopByOrderByIdDesc(); // New method for getting last staff
    
}
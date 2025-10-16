package org.example.atmservice.repository;

import org.example.atmservice.model.ATM;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ATMRepository extends JpaRepository<ATM,Long> {
}

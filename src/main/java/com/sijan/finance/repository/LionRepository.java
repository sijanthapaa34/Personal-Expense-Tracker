package com.sijan.finance.repository;

import com.sijan.finance.model.Lion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LionRepository extends JpaRepository<Lion,Long> {

    Lion findByUsername(String username);
}

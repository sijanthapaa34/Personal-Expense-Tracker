package com.sijan.finance.service;

import com.sijan.finance.model.Lion;
import com.sijan.finance.repository.LionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LionService {

    @Autowired
    private LionRepository lionRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public Lion registerLion(Lion newLion) {
        newLion.setPassword(encoder.encode(newLion.getPassword())  );
        return lionRepo.save(newLion);
    }
}

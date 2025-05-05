package com.sijan.finance.service;

import com.sijan.finance.model.Lion;
import com.sijan.finance.model.LionPrincipal;
import com.sijan.finance.repository.LionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private LionRepository lionRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Lion lion = lionRepo.findByUsername(username);
        if (lion == null) {
            throw new RuntimeException("Username with username "+username+" not found");
        }
        return new LionPrincipal(lion);
    }
}

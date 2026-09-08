package com.gstbilling.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.gstbilling.dao.UserRepository;
import com.gstbilling.models.User;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    
    public User findByUsername(String username)
    {
    	 User user = userRepository.findByUsername(username);

         if (user == null) {
             throw new UsernameNotFoundException("User not found");
         }
         return user;

    }
    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}

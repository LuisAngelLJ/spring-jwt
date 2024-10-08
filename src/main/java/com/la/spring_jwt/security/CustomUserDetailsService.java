package com.la.spring_jwt.security;

import com.la.spring_jwt.model.User;
import com.la.spring_jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(usernameOrEmail);

        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByUsername(usernameOrEmail);
        }

        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("Usuario no encontrado con email o usuario: " + usernameOrEmail));

        // Asegúrate de que el rol tenga el prefijo "ROLE_"
        String roleWithPrefix = "ROLE_" + user.getRole().name();

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                //Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
                Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix))
        );
    }
}
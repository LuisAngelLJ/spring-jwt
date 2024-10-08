package com.la.spring_jwt.service;

import com.la.spring_jwt.model.Role;
import com.la.spring_jwt.model.User;
import com.la.spring_jwt.model.dto.UserDto;
import com.la.spring_jwt.repository.UserRepository;
import com.la.spring_jwt.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<User> listUser() {
        return userRepository.findAll();
    }

    public boolean exist(long idUser) {
        return this.userRepository.existsById(idUser);
    }

    public User updateUser(long userId, UserDto userDto) {
        Optional<User> existingUserOpt = userRepository.findById(userId);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // Actualizar solo los campos que no son nulos
            if (userDto.getFirstName() != null) existingUser.setFirstName(userDto.getFirstName());
            if (userDto.getLastName() != null) existingUser.setLastName(userDto.getLastName());
            if (userDto.getMiddleName() != null) existingUser.setMiddleName(userDto.getMiddleName());
            if (userDto.getEmail() != null) existingUser.setEmail(userDto.getEmail());
            if (userDto.getUsername() != null) existingUser.setUsername(userDto.getUsername());
            if (userDto.getPhone() != null) existingUser.setPhone(userDto.getPhone());
            if (userDto.getRole() != null) existingUser.setRole(userDto.getRole());
            existingUser.setUpdatedAt(LocalDateTime.now());

            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public void deleteUser(long userId) {
        this.userRepository.deleteById(userId);
    }

    public void changeUserRole(User user, Role newRole) {
        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public User userInformation(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("El usuario no existe " + email)
        );
        return user;
    }


    public User getUserById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }
}

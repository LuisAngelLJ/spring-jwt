package com.la.spring_jwt.controller;

import com.la.spring_jwt.model.User;
import com.la.spring_jwt.model.dto.UserDto;
import com.la.spring_jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.findByEmail(user.getEmail()).isPresent()
                || userService.findByUsername(user.getUsername()).isPresent())
            return ResponseEntity.badRequest().body("El email o el Usuario ya existe");

        userService.registerUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario registrado");//retornar en formato json
        return ResponseEntity.ok(response);

    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.listUser();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<User> update(@PathVariable long userId, @RequestBody UserDto userDto) {
        if (!this.userService.exist(userId)) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(this.userService.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable long userId) {
        if (this.userService.exist(userId)) {
            this.userService.deleteUser(userId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Usuario eliminado")); //retornar en formato json
        }

        return ResponseEntity.badRequest().body("El usuario no existe");
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<User> getuserById(@PathVariable Long id) {
        User user = this.userService.getUserById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

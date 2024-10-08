package com.la.spring_jwt.controller;

import com.la.spring_jwt.model.Role;
import com.la.spring_jwt.model.User;
import com.la.spring_jwt.security.CustomUserDetails;
import com.la.spring_jwt.security.JwtUtil;
import com.la.spring_jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String email, @RequestParam String newPassword) {
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setPassword(newPassword);
        userService.registerUser(user);
        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }

    @PostMapping("/change-role")
    public ResponseEntity<?> changeRole(@RequestParam String email, @RequestParam Role newRole) {
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        userService.changeUserRole(user, newRole);
        return ResponseEntity.ok("Rol cambiado satisfactoriamente.");
    }

    @GetMapping()
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userPrincipal) {
        return new ResponseEntity<>(userService.userInformation(userPrincipal.getEmail()), HttpStatus.OK);
    }
}

package com.la.spring_jwt.service;

import com.la.spring_jwt.model.Role;
import com.la.spring_jwt.model.User;
import com.la.spring_jwt.model.dto.UserDto;
import com.la.spring_jwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Puedes utilizar esta anotación si necesitas inicializar algo antes de cada test
    }

    @Test
    void findByEmail() {
        String email = "la_10@correo.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User foundUser = userService.findByEmail(email).orElse(null);

        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    void findByUsername() {
        String username = "brenn";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername(username).orElse(null);

        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
    }

    @Test
    void registerUser() {
        User user = new User();
        user.setPassword("plainPassword");

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);

        userService.registerUser(user);

        assertEquals(encodedPassword, user.getPassword());
        assertEquals(Role.USER, user.getRole());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void listUser() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        List<User> userList = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.listUser();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    @Test
    void exist() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = userService.exist(userId);

        assertTrue(result);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void updateUser() {
        long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("Old Name");

        UserDto userDto = new UserDto();
        userDto.setFirstName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.updateUser(userId, userDto);

        assertEquals("New Name", updatedUser.getFirstName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void deleteUser() {
        long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void changeUserRole() {
        User user = new User();
        user.setRole(Role.USER);

        userService.changeUserRole(user, Role.ADMIN);

        assertEquals(Role.ADMIN, user.getRole());
        verify(userRepository, times(1)).save(user);
    }
}
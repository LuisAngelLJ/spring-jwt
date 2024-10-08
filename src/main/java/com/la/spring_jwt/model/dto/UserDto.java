package com.la.spring_jwt.model.dto;

import com.la.spring_jwt.model.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String username;
    private Long phone;
    private Role role;
}

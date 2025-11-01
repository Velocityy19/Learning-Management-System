package com.example.Learning.Management.System.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.Learning.Management.System.Models.Roles;
import com.example.Learning.Management.System.Models.Users;
import com.example.Learning.Management.System.Repositories.RolesRepository;
import com.example.Learning.Management.System.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

public class UsersServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersService usersService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        Users user = new Users();
        user.setUserEmail("test@gmail.com");
        user.setUserPassword("password");

        Roles studentRole = new Roles();
        studentRole.setRoleName("STUDENT");

        when(userRepository.findByUserEmail("test@gmail.com")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPwd");
        when(rolesRepository.findByRoleName("STUDENT")).thenReturn(studentRole);
        when(userRepository.save(any(Users.class))).thenAnswer(i -> i.getArguments()[0]);

        Users savedUser = usersService.registerUser(user);

        assertEquals("encodedPwd", savedUser.getUserPassword());
        assertEquals("STUDENT", savedUser.getRole().getRoleName());
    }

    @Test
    void testRegisterUser_EmailExists() {
        Users user = new Users();
        user.setUserEmail("existing@gmail.com");

        when(userRepository.findByUserEmail("existing@gmail.com")).thenReturn(new Users());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usersService.registerUser(user);
        });

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void testLoadUserByUsername_Success() {
        Users user = new Users();
        user.setUserEmail("john@gmail.com");
        user.setUserPassword("hashed");
        Roles role = new Roles();
        role.setRoleName("STUDENT");
        user.setRole(role);

        when(userRepository.findByUserEmail("john@gmail.com")).thenReturn(user);

        UserDetails userDetails = usersService.loadUserByUsername("john@gmail.com");

        assertEquals("john@gmail.com", userDetails.getUsername());
        assertEquals("hashed", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("STUDENT")));
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUserEmail("notfound@gmail.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            usersService.loadUserByUsername("notfound@gmail.com");
        });
    }
}

package com.example.Learning.Management.System.Controller;

import com.example.Learning.Management.System.Controllers.AuthController;
import com.example.Learning.Management.System.Models.Users;
import com.example.Learning.Management.System.Service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private UsersService usersService;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHomePage() {
        assertEquals("home", authController.home());
    }

    @Test
    void testLoginPage() {
        assertEquals("login", authController.loginPage());
    }

    @Test
    void testRegister_Success() {
        Users user = new Users();
        doReturn(user).when(usersService).registerUser(any(Users.class));

        String result = authController.register(user, model);

        assertTrue(result.contains("redirect:/login")); 
    }

    @Test
    void testRegister_Failure() {
        Users user = new Users();
        doThrow(new RuntimeException("error")).when(usersService).registerUser(any(Users.class));

        String result = authController.register(user, model);

        assertTrue(result.contains("redirect:/home?error=true"));
    }
}

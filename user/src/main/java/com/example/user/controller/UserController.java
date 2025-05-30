package com.example.user.controller;

import java.util.List;

import com.example.user.dto.request.ApiResponRequest;
import com.example.user.dto.request.UserCreationRequest;
import com.example.user.dto.request.UserUpdateRequest;
import com.example.user.dto.response.UserResponse;
import com.example.user.service.UserService;

import org.springframework.security.core.context.SecurityContextHolder;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @PostMapping("/register")
    ApiResponRequest<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        return ApiResponRequest.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }
    @GetMapping
    ApiResponRequest<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponRequest.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponRequest<UserResponse> getMyInfo(){
        return ApiResponRequest.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }
    
    @GetMapping("/{userId}")
    ApiResponRequest<UserResponse> getUser(@PathVariable("userId") String userId){
        return ApiResponRequest.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }
    
    @PutMapping("/{userId}")
    ApiResponRequest<UserResponse> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request){
        return ApiResponRequest.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponRequest<String> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return ApiResponRequest.<String>builder()
                .result("User has been deleted")
                .build();
    }
}

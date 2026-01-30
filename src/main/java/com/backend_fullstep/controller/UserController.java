package com.backend_fullstep.controller;

import com.backend_fullstep.controller.request.UserCreationRequest;
import com.backend_fullstep.controller.request.UserPasswordRequest;
import com.backend_fullstep.controller.request.UserUpdateRequest;
import com.backend_fullstep.controller.response.ApiResponse;
import com.backend_fullstep.service.impl.EmailService;
import com.backend_fullstep.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name ="User Controller")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserServiceImpl userService;
    private final EmailService emailService;

    @Operation(summary = "Get user list", description = "API retrieve user from database")
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('admin', 'manager', 'user')")
    public ApiResponse getList (@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size
                                        ){

        log.info("Get user list by keyword={}, sort={}, page={}, size={}", keyword, sort, page, size);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user list")
                .data(userService.findAll(keyword, sort, page, size))
                .build();
    };

    @Operation(summary = "Get user detail", description = "API retrieve user detail by ID")
    @GetMapping("/{userId}")
    public ApiResponse getUserDetail (@PathVariable @Min(value = 1, message = "userId must be equals or greater than 1") Long userId){

        log.info("Get user detail by ID: {}", userId);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("user")
                .data(userService.findById(userId))
                .build();
    };

    @Operation(summary = "Create User", description = "API add new user to db")
    @PostMapping("/add")
    public ApiResponse createUser (@RequestBody @Valid UserCreationRequest request){
        log.info("Creating new user: {}", request);

        return ApiResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(userService.save(request))
                .build();
    }

    @Operation(summary = "Update User", description = "API update user to db")
    @PutMapping("/update")
    public ApiResponse updateUser (@RequestBody @Valid UserUpdateRequest request){
        log.info("Updating user: {}", request);

        userService.update(request);

        return ApiResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .message("User updated successfully")
                .data("")
                .build();
    }

    @Operation(summary = "Change Password User", description = "API change password user to db")
    @PatchMapping("/change-pwd")
    public ApiResponse changePassword (@RequestBody @Valid UserPasswordRequest request){
        log.info("ChangePassword requested {}", request);

        userService.changePassword(request);

        return ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Password updated successfully")
                .data("")
                .build();
    }

    @Operation(summary = "Inactive user", description = "API activate user to database")
    @DeleteMapping("/delete/{userId}")
    public ApiResponse deleteUser (@PathVariable @Min(value = 1, message = "userId must be equals or greater than 1") Long userId){

        log.info("Delete user by userId = {}", userId);

        userService.delete(userId);
        return ApiResponse.builder()
                .status(HttpStatus.RESET_CONTENT.value())
                .message("User deleted successfully")
                .data("")
                .build();
    }


}

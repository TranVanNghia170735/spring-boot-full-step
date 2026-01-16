package com.backend_fullstep.controller;

import com.backend_fullstep.controller.request.UserCreationRequest;
import com.backend_fullstep.controller.request.UserPasswordRequest;
import com.backend_fullstep.controller.request.UserUpdateRequest;
import com.backend_fullstep.service.impl.EmailService;
import com.backend_fullstep.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public Map<String, Object> getList (@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size
                                        ){

        log.info("Get user list by keyword={}, sort={}, page={}, size={}", keyword, sort, page, size);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userService.findAll(keyword, sort, page, size));
        return result;
    };

    @Operation(summary = "Get user detail", description = "API retrieve user detail by ID")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail (@PathVariable @Min(value = 1, message = "userId must be equals or greater than 1") Long userId){

        log.info("Get user detail by ID: {}", userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userService.findById(userId));
        return result;
    };

    @Operation(summary = "Create User", description = "API add new user to db")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createUser (@RequestBody @Valid UserCreationRequest request){
        log.info("Creating new user: {}", request);

        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User created successfully");
        result.put("data", userService.save(request));
        return new ResponseEntity<>(result,HttpStatus.CREATED);
    }

    @Operation(summary = "Update User", description = "API update user to db")
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser (@RequestBody @Valid UserUpdateRequest request){
        log.info("Updating user: {}", request);

        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User updated successfully");
        userService.update(request);
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Change Password User", description = "API change password user to db")
    @PatchMapping("/change-pwd")
    public Map<String, Object> changePassword (@RequestBody @Valid UserPasswordRequest request){
        log.info("ChangePassword requested {}", request);

        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "User change password successfully");
        userService.changePassword(request);
        return result;
    }

    @Operation(summary = "Inactive user", description = "API activate user to database")
    @DeleteMapping("/delete/{userId}")
    public Map<String, Object> deleteUser (@PathVariable @Min(value = 1, message = "userId must be equals or greater than 1") Long userId){

        log.info("Delete user by userId = {}", userId);

        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "User deleted successfully");
        userService.delete(userId);
        return result;
    }


}

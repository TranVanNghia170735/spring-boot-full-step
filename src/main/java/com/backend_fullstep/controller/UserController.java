package com.backend_fullstep.controller;

import com.backend_fullstep.controller.request.UserCreationRequest;
import com.backend_fullstep.controller.request.UserPasswordRequest;
import com.backend_fullstep.controller.request.UserUpdateRequest;
import com.backend_fullstep.controller.response.UserResponse;
import com.backend_fullstep.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name ="User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @Operation(summary = "Get user list", description = "API retrieve user from database")
    @GetMapping("/list")
    public Map<String, Object> getList (@RequestParam(required = false) String keyword,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size){
        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1l);
        userResponse1.setFirstName("Nghia");
        userResponse1.setLastName("Tran");
        userResponse1.setGender("Nam");
        userResponse1.setBirthday(new Date("07/17/1998"));
        userResponse1.setUsername("admin");
        userResponse1.setEmail("admin@gmail.com");
        userResponse1.setPhone("0123456789");


        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2l);
        userResponse2.setFirstName("NghiaTV2");
        userResponse2.setLastName("Tran2");
        userResponse2.setGender("Nam");
        userResponse2.setBirthday(new Date("07/18/1998"));
        userResponse2.setUsername("user");
        userResponse2.setEmail("user@gmail.com");
        userResponse2.setPhone("0987654321");
        List<UserResponse> userResponses = List.of(userResponse1, userResponse2);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userResponses);
        return result;
    };

    @Operation(summary = "Get user detail", description = "API retrieve user detail by ID")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail (@PathVariable Long userId
                                       ){
        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1l);
        userResponse1.setFirstName("Nghia");
        userResponse1.setLastName("Tran");
        userResponse1.setGender("Nam");
        userResponse1.setBirthday(new Date("07/17/1998"));
        userResponse1.setUsername("admin");
        userResponse1.setEmail("admin@gmail.com");
        userResponse1.setPhone("0123456789");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", userResponse1);
        return result;
    };
    @Operation(summary = "Create User", description = "API add new user to db")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createUser (@RequestBody UserCreationRequest request){
        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User created successfully");
        result.put("data", userService.save(request));
        return new ResponseEntity<>(result,HttpStatus.CREATED);
    }

    @Operation(summary = "Update User", description = "API update user to db")
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser (@RequestBody UserUpdateRequest request){
        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User updated successfully");
        userService.update(request);
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Change Password User", description = "API change password user to db")
    @PatchMapping("/change-pwd")
    public Map<String, Object> changePassword (@RequestBody UserPasswordRequest request){
        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "User change password successfully");
        userService.changePassword(request);
        return result;
    }

    @Operation(summary = "Inactive user", description = "API activate user to database")
    @DeleteMapping("/delete/{userId}")
    public Map<String, Object> deleteUser (@PathVariable Long userId){
        Map<String, Object>  result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "User deleted successfully");
        userService.delete(userId);
        return result;
    }
}

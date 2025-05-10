package com.example.demo.controllers;

import com.example.demo.dto.MembershipRequest;
import com.example.demo.models.User;
import com.example.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/api/user")
public class UserController {

    private final UserService userServ;

    @GetMapping(value = "/all")
    public ResponseEntity<Map<String,Object>> getAllUsers(){
        List<User> userList = userServ.getAllUsers();
        Map<String,Object> response = new HashMap<>();

        if(userList.isEmpty()){
            log.warn("User repository is Empty");
            response.put("message","User repository is Empty");
            response.put("users",userList);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        List<User> users = userList.stream()
                .map(user -> User.builder()
                        .id(user.getId())
                        .token(user.getToken())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());

        log.info("Retrieved all users :{}", users.size());
        response.put("message","Retrieved all users");
        response.put("users",users);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping(value = "/principal")
    public ResponseEntity<?> getCurrentUser(Principal principal){
        try{
            User currentUser = userServ.getCurrentUserRole(principal);

            Map<String, Object> response = new HashMap<>();
            if(currentUser.equals(null)){
                log.warn("User not found");
                response.put("message","User not found");
                response.put("user",currentUser);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

            User userDTO = currentUser.builder()
                    .id(currentUser.getEmail())
                    .token(currentUser.getToken())
                    .firstName(currentUser.getFirstName())
                    .lastName(currentUser.getLastName())
                    .email(currentUser.getEmail())
                    .role(currentUser.getRole())
                    .build();

            log.info("✔ Retrieved current user:{}",userDTO.getId());
            response.put("message","Retrieved current user");
            response.put("users",userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        }catch(Exception e){
            log.warn("An Error occurred : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id){
        try{
            User user = userServ.getUserById(id);
            log.info("Retrieved user by ID:{}", id);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (RuntimeException e) {
            log.warn("An Error occurred : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping(value = "/membership")
    public ResponseEntity<Map<String,Object>> addMembership(@RequestBody MembershipRequest request){
        Map<String,Object> response = new HashMap<>();
        User user = userServ.setMembership(request.getUserId(), request.getMonths());
        log.info("✔ Membership added");
        response.put("message","Membership added");
        response.put("user",user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/delete")
    public ResponseEntity<Map<String,Object>> delete(Principal principal){
        Map<String,Object> response = new HashMap<>();
        userServ.deleteUser(principal);
        log.info("✔ User deleted successfully");
        response.put("message","User deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

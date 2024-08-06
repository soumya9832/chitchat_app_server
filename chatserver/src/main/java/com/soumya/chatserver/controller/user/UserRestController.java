package com.soumya.chatserver.controller.user;

import com.soumya.chatserver.entity.User;
import com.soumya.chatserver.service.UserService;
import com.soumya.chatserver.util.Status;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserRestController {
    private final UserService service;
    public static User USER_NAME_TAKEN = new User(-1,"userName Taken","", Status.OFFLINE,null);

    @GetMapping("/users")
    public List<User> findAll() {
        return service.findAll();
    }


    // mapping for adding a user, post method
    @PostMapping("/users/post")
    public User addUser(@RequestBody User user) {

        //before adding a user, check if username already exist
        if (service.findByUserName(user.getUserName()) != null) {
            return USER_NAME_TAKEN;
        } else {
            service.saveUser(user);
            return user;
        }

    }

    // mapping for delete user with id
    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable int userId) {

        User tempUser = service.findById(userId);

        if (tempUser == null) {

            throw new RuntimeException(("The item to be deleted not found - " + userId));
        }
        service.deleteById(userId);

        return "Deleted user id - " + userId;
    }


    // mapping for put, update existing users
    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {

        service.saveUser(user);
        return user;
    }

    // mapping for get fcm-token of a specific user
    @GetMapping("/{username}/fcm-token")
    public ResponseEntity<String> getFcmToken(@PathVariable String username){
        String fcmToken = service.getFcmTokenByUsername(username);
        if(fcmToken!=null){
            return ResponseEntity.ok(fcmToken);
        }else{
            return ResponseEntity.notFound().build();
        }
    }


}

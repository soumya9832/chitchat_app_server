package com.soumya.chatserver.controller.user;

import com.soumya.chatserver.entity.User;
import com.soumya.chatserver.service.UserService;
import com.soumya.chatserver.util.Status;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserListWebSocketController {

    private final UserService service;

    @MessageMapping("/userList/status/listen")
    @SendTo("/topic/userList")
    public List<User> subscribeToUserList() {
        System.out.println("hello world");
        return service.findAll();
    }

    @MessageMapping("/userList/status/on")
    @SendTo("/topic/userList")
    public List<User> turnOnUserStatus(String idString) {

        int id = Integer.parseInt(idString);

        User user = service.findById(id);
        user.setStatus(Status.ONLINE);
        service.saveUser(user);
        return service.findAll();

    }

    @MessageMapping("/userList/status/off")
    @SendTo("/topic/userList")
    public List<User> turnOffUserStatus(String idString) {

        int id = Integer.parseInt(idString);

        User user = service.findById(id);
        user.setStatus(Status.OFFLINE);
        service.saveUser(user);
        return service.findAll();
    }

}

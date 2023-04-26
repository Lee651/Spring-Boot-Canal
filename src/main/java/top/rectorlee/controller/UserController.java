package top.rectorlee.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.rectorlee.entity.User;
import top.rectorlee.service.UserService;

/**
 * @author Lee
 * @description
 * @date 2023-04-26  10:57:51
 */
@Slf4j
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    public void addUser(@RequestBody User user) {
        userService.addUser(user);

        log.info("Add user: {}", user);
    }

    @RequestMapping("/update")
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);

        log.info("Update user: {}", user);
    }

    @RequestMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);

        log.info("Delete user: {}", id);
    }

    @RequestMapping("/find/{id}")
    public User findUser(@PathVariable("id") Integer id) {
        User user = userService.findUser(id);
        log.info("Find user: {}", user);

        return user;
    }
}

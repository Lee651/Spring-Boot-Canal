package top.rectorlee.service;

import top.rectorlee.entity.User;

public interface UserService {
    void addUser(User user);

    void updateUser(User user);

    void deleteUser(Integer id);

    User findUser(Integer id);
}

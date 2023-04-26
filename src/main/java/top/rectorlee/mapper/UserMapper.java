package top.rectorlee.mapper;

import top.rectorlee.entity.User;

public interface UserMapper {
    void addUser(User user);

    void updateUser(User user);

    void deleteUser(Integer id);

    User findUser(Integer id);
}

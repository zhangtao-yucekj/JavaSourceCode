package com.lagou.dao;

import com.lagou.pojo.User;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.util.List;

public interface IUserDao {

    //查询所有用户
    List<User> findAll() throws Exception;

    //根据条件进行用户查询
    User findByCondition(User user) throws Exception;

    //添加用户
    void addUser(User user);

    //通过ID删除用户
    void deleteUserById(Integer id);

    //更新用户
    void updateUser(User user);
}

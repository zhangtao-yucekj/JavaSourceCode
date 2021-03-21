package com.lagou.test;

import com.lagou.dao.IUserDao;
import com.lagou.io.Resources;
import com.lagou.pojo.User;
import com.lagou.sqlSession.SqlSession;
import com.lagou.sqlSession.SqlSessionFactory;
import com.lagou.sqlSession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {

    SqlSession sqlSession = null;

    @Before
    public void before() throws PropertyVetoException, DocumentException {
        InputStream resourceAsStream = Resources.getResourceAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        sqlSession = sqlSessionFactory.openSession();
    }

    @Test
    public void test() throws Exception {

        //调用
        User user = new User();
        user.setId(2);
        user.setUsername("zhangtao");

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        List<User> all = userDao.findAll();
        for (User user1 : all) {
            System.out.println(user1.getId() + " " + user1.getUsername());
        }

        System.out.println("*******************************************");

        User user1 = userDao.findByCondition(user);
        System.out.println(user1.getId() + " " + user1.getUsername());

    }

    /**
     * 添加用户
     */
    @Test
    public void testAddUser() {
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        User user = new User();
        user.setId(10);
        user.setUsername("tom10");
        mapper.addUser(user);
    }

    /**
     * 删除用户
     */
    @Test
    public void testDeleteUser() {
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        mapper.deleteUserById(10);
    }

    /**
     * 更新用户
     */
    @Test
    public void testUpdateUser() {
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        User user = new User();
        user.setId(8);
        user.setUsername("tom888");
        mapper.updateUser(user);
    }
}

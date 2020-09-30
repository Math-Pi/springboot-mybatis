package com.mybatis;

import com.mybatis.enums.UserSexEnum;
import com.mybatis.mapper.UserMapper;
import com.mybatis.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsert() throws Exception {
        userMapper.insert(new User("Jax", "123", UserSexEnum.MAN));
        userMapper.insert(new User("Luna", "111", UserSexEnum.WOMAN));
        userMapper.insert(new User("Amy", "222", UserSexEnum.WOMAN));

        Assert.assertEquals(3, userMapper.getAll().size());
    }

    @Test
    public void testQuery() throws Exception {
        List<User> users = userMapper.getAll();
        System.out.println(users.toString());
    }


    @Test
    public void testUpdate() throws Exception {
        User user = userMapper.getOne(1l);
        System.out.println(user.toString());
        user.setNickName("汤姆");
        userMapper.update(user);
        Assert.assertTrue(("汤姆".equals(userMapper.getOne(1l).getNickName())));
    }
}

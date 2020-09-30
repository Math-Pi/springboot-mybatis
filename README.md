# SpringBoot-Mybatis(注解版)

## 1、Maven依赖

- 主要依赖：**mybatis-spring-boot-starter**

```xml
<dependencies>
	<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
	<dependency>
		<groupId>org.mybatis.spring.boot</groupId>
		<artifactId>mybatis-spring-boot-starter</artifactId>
		<version>2.0.0</version>
	</dependency>
     <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

## 2、`application.properties` 

```properties
mybatis.type-aliases-package=com.mybatis.model

spring.datasource.url=jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Spring Boot 会自动加载 `spring.datasource.*` 相关配置，数据源就会自动注入到 sqlSessionFactory 中，sqlSessionFactory 会自动注入到 Mapper 中，对了，你一切都不用管了，直接拿起来使用就行了。

## 3、启动类

在启动类中添加对 mapper 包扫描`@MapperScan`

```java
@SpringBootApplication
@MapperScan("com.neo.mapper")
public class MybatisAnnotationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisAnnotationApplication.class, args);
	}
}
```

或者直接在 Mapper 类上面添加注解`@Mapper`，建议使用上面那种，不然每个 mapper 加个注解也挺麻烦的

## 4、Mapper接口文件

第三步是最关键的一块， Sql 生产都在这里

```java
public interface UserMapper {
	
	@Select("SELECT * FROM users")
	@Results({
		@Result(property = "userSex",  column = "user_sex", javaType = UserSexEnum.class),
		@Result(property = "nickName", column = "nick_name")
	})
	List<UserEntity> getAll();
	
	@Select("SELECT * FROM users WHERE id = #{id}")
	@Results({
		@Result(property = "userSex",  column = "user_sex", javaType = UserSexEnum.class),
		@Result(property = "nickName", column = "nick_name")
	})
	UserEntity getOne(Long id);

	@Insert("INSERT INTO users(userName,passWord,user_sex) VALUES(#{userName}, #{passWord}, #{userSex})")
	void insert(UserEntity user);

	@Update("UPDATE users SET userName=#{userName},nick_name=#{nickName} WHERE id =#{id}")
	void update(UserEntity user);

	@Delete("DELETE FROM users WHERE id =#{id}")
	void delete(Long id);

}
```

**为了更接近生产我特地将 user_sex、nick_name 两个属性在数据库加了下划线和实体类属性名不一致，另外 user_sex 使用了枚举**

> - @Select 是查询类的注解，所有的查询均使用这个
> - @Result 修饰返回的结果集，关联实体类属性和数据库字段一一对应，如果实体类属性和数据库属性名保持一致，就不需要这个属性来修饰。
> - @Insert 插入数据库使用，直接传入实体类会自动解析属性到对应的值
> - @Update 负责修改，也可以直接传入对象
> - @Delete 负责删除

[了解更多属性参考这里](http://www.mybatis.org/mybatis-3/zh/java-api.html)

> **注意，使用#符号和$符号的不同：**

```java
// This example creates a prepared statement, something like select * from teacher where name = ?;
@Select("Select * from teacher where name = #{name}")
Teacher selectTeachForGivenName(@Param("name") String name);

// This example creates n inlined statement, something like select * from teacher where name = 'someName';
@Select("Select * from teacher where name = '${name}'")
Teacher selectTeachForGivenName(@Param("name") String name);
```

- **\#{}是预编译处理，$ {}是字符串替换。mybatis在处理#{}时，会将sql中的#{}替换为?号，调用PreparedStatement的set方法来赋值；mybatis在处理 $ { } 时，就是把 ${ } 替换成变量的值。使用 #{} 可以有效的防止SQL注入，提高系统安全性。**

## 4、测试类

```java
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
```

- Assert.assertEquals();及其重载方法: 1. 如果两者一致, 程序继续往下运行. 2. 如果两者不一致, 中断测试方法, 抛出异常信息 AssertionFailedError .
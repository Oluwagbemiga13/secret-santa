package cz.oluwagbemiga.santa.be.mapper;

import cz.oluwagbemiga.santa.be.config.EmailMockConfig;
import cz.oluwagbemiga.santa.be.dto.UserDTO;
import cz.oluwagbemiga.santa.be.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

@SpringBootTest
@Import(EmailMockConfig.class)
class UserMapperTest extends AbstractMapperTest<User, UserDTO> {

    private UUID userId = UUID.randomUUID();

    @Autowired
    private UserMapper userMapper;

    @Override
    protected GenericMapper<User, UserDTO> mapper() {
        return userMapper;
    }

    @Override
    protected User entityInstance() {
        User user = new User();
        user.setUsername("username");
        user.setEmail("email");
        user.setPassword("pass");
        return user;
    }

    @Override
    protected UserDTO dtoInstance() {
        return new UserDTO("username", "email", "pass");
    }

    @Test
    void testMapper() {
        test();
    }
}
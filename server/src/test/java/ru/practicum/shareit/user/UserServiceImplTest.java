//package ru.practicum.shareit.user;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.web.server.ResponseStatusException;
//import ru.practicum.shareit.user.dto.UserDto;
//import ru.practicum.shareit.user.dto.UserDtoUpdate;
//import ru.practicum.shareit.user.service.UserServiceImpl;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureTestDatabase
//@ActiveProfiles("test")
//@Sql(scripts = {"file:src/main/resources/schema.sql"})
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class UserServiceImplTest {
//    private final UserServiceImpl userServiceImpl;
//    private static UserDto user1;
//    private static UserDto user2;
//    private static UserDtoUpdate updateUser1;
//
//    @BeforeEach
//    public void setUp() {
//        user1 = UserDto.builder()
//                .name("test name")
//                .email("test@test.ru")
//                .build();
//        user2 = UserDto.builder()
//                .name("test name 2")
//                .email("test2@test.ru")
//                .build();
//    }
//
//    @Test
//    public void createAndGetUserTest() {
//        //when
//        var savedUser = userServiceImpl.createUser(user1);
//        var findUser = userServiceImpl.getUserById(1L);
//        //then
//        assertThat(savedUser).usingRecursiveComparison().isEqualTo(findUser);
//        //usingRecursiveComparison(). позволяет сравнивать объекты рекурсивно, учитывая все их поля
//    }
//
//
//    @Test
//    public void createUserWithDuplicateEmailTest() {
//        //given
//        userServiceImpl.createUser(user1);
//        assertThatThrownBy(
//                //when
//                () -> userServiceImpl.createUser(user1))
//                //then
//                .isInstanceOf(DataIntegrityViolationException.class);
//    }
//
//    @Test
//    public void getNotExistUserByIdTest() {
//        assertThatThrownBy(
//                //when
//                () -> userServiceImpl.getUserById(2L))
//                //then
//                .isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    public void getEmptyUsersListTest() {
//        //when
//        var users = userServiceImpl.getAllUsers();
//        //then
//        assertThat(users.getUsers()).isEmpty();
//    }
//
//    @Test
//    public void getUsersListTest() {
//        //when
//        var savedUser1 = userServiceImpl.createUser(user1);
//        var savedUser2 = userServiceImpl.createUser(user2);
//        var findUsers = userServiceImpl.getAllUsers();
//        //then
//        assertThat(findUsers.getUsers()).element(0).usingRecursiveComparison().isEqualTo(savedUser1);
//        assertThat(findUsers.getUsers()).element(1).usingRecursiveComparison().isEqualTo(savedUser2);
//    }
//
//    @Test
//    public void updateUserTest() {
//        //given
//        updateUser1 = UserDtoUpdate.builder()
//                .name("update name")
//                .email("update-email@test.ru")
//                .build();
//        //when
//        userServiceImpl.createUser(user1);
//        userServiceImpl.updateUser(updateUser1, 1L);
//        var updatedUser1 = userServiceImpl.getUserById(1L);
//        assertThat(updatedUser1.getName()).isEqualTo(updateUser1.getName());
//        assertThat(updatedUser1.getEmail()).isEqualTo(updateUser1.getEmail());
//    }
//
//    @Test
//    public void updateUserNameTest() {
//        //given
//        updateUser1 = UserDtoUpdate.builder()
//                .email("update-email@test.ru")
//                .build();
//        //when
//        userServiceImpl.createUser(user1);
//        userServiceImpl.updateUser(updateUser1, 1L);
//        var updatedUser1 = userServiceImpl.getUserById(1L);
//        assertThat(updatedUser1.getName()).isEqualTo(user1.getName());
//        assertThat(updatedUser1.getEmail()).isEqualTo(updatedUser1.getEmail());
//    }
//
//    @Test
//    public void updateUserEmailTest() {
//        //given
//        updateUser1 = UserDtoUpdate.builder()
//                .name("update name")
//                .build();
//        //when
//        userServiceImpl.createUser(user1);
//        userServiceImpl.updateUser(updateUser1, 1L);
//        var updatedUser1 = userServiceImpl.getUserById(1L);
//        assertThat(updatedUser1.getName()).isEqualTo(updateUser1.getName());
//        assertThat(updatedUser1.getEmail()).isEqualTo(user1.getEmail());
//    }
//
//
//    @Test
//    public void deleteUserByIdTest() {
//        //given
//        var savedUser = userServiceImpl.createUser(user1);
//        //when
//        userServiceImpl.deleteUser(savedUser.getId());
//        //then
//        assertThatThrownBy(() -> userServiceImpl.getUserById(savedUser.getId())).isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    public void deleteUserByNotExistIdTest() {
//        assertThatThrownBy(
//                //when
//                () -> userServiceImpl.deleteUser(1L)
//        )
//                //then
//                .isInstanceOf(ResponseStatusException.class);
//    }
//}
//

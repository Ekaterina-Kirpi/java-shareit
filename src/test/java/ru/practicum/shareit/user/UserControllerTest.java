package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserControllerTest {
    private final ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userServiceImpl;
    private final MockMvc mvc;

    private static UserDtoResponse userDtoResponse;
    private static UserDto userDto;
    private static UserDtoUpdate userDtoUpdate;

    @BeforeEach
    public void setUp() {
        userDtoResponse = UserDtoResponse.builder()
                .id(1L)
                .name("test name")
                .email("test@test.ru")
                .build();
        userDto = UserDto.builder()
                .name("test name")
                .email("test@test.ru")
                .build();
        userDtoUpdate = UserDtoUpdate.builder()
                .name("test name")
                .email("test@test.ru")
                .build();
    }

    @Test
    public void createUserTest() throws Exception {
        when(userServiceImpl.createUser(any(UserDto.class))).thenReturn(userDtoResponse);
        //when
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isCreated(),
                        content().json(objectMapper.writeValueAsString(userDtoResponse)));

    }

    @Test
    public void createUserDuplicateTest() throws Exception {
        when(userServiceImpl.createUser(any(UserDto.class))).thenThrow(DataIntegrityViolationException.class);
        //when
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isInternalServerError()
                );
    }

    @Test
    public void createUserWithIncorrectNameTest() throws Exception {
        UserDto userDtoWithIncorrectName = UserDto.builder()
                .name("  incorrect name")
                .build();
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithIncorrectName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(userServiceImpl, times(0)).createUser(any(UserDto.class));
    }

    @Test
    public void createUserWithIncorrectEmailTest() throws Exception {
        UserDto userDtoWithIncorrectEmail = UserDto.builder()
                .name("test name")
                .email("incorrect-email@.ru")
                .build();
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithIncorrectEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(userServiceImpl, times(0)).createUser(any(UserDto.class));
    }

    @Test
    public void getUserByIdTest() throws Exception {
        when(userServiceImpl.getUserById(anyLong())).thenReturn(userDtoResponse);
        //when
        mvc.perform(get("/users/1"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoResponse))
                );
    }

    @Test
    public void getUserByNotExistingIdTest() throws Exception {
        when(userServiceImpl.getUserById(anyLong())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        //when
        mvc.perform(get("/users/1"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    public void getUserByIncorrectIdTest() throws Exception {
        when(userServiceImpl.getUserById(anyLong())).thenReturn(userDtoResponse);
        //when
        mvc.perform(get("/users/-1"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    public void getUsers() throws Exception {
        UserListDto userList = UserListDto.builder().users(List.of(userDtoResponse)).build();
        when(userServiceImpl.getAllUsers()).thenReturn(userList);
        //then
        mvc.perform(get("/users"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userList))
                );
    }

    @Test
    public void updateUserTest() throws Exception {
        when(userServiceImpl.updateUser(any(UserDtoUpdate.class), anyLong())).thenReturn(userDtoResponse);
        //then
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoResponse))
                );
    }

    @Test
    public void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isNoContent()
                );
        verify(userServiceImpl, times(1)).deleteUser(1L);
    }

}
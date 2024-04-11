package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void getAllUserTest() throws Exception {
        when(service.getAllUser())
                .thenAnswer(invocationOnMock -> {
                    List<User> usersResponse = new ArrayList<>();
                    usersResponse.add(new User(1L, "Пользователь 1", "user1@yandex.ru"));
                    usersResponse.add(new User(2L, "Пользователь 2", "user2@yandex.ru"));
                    usersResponse.add(new User(1L, "Пользователь 2", "user2@yandex.ru"));
                    return usersResponse;
                });

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void getUserTest() throws Exception {
        when(service.getUser(anyLong()))
                .thenAnswer(invocationOnMock -> {
                    User userResponse = new User();
                    userResponse.setId(invocationOnMock.getArgument(0, Long.class));
                    userResponse.setName("Пользователь 1");
                    userResponse.setEmail("user1@yandex.ru");
                    return userResponse;
                });

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Пользователь 1")))
                .andExpect(jsonPath("$.email", is("user1@yandex.ru")));
    }

    @Test
    void saveUserTest() throws Exception {
        UserDto userResearch = new UserDto("Пользователь 1", "user1@yandex.ru");
        when(service.saveUser(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    User userResponse = new User();
                    userResponse.setId(1L);
                    userResponse.setName(userDto.getName());
                    userResponse.setEmail(userDto.getEmail());
                    return userResponse;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResearch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(userResearch.getName())))
                .andExpect(jsonPath("$.email", is(userResearch.getEmail())));
    }

    @Test
    void saveUserTestExceptionValidationName() throws Exception {
        UserDto userResearch = new UserDto("", "user1@yandex.ru");
        when(service.saveUser(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    User userResponse = new User();
                    userResponse.setId(1L);
                    userResponse.setName(userDto.getName());
                    userResponse.setEmail(userDto.getEmail());
                    return userResponse;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResearch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveUserTestExceptionValidationEmailBlank() throws Exception {
        UserDto userResearch = new UserDto("Имя 1", "");
        when(service.saveUser(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    User userResponse = new User();
                    userResponse.setId(1L);
                    userResponse.setName(userDto.getName());
                    userResponse.setEmail(userDto.getEmail());
                    return userResponse;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResearch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveUserTestExceptionValidationEmail() throws Exception {
        UserDto userResearch = new UserDto("Имя 1", "user1yandex.ru");
        when(service.saveUser(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    User userResponse = new User();
                    userResponse.setId(1L);
                    userResponse.setName(userDto.getName());
                    userResponse.setEmail(userDto.getEmail());
                    return userResponse;
                });

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResearch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserTest() throws Exception {
        UserDto userResearch = new UserDto("Пользователь обновленный 1", "user1@yandex.ru");
        when(service.updateUser(anyLong(), any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(1, UserDto.class);
                    User userResponse = new User();
                    userResponse.setId(invocationOnMock.getArgument(0, Long.class));
                    userResponse.setName(userDto.getName());
                    userResponse.setEmail(userDto.getEmail());
                    return userResponse;
                });

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userResearch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(userResearch.getName())))
                .andExpect(jsonPath("$.email", is(userResearch.getEmail())));
    }

    @Test
    void removeUserTest() throws Exception {
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
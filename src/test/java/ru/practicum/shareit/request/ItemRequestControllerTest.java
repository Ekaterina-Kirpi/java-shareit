package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private final ItemRequestServiceImpl itemRequestServiceImpl;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDtoResponse itemRequestDtoResponse;
    private ItemRequestListDto itemRequestListDto;
    private RequestDtoResponse requestDtoResponse;
    private ItemForRequestDto itemForRequestDto;
    private final String userIdHeader = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .description("test description")
                .build();
        itemRequestDtoResponse = ItemRequestDtoResponse.builder()
                .id(1L)
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
        requestDtoResponse = RequestDtoResponse.builder()
                .id(1L)
                .description(itemRequestDtoResponse.getDescription())
                .created(itemRequestDtoResponse.getCreated())
                .build();
        itemForRequestDto = ItemForRequestDto.builder()
                .id(1L)
                .name("test item name")
                .description("test description name")
                .requestId(1L)
                .available(Boolean.TRUE)
                .build();
    }

    @Test
    @SneakyThrows
    public void createRequestTest() {
        //when
        when(itemRequestServiceImpl.createItemRequest(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestDtoResponse);

        mvc.perform(post("/requests")
                        .header(userIdHeader, 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                //then
                .andExpectAll(status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemRequestDtoResponse)));
    }

    @Test
    @SneakyThrows
    public void createRequestWitchIncorrectUserIdTest() {
        //when
        mvc.perform(post("/requests")
                        .header(userIdHeader, 0)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(itemRequestServiceImpl, times(0)).createItemRequest(any(ItemRequestDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    public void createRequestWhenIncorrectDescriptionTest() {
        //given
        itemRequestDto.setDescription(" ");
        //when
        mvc.perform(post("/requests")
                        .header(userIdHeader, 0)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(itemRequestServiceImpl, times(0)).createItemRequest(any(ItemRequestDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    public void getPrivateRequestsTest() {
        //given
        requestDtoResponse.setItems(List.of(itemForRequestDto));
        itemRequestListDto = ItemRequestListDto.builder()
                .requests(List.of(requestDtoResponse))
                .build();
        //when
        when(itemRequestServiceImpl.getOwnerRequests(anyLong(), any(Integer.class), any(Integer.class))).thenReturn(itemRequestListDto);
        mvc.perform(get("/requests")
                        .header(userIdHeader, 1)
                        .param("from", "0")
                        .param("size", "2")).andDo(print())
                //then
                .andExpectAll(status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemRequestListDto)));
    }

    @Test
    @SneakyThrows
    public void getPrivateRequestsWithIncorrectUserIdTest() {
        //when
        mvc.perform(get("/requests")
                        .header(userIdHeader, 0)
                        .param("from", "0")
                        .param("size", "2")).andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(itemRequestServiceImpl, times(0)).getOwnerRequests(anyLong(), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getPrivateRequestsWithIncorrectParamFromTest() {
        //when
        mvc.perform(get("/requests")
                        .header(userIdHeader, 1)
                        .param("from", "-1")
                        .param("size", "2")).andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(itemRequestServiceImpl, times(0)).getOwnerRequests(anyLong(), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getPrivateRequestsWithIncorrectParamSizeTest() {
        //when
        mvc.perform(get("/requests")
                        .header(userIdHeader, 1)
                        .param("from", "0")
                        .param("size", "-1")).andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(itemRequestServiceImpl, times(0)).getOwnerRequests(anyLong(), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getOtherRequestsTest() {
        //given
        requestDtoResponse.setItems(Collections.singletonList(itemForRequestDto));
        itemRequestListDto = ItemRequestListDto.builder()
                .requests(List.of(requestDtoResponse))
                .build();
        //when
        when(itemRequestServiceImpl.getUserRequests(anyLong(), any(Integer.class), any(Integer.class))).thenReturn(itemRequestListDto);
        mvc.perform(get("/requests/all")
                        .header(userIdHeader, 1)
                        .param("from", "0")
                        .param("size", "2")).andDo(print())
                //then
                .andExpectAll(status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemRequestListDto)));
    }

    @Test
    @SneakyThrows
    public void getOtherRequestsWitchIncorrectUserIdTest() {
        //when
        mvc.perform(get("/requests/all")
                        .header(userIdHeader, 0)
                        .param("from", "0")
                        .param("size", "2")).andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(itemRequestServiceImpl, times(0)).getUserRequests(anyLong(), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getOtherRequestsWitchIncorrectParamFromTest() {
        //when
        mvc.perform(get("/requests/all")
                        .header(userIdHeader, 1)
                        .param("from", "-1")
                        .param("size", "2")).andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(itemRequestServiceImpl, times(0)).getUserRequests(anyLong(), any(Integer.class), any(Integer.class));
    }


    @Test
    @SneakyThrows
    public void getItemRequestTest() {
        //given
        requestDtoResponse.setItems(Collections.singletonList(itemForRequestDto));
        //when
        when(itemRequestServiceImpl.getItemRequestById(anyLong(), anyLong())).thenReturn(requestDtoResponse);
        mvc.perform(get("/requests/1")
                        .header(userIdHeader, 1)).andDo(print())
                //then
                .andExpectAll(status().isOk(),
                        content().json(objectMapper.writeValueAsString(requestDtoResponse)));
    }

    @Test
    @SneakyThrows
    public void getItemRequestWitchIncorrectUserIdTest() {
        //when
        mvc.perform(get("/requests/1")
                        .header(userIdHeader, 0)).andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(itemRequestServiceImpl, times(0)).getItemRequestById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getItemRequestWitchIncorrectItemRequestIdTest() {
        //when
        mvc.perform(get("/requests/0")
                        .header(userIdHeader, 1)).andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(itemRequestServiceImpl, times(0)).getItemRequestById(anyLong(), anyLong());
    }
}


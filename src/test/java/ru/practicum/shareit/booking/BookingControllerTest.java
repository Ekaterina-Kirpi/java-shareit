package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;
import ru.practicum.shareit.booking.enam.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.dto.ItemLimitDto;
import ru.practicum.shareit.user.dto.UserLimitDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private final BookingServiceImpl bookingServiceImpl;
    private static BookingDto bookingDto;
    private BookingListDto bookingListDto;
    private static BookingDtoResponse bookingDtoResponse;
    private final String userIdHeader = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {
        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        ItemLimitDto itemLimitDto = ItemLimitDto.builder()
                .id(bookingDto.getItemId())
                .name("test item")
                .build();
        UserLimitDto userLimitDto = UserLimitDto.builder()
                .id(1L)
                .name("test name")
                .build();
        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(itemLimitDto)
                .booker(userLimitDto)
                .status(bookingDto.getStatus())
                .build();
    }

    @Test
    @SneakyThrows
    public void createBookingTest() {
        //when
        when(bookingServiceImpl.createBooking(anyLong(), any(BookingDto.class))).thenReturn(bookingDtoResponse);
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userIdHeader, 1))
                .andDo(print())
                //then
                .andExpectAll(status().isCreated(), content().json(objectMapper.writeValueAsString(bookingDtoResponse)));
    }

    @Test
    @SneakyThrows
    public void createBookingWithIncorrectBookerIdTest() {
        //when
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userIdHeader, 0))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0)).createBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    public void createBookingWithIncorrectStartTest() {
        //given
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        //when
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userIdHeader, 1))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0)).createBooking(anyLong(), any(BookingDto.class));
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
    }

    @Test
    @SneakyThrows
    public void createBookingWithIncorrectEndTest() {
        //given
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        //when
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userIdHeader, 1))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0)).createBooking(anyLong(), any(BookingDto.class));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    @SneakyThrows
    public void createBookingWithIncorrectItemIdTest() {
        //given
        bookingDto.setItemId(null);
        //when
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userIdHeader, 1))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0)).createBooking(anyLong(), any(BookingDto.class));
        bookingDto.setItemId(1L);
    }

    @Test
    @SneakyThrows
    public void approveBookingTest() {
        //given
        bookingDtoResponse.setStatus(Status.APPROVED);
        //when
        when(bookingServiceImpl.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoResponse);
        mvc.perform((patch("/bookings/1"))
                        .header(userIdHeader, 1)
                        .param("approved", "true"))
                .andDo(print())
                //then
                .andExpectAll(status().isOk(), content().json(objectMapper.writeValueAsString(bookingDtoResponse)));
        bookingDtoResponse.setStatus(Status.WAITING);
    }

    @Test
    @SneakyThrows
    public void approveBookingWitchIncorrectUserIdTest() {
        //when
        mvc.perform((patch("/bookings/1"))
                        .header(userIdHeader, 0)
                        .param("approved", "true"))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    public void approveBookingWitchIncorrectBookingIdTest() {
        //when
        mvc.perform((patch("/bookings/0"))
                        .header(userIdHeader, 1)
                        .param("approved", "true"))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdForOwnerAndBookerTest() {
        //when
        when(bookingServiceImpl.getBookingById(anyLong(), anyLong())).thenReturn(bookingDtoResponse);
        mvc.perform(get("/bookings/1")
                        .header(userIdHeader, 1))
                .andDo(print())
                //then
                .andExpectAll(status().isOk(), content().json(objectMapper.writeValueAsString(bookingDtoResponse)));
    }

    @Test
    @SneakyThrows
    public void getBookingByIncorrectBookingIdForOwnerAndBookerTest() {
        //when
        mvc.perform(get("/bookings/0")
                        .header(userIdHeader, 1))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0)).getBookingById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWithIncorrectUserIdForOwnerAndBookerTest() {
        //when
        mvc.perform(get("/bookings/1")
                        .header(userIdHeader, 0))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0)).getBookingById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWithOutUserIdForOwnerAndBookerTest() {
        //when
        mvc.perform(get("/bookings/1"))
                .andDo(print())
                //then
                .andExpectAll(status().isInternalServerError());
        verify(bookingServiceImpl, times(0)).getBookingById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserTest() {
        //given
        bookingListDto = BookingListDto.builder()
                .bookings(List.of(bookingDtoResponse))
                .build();
        //when
        when(bookingServiceImpl.getAllBookings(anyLong(), anyString(), any(Integer.class), any(Integer.class)))
                .thenReturn(bookingListDto);
        mvc.perform(get("/bookings")
                        .header(userIdHeader, 1)
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                //then
                .andExpectAll(status().isOk(), content().json(objectMapper.writeValueAsString(bookingListDto)));
    }


    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectStateTest() {
        //given
        bookingListDto = BookingListDto.builder()
                .bookings(List.of(bookingDtoResponse))
                .build();
        //when
        when(bookingServiceImpl.getAllBookings(anyLong(), anyString(), any(Integer.class), any(Integer.class)))
                .thenThrow(StateException.class);
        mvc.perform(get("/bookings")
                        .header(userIdHeader, 1)
                        .param("from", "0")
                        .param("size", "2")
                        .param("state", "qwe"))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectUserIdTest() {
        //when
        mvc.perform(get("/bookings")
                        .header(userIdHeader, 0)
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                //then
                .andExpectAll(
                        status().isBadRequest());
        verify(bookingServiceImpl, times(0)).getAllBookings(anyLong(), anyString(), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectParamTest() {
        //when
        mvc.perform(get("/bookings")
                        .header(userIdHeader, 1)
                        .param("from", "-1")
                        .param("size", "2"))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0)).getAllBookings(anyLong(), anyString(), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUserWithIncorrectUserIdTest() {
        //when
        mvc.perform(get("/bookings")
                        .header(userIdHeader, 0)
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0))
                .getAllBookingsOfOwner(anyLong(), anyString(), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUserWithIncorrectParamTest() {
        //when
        mvc.perform(get("/bookings/owner")
                        .header(userIdHeader, 1)
                        .param("from", "-1")
                        .param("size", "2"))
                .andDo(print())
                //then
                .andExpectAll(status().isBadRequest());
        verify(bookingServiceImpl, times(0))
                .getAllBookingsOfOwner(anyLong(), anyString(), any(Integer.class), any(Integer.class));
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUserTest() {
        //given
        bookingListDto = BookingListDto.builder()
                .bookings(List.of(bookingDtoResponse))
                .build();
        //when
        when(bookingServiceImpl.getAllBookingsOfOwner(anyLong(), anyString(), any(Integer.class), any(Integer.class))).thenReturn(bookingListDto);
        mvc.perform(get("/bookings/owner")
                        .header(userIdHeader, 1)
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                //then
                .andExpectAll(status().isOk(), content().json(objectMapper.writeValueAsString(bookingListDto)));
    }

}

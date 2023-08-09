package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enam.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ActiveProfiles("unit-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingServiceImplUnitTest extends Bookings {

    private BookingService service;
    private final BookingMapper mapper;

    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private BookingDto booking1Dto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new BookingServiceImpl(
                bookingRepository, userRepository, itemRepository,
                mapper);
        user1 = new User();
        user1.setId(1L);
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setId(2L);
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        item1 = new Item();
        item1.setId(1L);
        item1.setName("test item");
        item1.setDescription("test item description");
        item1.setAvailable(Boolean.TRUE);
        item1.setOwner(user1);
        item2 = new Item();
        item2.setId(2L);
        item2.setName("test item2");
        item2.setDescription("test item2 description");
        item2.setAvailable(Boolean.TRUE);
        item2.setOwner(user2);
        booking1Dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        currentBookingForItem1 = mapper.toBookingFromBookingDto(booking1Dto);
    }

    @Test
    void createBookingTest() {
        BookingDtoResponse expected = mapper.toBookingDtoResponseFromBooking(currentBookingForItem1);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.save(any())).thenReturn(currentBookingForItem1);

        BookingDtoResponse actual = service.createBooking(3L, booking1Dto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void createBookingByInvalidBookingDtoDatesTest() {
        String expectedMessage = " \"Дата окончания бронирования не может быть раньше даты начала или равна ей\"";
        booking1Dto.setEnd(LocalDateTime.now());
        booking1Dto.setStart(LocalDateTime.now());

        assertThatThrownBy(() -> service.createBooking(3L, booking1Dto))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.BAD_REQUEST + expectedMessage);
    }

    @Test
    void createBookingByInvalidItemIdTest() {
        String expectedMessage = " \"Вещь " + booking1Dto.getItemId() + " не найдена\"";

        assertThatThrownBy(() -> service.createBooking(3L, booking1Dto))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
    }

    @Test
    void createBookingByOwnerIdEqualsBookerIdTest() {
        String expectedMessage = " \"Владелец не может бронировать свои вещи\"";

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThatThrownBy(() -> service.createBooking(1L, booking1Dto))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
    }

    @Test
    void createBookingByNotAvailableItemTest() {
        item1.setAvailable(false);
        String expectedMessage = " \"Вещь " + item1.getId() + " недоступна для бронирования\"";

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThatThrownBy(() -> service.createBooking(3L, booking1Dto))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.BAD_REQUEST + expectedMessage);
    }

    @Test
    void createBookingByInvalidBookerIdTest() {
        Long invalidBookerId = 3L;
        String expectedMessage = " \"Пользователь " + invalidBookerId + " не найден\"";

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThatThrownBy(() -> service.createBooking(invalidBookerId, booking1Dto))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
    }

    @Test
    void approveBookingByApprovedTest() {
        currentBookingForItem1.setStatus(Status.WAITING);
        currentBookingForItem1.setItem(item1);
        BookingDtoResponse expected = mapper.toBookingDtoResponseFromBooking(currentBookingForItem1);
        expected.setStatus(Status.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(currentBookingForItem1));
        when(bookingRepository.save(any())).thenReturn(currentBookingForItem1);

        BookingDtoResponse actual = service.approveBooking(user1.getId(), 3L, true);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void approveBookingByApprovedFalseTest() {
        currentBookingForItem1.setStatus(Status.WAITING);
        currentBookingForItem1.setItem(item1);
        BookingDtoResponse expected = mapper.toBookingDtoResponseFromBooking(currentBookingForItem1);
        expected.setStatus(Status.REJECTED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(currentBookingForItem1));
        when(bookingRepository.save(any())).thenReturn(currentBookingForItem1);

        BookingDtoResponse actual = service.approveBooking(user1.getId(), 3L, false);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void approveBookingByInvalidBookingIdTest() {
        String expectedMessage = " \"Бронирование " + 3L + " не найдено\"";

        assertThatThrownBy(() -> service.approveBooking(user1.getId(), 3L, true))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
    }

    @Test
    void approveBookingByInvalidStatusTest() {
        currentBookingForItem1.setStatus(Status.APPROVED);
        String expectedMessage = " \"Невозможно изменить статус, когда бронирование в статусе: "
                + currentBookingForItem1.getStatus() + "\"";

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(currentBookingForItem1));

        assertThatThrownBy(() -> service.approveBooking(user1.getId(), 3L, true))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.BAD_REQUEST + expectedMessage);
    }

    @Test
    void approveBookingByInvalidOwnerIdTest() {
        currentBookingForItem1.setStatus(Status.WAITING);
        currentBookingForItem1.setItem(item1);
        String expectedMessage = " \"Пользователь " + 999L +
                " не является владельцем вещи " + currentBookingForItem1.getItem().getOwner().getId() + "\"";
        BookingDtoResponse expected = mapper.toBookingDtoResponseFromBooking(currentBookingForItem1);
        expected.setStatus(Status.REJECTED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(currentBookingForItem1));

        assertThatThrownBy(() -> service.approveBooking(999L, 3L, true))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
    }

    @Test
    void getBookingByIdTest() {
        currentBookingForItem1.setBooker(user1);
        BookingDtoResponse expected = mapper.toBookingDtoResponseFromBooking(currentBookingForItem1);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(currentBookingForItem1));

        BookingDtoResponse actual = service.getBookingById(1L, user1.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getBookingByInvalidBookingIdTest() {
        String expectedMessage = " \"Бронирование " + 1L + " не найдено " + "¯\\_(ツ)_/¯\"";

        assertThatThrownBy(() -> service.getBookingById(1L, 3L))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
    }

    @Test
    void getBookingByInvalidUserIdTest() {
        currentBookingForItem1.setBooker(user2);
        currentBookingForItem1.setItem(item2);
        String expectedMessage = " \"Пользователь " + user1.getId() + " не бронировал и не является владелецем забронированной вещи\"";

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(currentBookingForItem1));

        assertThatThrownBy(() -> service.getBookingById(1L, user1.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .message()
                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
    }

}
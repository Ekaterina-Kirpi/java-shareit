package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enam.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImplIntegrationTest extends Bookings {
    private final BookingServiceImpl bookingServiceImpl;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private BookingDto booking1Dto;


    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        item1 = new Item();
        item1.setName("test item");
        item1.setDescription("test item description");
        item1.setAvailable(Boolean.TRUE);
        item1.setOwner(user1);
        item2 = new Item();
        item2.setName("test item2");
        item2.setDescription("test item2 description");
        item2.setAvailable(Boolean.TRUE);
        item2.setOwner(user2);
        booking1Dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
    }

    @Test
    public void createAndGetBookingTest() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        //when
        var savedBooking = bookingServiceImpl.createBooking(user2.getId(), booking1Dto);
        var findBooking = bookingServiceImpl
                .getBookingById(savedBooking.getId(), user2.getId());
        //then
        assertThat(savedBooking).usingRecursiveComparison().ignoringFields("start", "end")
                .isEqualTo(findBooking);
    }

    @Test
    public void createBookingWhenEndBeforeStartTest() {
        //given
        booking1Dto.setEnd(LocalDateTime.now().plusDays(1));
        booking1Dto.setStart(LocalDateTime.now().plusDays(2));
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        assertThatThrownBy(
                //when
                () -> bookingServiceImpl.createBooking(user2.getId(), booking1Dto)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void createBookingWithNotExistingItemTest() {
        //given
        booking1Dto.setItemId(2L);
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        assertThatThrownBy(
                //when
                () -> bookingServiceImpl.createBooking(user2.getId(), booking1Dto)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void createBookingWhenBookerIsOwnerTest() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        assertThatThrownBy(
                //when
                () -> bookingServiceImpl.createBooking(user1.getId(), booking1Dto)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void createBookingWhenNotExistingBookerTest() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        assertThatThrownBy(
                //when
                () -> bookingServiceImpl.createBooking(99L, booking1Dto)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @SneakyThrows
    public void createBookingWithNotAvailableItemTest() {
        // given
        item1.setAvailable(Boolean.FALSE);
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        // when
        Throwable throwable = catchThrowable(() -> bookingServiceImpl.createBooking(user2.getId(), booking1Dto));

        // then
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void approveBookingTest() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        var savedBooking = bookingServiceImpl.createBooking(user2.getId(), booking1Dto);
        //when
        var approvedBooking = bookingServiceImpl
                .approveBooking(user1.getId(), savedBooking.getId(), true);
        var findBooking = bookingServiceImpl
                .getBookingById(savedBooking.getId(), user2.getId());
        //then
        assertThat(approvedBooking).usingRecursiveComparison().isEqualTo(findBooking);
    }

    @Test
    public void rejectBookingTest() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        var savedBooking = bookingServiceImpl.createBooking(user2.getId(), booking1Dto);
        //when
        var approvedBooking = bookingServiceImpl
                .approveBooking(user1.getId(), savedBooking.getId(), false);
        var findBooking = bookingServiceImpl
                .getBookingById(savedBooking.getId(), user2.getId());
        //then
        assertThat(approvedBooking).usingRecursiveComparison().isEqualTo(findBooking);
    }

    @Test
    public void approveBookingWithNotExistingBookingTest() {
        // given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        bookingServiceImpl.createBooking(user2.getId(), booking1Dto);

        // when & then
        assertThatThrownBy(() -> bookingServiceImpl.approveBooking(user1.getId(), 99L, true))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void approveBookingWhenBookingIsNotWaitingTest() {
        // given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        var savedBooking = bookingServiceImpl.createBooking(user2.getId(), booking1Dto);
        bookingServiceImpl.approveBooking(user1.getId(), savedBooking.getId(), false);

        // when & then
        assertThatThrownBy(() -> bookingServiceImpl.approveBooking(user1.getId(), savedBooking.getId(), true))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void approveBookingWhenUserIsNotOwnerTest() {
        // given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        var savedBooking = bookingServiceImpl.createBooking(user2.getId(), booking1Dto);

        // when & then
        assertThatThrownBy(() -> bookingServiceImpl.approveBooking(user2.getId(), savedBooking.getId(), true))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getBookingWhenBookingNotFoundTest() {
        // given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        bookingServiceImpl.createBooking(user2.getId(), booking1Dto);

        // when & then
        assertThatThrownBy(() -> bookingServiceImpl.getBookingById(99L, user2.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getBookingWhenUserIsNotOwnerOrBookerTest() {
        // given
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        var savedBooking = bookingServiceImpl.createBooking(user2.getId(), booking1Dto);

        // when & then
        assertThatThrownBy(() -> bookingServiceImpl.getBookingById(savedBooking.getId(), 10L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getAllBookingForUserWhenStateIsAllTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookings(user2.getId(), "ALL", 0, 10);
        //then
        assertThat(findBookingList.getBookings().size()).isEqualTo(10);
        List<Long> listId
                = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).first().isEqualTo(futureBookingForItem2.getId());
        assertThat(listId).element(1).isEqualTo(futureBookingForItem1.getId());
        assertThat(listId).element(2).isEqualTo(rejectedBookingForItem2.getId());
        assertThat(listId).element(3).isEqualTo(rejectedBookingForItem1.getId());
        assertThat(listId).element(4).isEqualTo(waitingBookingForItem2.getId());
        assertThat(listId).element(5).isEqualTo(waitingBookingForItem1.getId());
        assertThat(listId).element(6).isEqualTo(currentBookingForItem2.getId());
        assertThat(listId).element(7).isEqualTo(currentBookingForItem1.getId());
        assertThat(listId).element(9).isEqualTo(pastBookingForItem1.getId());
        assertThat(listId).element(8).isEqualTo(pastBookingForItem2.getId());
        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    public void getAllBookingsForItemsUserTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookingsOfOwner(user1.getId(), "ALL", 0, 10);
        //then
        assertThat(findBookingList.getBookings().size()).isEqualTo(5);
        List<Long> listId
                = findBookingList.getBookings().stream().map(BookingDtoResponse::getId)
                .collect(Collectors.toList());
        assertThat(listId).first().isEqualTo(futureBookingForItem1.getId());
        assertThat(listId).element(1).isEqualTo(rejectedBookingForItem1.getId());
        assertThat(listId).element(2).isEqualTo(waitingBookingForItem1.getId());
        assertThat(listId).element(3).isEqualTo(currentBookingForItem1.getId());
        assertThat(listId).element(4).isEqualTo(pastBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsCurrentTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookings(user2.getId(), "CURRENT", 0, 10);
        //then
        assertThat(findBookingList.getBookings().size()).isEqualTo(2);
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).first().isEqualTo(currentBookingForItem2.getId());
        assertThat(listId).last().isEqualTo(currentBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsCurrentTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookingsOfOwner(user1.getId(), "CURRENT", 0, 10);
        //then
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).singleElement().isEqualTo(currentBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsPastTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookings(user2.getId(), "PAST", 0, 10);
        //then
        assertThat(findBookingList.getBookings().size()).isEqualTo(2);
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).first().isEqualTo(pastBookingForItem2.getId());
        assertThat(listId).last().isEqualTo(pastBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsPastTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookingsOfOwner(user1.getId(), "PAST", 0, 10);
        //then
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).singleElement().isEqualTo(pastBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsFutureTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookings(user2.getId(), "Future", 0, 10);
        //then
        assertThat(findBookingList.getBookings().size()).isEqualTo(6);
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).first().isEqualTo(futureBookingForItem2.getId());
        assertThat(listId).element(1).isEqualTo(futureBookingForItem1.getId());
        assertThat(listId).element(2).isEqualTo(rejectedBookingForItem2.getId());
        assertThat(listId).element(3).isEqualTo(rejectedBookingForItem1.getId());
        assertThat(listId).element(4).isEqualTo(waitingBookingForItem2.getId());
        assertThat(listId).element(5).isEqualTo(waitingBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsFutureTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookingsOfOwner(user1.getId(), "Future", 0, 10);
        //then
        assertThat(findBookingList.getBookings().size()).isEqualTo(3);
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).first().isEqualTo(futureBookingForItem1.getId());
        assertThat(listId).element(1).isEqualTo(rejectedBookingForItem1.getId());
        assertThat(listId).element(2).isEqualTo(waitingBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsWaitingTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookings(user2.getId(), "waiting", 0, 10);
        //then
        assertThat(findBookingList.getBookings().size()).isEqualTo(2);
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).first().isEqualTo(waitingBookingForItem2.getId());
        assertThat(listId).last().isEqualTo(waitingBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsWaitingTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookingsOfOwner(user1.getId(), "waiting", 0, 10);
        //then
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).singleElement().isEqualTo(waitingBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsRejected() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookings(user2.getId(), "rejected", 0, 10);
        //then
        assertThat(findBookingList.getBookings().size()).isEqualTo(2);
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).first().isEqualTo(rejectedBookingForItem2.getId());
        assertThat(listId).last().isEqualTo(rejectedBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsRejectedTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        //when
        var findBookingList = bookingServiceImpl
                .getAllBookingsOfOwner(user1.getId(), "rejected", 0, 10);
        //then
        List<Long> listId = findBookingList.getBookings().stream().map(BookingDtoResponse::getId).collect(Collectors.toList());
        assertThat(listId).singleElement().isEqualTo(rejectedBookingForItem1.getId());
    }

    @Test
    public void getBookingListWithUnknownStateTest() {
        userRepository.save(user1);
        assertThatThrownBy(
                () -> bookingServiceImpl.getAllBookings(user1.getId(), "qwety", 0, 10)
        ).isInstanceOf(StateException.class);
    }

    @Test
    public void getAllBookingsForUserWhenUserNotFoundTest() {
        userRepository.save(user1);
        assertThatThrownBy(
                () -> bookingServiceImpl.getAllBookings(50L, "ALL", 0, 10)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getAllBookingsForItemsUserWhenUserNotFoundTest() {
        //given
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        createBookingsInDb();
        assertThatThrownBy(
                () -> bookingServiceImpl.getAllBookingsOfOwner(50L, "ALL", 0, 10)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getAllBookingsForItemsUserWhenUserNotExistingBookingTest() {
        //given
        userRepository.save(user1);
        assertThatThrownBy(
                () -> bookingServiceImpl.getAllBookingsOfOwner(user1.getId(), "ALL", 0, 10)
        ).isInstanceOf(RuntimeException.class);
    }

    @SneakyThrows
    private void initializationItem2AndBookings() {

        currentBookingForItem1 = new Booking();
        currentBookingForItem1.setStart(LocalDateTime.now().minusDays(1));
        currentBookingForItem1.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingForItem1.setItem(item1);
        currentBookingForItem1.setBooker(user2);
        currentBookingForItem1.setStatus(Status.APPROVED);

        Thread.sleep(50);

        currentBookingForItem2 = new Booking();
        currentBookingForItem2.setStart(LocalDateTime.now().minusDays(1));
        currentBookingForItem2.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingForItem2.setItem(item2);
        currentBookingForItem2.setBooker(user2);
        currentBookingForItem2.setStatus(Status.APPROVED);

        Thread.sleep(50);

        pastBookingForItem1 = new Booking();
        pastBookingForItem1.setStart(LocalDateTime.now().minusDays(2));
        pastBookingForItem1.setEnd(LocalDateTime.now().minusDays(1));
        pastBookingForItem1.setItem(item1);
        pastBookingForItem1.setBooker(user2);
        pastBookingForItem1.setStatus(Status.APPROVED);

        Thread.sleep(50);

        pastBookingForItem2 = new Booking();
        pastBookingForItem2.setStart(LocalDateTime.now().minusDays(2));
        pastBookingForItem2.setEnd(LocalDateTime.now().minusDays(1));
        pastBookingForItem2.setItem(item2);
        pastBookingForItem2.setBooker(user2);
        pastBookingForItem2.setStatus(Status.APPROVED);

        Thread.sleep(50);

        futureBookingForItem1 = new Booking();
        futureBookingForItem1.setStart(LocalDateTime.now().plusDays(1));
        futureBookingForItem1.setEnd(LocalDateTime.now().plusDays(2));
        futureBookingForItem1.setItem(item1);
        futureBookingForItem1.setBooker(user2);
        futureBookingForItem1.setStatus(Status.APPROVED);

        Thread.sleep(50);

        futureBookingForItem2 = new Booking();
        futureBookingForItem2.setStart(LocalDateTime.now().plusDays(1));
        futureBookingForItem2.setEnd(LocalDateTime.now().plusDays(2));
        futureBookingForItem2.setItem(item2);
        futureBookingForItem2.setBooker(user2);
        futureBookingForItem2.setStatus(Status.APPROVED);

        Thread.sleep(50);

        waitingBookingForItem1 = new Booking();
        waitingBookingForItem1.setStart(LocalDateTime.now().plusHours(1));
        waitingBookingForItem1.setEnd(LocalDateTime.now().plusHours(2));
        waitingBookingForItem1.setItem(item1);
        waitingBookingForItem1.setBooker(user2);
        waitingBookingForItem1.setStatus(Status.WAITING);

        Thread.sleep(50);

        waitingBookingForItem2 = new Booking();
        waitingBookingForItem2.setStart(LocalDateTime.now().plusHours(1));
        waitingBookingForItem2.setEnd(LocalDateTime.now().plusHours(2));
        waitingBookingForItem2.setItem(item2);
        waitingBookingForItem2.setBooker(user2);
        waitingBookingForItem2.setStatus(Status.WAITING);

        Thread.sleep(50);

        rejectedBookingForItem1 = new Booking();
        rejectedBookingForItem1.setStart(LocalDateTime.now().plusHours(1));
        rejectedBookingForItem1.setEnd(LocalDateTime.now().plusHours(2));
        rejectedBookingForItem1.setItem(item1);
        rejectedBookingForItem1.setBooker(user2);
        rejectedBookingForItem1.setStatus(Status.REJECTED);

        Thread.sleep(50);

        rejectedBookingForItem2 = new Booking();
        rejectedBookingForItem2.setStart(LocalDateTime.now().plusHours(1));
        rejectedBookingForItem2.setEnd(LocalDateTime.now().plusHours(2));
        rejectedBookingForItem2.setItem(item2);
        rejectedBookingForItem2.setBooker(user2);
        rejectedBookingForItem2.setStatus(Status.REJECTED);
    }

    @SneakyThrows
    private void createBookingsInDb() {
        bookingRepository.save(currentBookingForItem1);
        bookingRepository.save(currentBookingForItem2);
        bookingRepository.save(pastBookingForItem1);
        bookingRepository.save(pastBookingForItem2);
        bookingRepository.save(futureBookingForItem1);
        bookingRepository.save(futureBookingForItem2);
        bookingRepository.save(waitingBookingForItem1);
        bookingRepository.save(waitingBookingForItem2);
        bookingRepository.save(rejectedBookingForItem1);
        bookingRepository.save(rejectedBookingForItem2);
    }


}




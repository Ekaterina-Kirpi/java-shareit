//package ru.practicum.shareit.item;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.jdbc.Sql;
//import org.springframework.web.server.ResponseStatusException;
//import ru.practicum.shareit.booking.enam.Status;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.booking.repository.BookingRepository;
//import ru.practicum.shareit.item.dto.CommentDto;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.dto.ItemDtoResponse;
//import ru.practicum.shareit.item.dto.ItemDtoUpdate;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.CommentRepository;
//import ru.practicum.shareit.item.service.ItemServiceImpl;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.request.repository.ItemRequestRepository;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.time.LocalDateTime;
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
//public class ItemServiceImplTest {
//
//    private final ItemServiceImpl itemServiceImpl;
//    private final UserRepository userRepository;
//    private final BookingRepository bookingRepository;
//    private final CommentRepository commentRepository;
//    private final ItemRequestRepository itemRequestRepository;
//    private ItemDto item1Dto;
//    private ItemDto item2Dto;
//    private ItemDtoUpdate item1UpdateDto;
//    private User user1;
//    private User user2;
//    private ItemRequest itemRequest1;
//    private Booking lastBooking;
//    private Booking nextBooking;
//
//
//    @BeforeEach
//    public void setUp() {
//        item1Dto = ItemDto.builder()
//                .name("item test")
//                .description("item test description")
//                .available(Boolean.TRUE)
//                .build();
//        item2Dto = ItemDto.builder()
//                .name("item2 test")
//                .description("item2 test description")
//                .available(Boolean.TRUE)
//                .build();
//        item1UpdateDto = ItemDtoUpdate.builder()
//                .name("updated name")
//                .description("updated description")
//                .available(Boolean.FALSE)
//                .build();
//        user1 = new User();
//        user1.setName("test name");
//        user1.setEmail("test@test.ru");
//        user2 = new User();
//        user2.setName("test name2");
//        user2.setEmail("test2@test.ru");
//        itemRequest1 = new ItemRequest();
//        itemRequest1.setDescription("item request1 description");
//        itemRequest1.setRequester(user2);
//        itemRequest1.setCreated(LocalDateTime.now());
//    }
//
//    @Test
//    public void createAndGetItemByIdTest() {
//        //given
//        userRepository.save(user1);
//        //when
//        var savedItem = itemServiceImpl.createItem(item1Dto, user1.getId());
//        var findItem = itemServiceImpl.getItemById(user1.getId(), savedItem.getId());
//        //then
//        assertThat(savedItem).usingRecursiveComparison().ignoringFields("comments").isEqualTo(findItem);
//    }
//
//    @Test
//    public void notExistingUserCreateItemTest() {
//        assertThatThrownBy(
//                //then
//                () -> itemServiceImpl.createItem(item1Dto, 1L)
//        )
//                //when
//                .isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    public void createItemWithNotExistingItemRequestTest() {
//        //given
//        userRepository.save(user1);
//        userRepository.save(user2);
//        itemRequestRepository.save(itemRequest1);
//        item1Dto.setRequestId(2L);
//        assertThatThrownBy(
//                //when
//                () -> itemServiceImpl.createItem(item1Dto, user1.getId())
//        )
//                //then
//                .isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    public void updateItemTest() {
//        //given
//        userRepository.save(user1);
//        //when
//        var savedItem = itemServiceImpl.createItem(item1Dto, user1.getId());
//        var updatedItem = itemServiceImpl.updateItem(savedItem.getId(), user1.getId(), item1UpdateDto);
//        assertThat(updatedItem.getId()).isEqualTo(savedItem.getId());
//        //then
//        assertThat(updatedItem.getName()).isEqualTo(item1UpdateDto.getName());
//        assertThat(updatedItem.getDescription()).isEqualTo(item1UpdateDto.getDescription());
//        assertThat(updatedItem.getAvailable()).isEqualTo(item1UpdateDto.getAvailable());
//    }
//
//    @Test
//    public void updateItemWithNotExistingItemIdTest() {
//        //given
//        userRepository.save(user1);
//        itemServiceImpl.createItem(item1Dto, user1.getId());
//        assertThatThrownBy(
//                () -> itemServiceImpl.updateItem(2L, user1.getId(), item1UpdateDto)
//        )
//                //then
//                .isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    public void updateItemWithOtherUserTest() {
//        //given
//        userRepository.save(user1);
//        //when
//        var savedItem = itemServiceImpl.createItem(item1Dto, user1.getId());
//        assertThatThrownBy(
//                () -> itemServiceImpl.updateItem(savedItem.getId(), 2L, item1UpdateDto)
//        )
//                //then
//                .isInstanceOf(ResponseStatusException.class);
//    }
//
//
//    @Test
//    public void getItemByNotExistingIdTest() {
//        //given
//        userRepository.save(user1);
//        //when
//        itemServiceImpl.createItem(item1Dto, user1.getId());
//        assertThatThrownBy(
//                () -> itemServiceImpl.getItemById(user1.getId(), 2L)
//                //then
//        ).isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    void getItemByIdWithLastAndNextBookingsTest() {
//        //given
//        userRepository.save(user1);
//        userRepository.save(user2);
//        var savedItem = itemServiceImpl.createItem(item1Dto, user1.getId());
//        createLastAndNextBookings(savedItem);
//        bookingRepository.save(lastBooking);
//        bookingRepository.save(nextBooking);
//        //when
//        var findItem = itemServiceImpl.getItemById(user1.getId(), savedItem.getId());
//        //then
//        assertThat(findItem.getId()).isEqualTo(savedItem.getId());
//        assertThat(findItem.getName()).isEqualTo(item1Dto.getName());
//        assertThat(findItem.getDescription()).isEqualTo(item1Dto.getDescription());
//        assertThat(findItem.getAvailable()).isEqualTo(item1Dto.getAvailable());
//        assertThat(findItem.getLastBooking().getBookerId()).isEqualTo(user2.getId());
//        assertThat(findItem.getLastBooking().getId()).isEqualTo(lastBooking.getId());
//        assertThat(findItem.getNextBooking().getBookerId()).isEqualTo(user2.getId());
//        assertThat(findItem.getNextBooking().getId()).isEqualTo(nextBooking.getId());
//    }
//
//    @Test
//    public void getPersonalItemsTest() {
//        //given
//        userRepository.save(user1);
//        userRepository.save(user2);
//        var savedItem1 = itemServiceImpl.createItem(item1Dto, user1.getId());
//        itemServiceImpl.createItem(item2Dto, user2.getId());
//        createLastAndNextBookings(savedItem1);
//        bookingRepository.save(lastBooking);
//        bookingRepository.save(nextBooking);
//        var findItem = itemServiceImpl.getItemById(savedItem1.getId(), user1.getId());
//        //when
//        var personalItemsList = itemServiceImpl.getAllItemsOwner(user1.getId(), 0, 2);
//        //then
//        assertThat(personalItemsList.getItems()).singleElement().usingRecursiveComparison()
//                .ignoringFields("comments").isEqualTo(findItem);
//    }
//
//    @Test
//    public void getPersonalItemsWithNotExistingUserTest() {
//        //given
//        userRepository.save(user1);
//        userRepository.save(user2);
//        var savedItem1 = itemServiceImpl.createItem(item1Dto, user1.getId());
//        itemServiceImpl.createItem(item2Dto, user2.getId());
//        createLastAndNextBookings(savedItem1);
//        bookingRepository.save(lastBooking);
//        bookingRepository.save(nextBooking);
//        assertThatThrownBy(
//                //when
//                () -> itemServiceImpl.getAllItemsOwner(99L, 0, 2)
//                //then
//        ).isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    public void getFoundItemsWhenSearchTextIsBlankTest() {
//        //given
//        userRepository.save(user1);
//        itemServiceImpl.createItem(item1Dto, user1.getId());
//        itemServiceImpl.createItem(item2Dto, user1.getId());
//        //when
//        var findItems = itemServiceImpl.search(" ", 0, 2);
//        //then
//        assertThat(findItems.getItems()).isEmpty();
//    }
//
//
//    @Test
//    public void addCommentTest() {
//        //given
//        CommentDto commentDto = CommentDto.builder()
//                .text("Nice item, awesome author")
//                .build();
//        userRepository.save(user1);
//        userRepository.save(user2);
//        var savedItem1 = itemServiceImpl.createItem(item1Dto, user1.getId());
//        createLastAndNextBookings(savedItem1);
//        bookingRepository.save(lastBooking);
//        //when
//        var savedComment1 = itemServiceImpl.createComment(savedItem1.getId(), user2.getId(), commentDto);
//        var comment1 = commentRepository.findById(savedComment1.getId()).get();
//        //then
//        assertThat(savedComment1.getId()).isEqualTo(1L);
//        assertThat(savedComment1.getText()).isEqualTo(commentDto.getText());
//        assertThat(savedComment1.getCreated()).isBefore(LocalDateTime.now());
//        assertThat(savedComment1.getAuthorName()).isEqualTo(user2.getName());
//        //when
//        commentDto.setText("Nice item, awesome author2");
//        var savedComment2 = itemServiceImpl.createComment(savedItem1.getId(), user2.getId(), commentDto);
//        var comment2 = commentRepository.findById(savedComment2.getId()).get();
//        //then
//        assertThat(comment1.equals(comment2)).isFalse();
//
//    }
//
//    @Test
//    public void addCommentFromUserWithNotExistingBooksTest() {
//        //given
//        CommentDto commentDto = CommentDto.builder()
//                .text("Nice item, awesome author")
//                .build();
//        userRepository.save(user1);
//        userRepository.save(user2);
//        var savedItem1 = itemServiceImpl.createItem(item1Dto, user1.getId());
//        assertThatThrownBy(
//                //when
//                () -> itemServiceImpl.createComment(savedItem1.getId(), user2.getId(), commentDto)
//                //then
//        ).isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    public void addCommentForNotExistingItemTest() {
//        //given
//        CommentDto commentDto = CommentDto.builder()
//                .text("Nice item, awesome author")
//                .build();
//        userRepository.save(user1);
//        userRepository.save(user2);
//        var savedItem1 = itemServiceImpl.createItem(item1Dto, user1.getId());
//        createLastAndNextBookings(savedItem1);
//        bookingRepository.save(lastBooking);
//        assertThat(lastBooking.equals(nextBooking)).isFalse();
//        assertThatThrownBy(
//                //when
//                () -> itemServiceImpl.createComment(2L, user2.getId(), commentDto)
//                //then
//        ).isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    public void addCommentFromNotExistingUserTest() {
//        //given
//        CommentDto commentDto = CommentDto.builder()
//                .text("Nice item, awesome author")
//                .build();
//        userRepository.save(user1);
//        userRepository.save(user2);
//        var savedItem1 = itemServiceImpl.createItem(item1Dto, user1.getId());
//        createLastAndNextBookings(savedItem1);
//        bookingRepository.save(lastBooking);
//        assertThatThrownBy(
//                //when
//                () -> itemServiceImpl.createComment(savedItem1.getId(), 50L, commentDto)
//                //then
//        ).isInstanceOf(ResponseStatusException.class);
//    }
//
//    @Test
//    public void getFoundItems() {
//        //given
//        userRepository.save(user1);
//        var savedItem1 = itemServiceImpl.createItem(item1Dto, user1.getId());
//        var savedItem2 = itemServiceImpl.createItem(item2Dto, user1.getId());
//        //when
//        var findItems = itemServiceImpl.search("em2", 0, 2);
//        //then
//        assertThat(findItems.getItems()).singleElement().usingRecursiveComparison()
//                .ignoringFields("comments").isEqualTo(savedItem2);
//        //when
//        findItems = itemServiceImpl.search("test", 0, 2);
//        //then
//        assertThat(findItems.getItems().size()).isEqualTo(2);
//        assertThat(findItems.getItems()).element(0).usingRecursiveComparison()
//                .ignoringFields("comments").isEqualTo(savedItem1);
//        assertThat(findItems.getItems()).element(1).usingRecursiveComparison()
//                .ignoringFields("comments").isEqualTo(savedItem2);
//    }
//
//
//
//    private void createLastAndNextBookings(ItemDtoResponse item) {
//        Item bookingItem = new Item();
//        bookingItem.setId(item.getId());
//        bookingItem.setOwner(user1);
//        bookingItem.setName(item.getName());
//        bookingItem.setDescription(item.getDescription());
//        bookingItem.setAvailable(item.getAvailable());
//        lastBooking = new Booking();
//        lastBooking.setStart(LocalDateTime.now().minusDays(2));
//        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
//        lastBooking.setItem(bookingItem);
//        lastBooking.setBooker(user2);
//        lastBooking.setStatus(Status.APPROVED);
//        nextBooking = new Booking();
//        nextBooking.setStart(LocalDateTime.now().plusDays(1));
//        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
//        nextBooking.setItem(bookingItem);
//        nextBooking.setBooker(user2);
//        nextBooking.setStatus(Status.APPROVED);
//    }
//}
//
//
//
//

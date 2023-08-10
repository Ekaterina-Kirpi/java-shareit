//package ru.practicum.shareit.request;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.server.ResponseStatusException;
//import ru.practicum.shareit.item.model.Comment;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.CommentRepository;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.request.dto.ItemRequestDto;
//import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
//import ru.practicum.shareit.request.dto.RequestDtoResponse;
//import ru.practicum.shareit.request.mapper.ItemRequestMapper;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.request.repository.ItemRequestRepository;
//import ru.practicum.shareit.request.service.ItemRequestService;
//import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.Mockito.*;
//
//@ActiveProfiles("unit-test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class ItemRequestServiceImplUnitTest {
//
//    private ItemRequestService service;
//    private final ItemRequestMapper mapper;
//
//    @Mock
//    private final ItemRequestRepository itemRequestRepository;
//    @Mock
//    private final ItemRepository itemRepository;
//    @Mock
//    private final CommentRepository commentRepository;
//    @Mock
//    private final UserRepository userRepository;
//
//    private User user1;
//    private Item item1;
//    private Comment comment1;
//    private ItemRequest itemRequest;
//    private ItemRequestDto itemRequestDto;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        service = new ItemRequestServiceImpl(
//                mapper, itemRequestRepository, itemRepository,
//                commentRepository, userRepository);
//        user1 = new User();
//        user1.setId(1L);
//        user1.setName("test name");
//        user1.setEmail("test@test.ru");
//        item1 = new Item();
//        item1.setId(1L);
//        item1.setName("test item");
//        item1.setDescription("test item description");
//        item1.setAvailable(Boolean.TRUE);
//        item1.setOwner(user1);
//        comment1 = new Comment();
//        comment1.setId(1L);
//        comment1.setText("testText");
//        comment1.setItem(item1);
//        comment1.setAuthor(user1);
//        comment1.setCreated(LocalDateTime.now());
//        item1.setComments(new HashSet<>(List.of(comment1)));
//        itemRequestDto = ItemRequestDto.builder()
//                .description("test request description")
//                .build();
//        itemRequest = mapper.toItemRequestFromItemRequestDto(itemRequestDto);
//        itemRequest.setItems(new HashSet<>(List.of(item1)));
//        item1.setRequest(itemRequest);
//    }
//
//    @Test
//    void createItemRequestTest() {
//        ItemRequest expected = mapper.toItemRequestFromItemRequestDto(itemRequestDto);
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
//        when(itemRequestRepository.save(any())).thenReturn(expected);
//
//        ItemRequestDtoResponse actual = service.createItemRequest(itemRequestDto, user1.getId());
//
//        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
//    }
//
//    @Test
//    void createItemRequestByInvalidRequesterIdTest() {
//        String expectedMessage = " \"Пользователь " + 999L + " отсутствует\"";
//
//        assertThatThrownBy(() -> service.createItemRequest(itemRequestDto, 999L))
//                .isInstanceOf(ResponseStatusException.class)
//                .message()
//                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
//    }
//
//    @Test
//    void getItemRequestByIdTest() {
//        itemRequest.setId(1L);
//        RequestDtoResponse expected = mapper.toListRequestDtoToResponseFromListItemRequest(itemRequest);
//
//        when(userRepository.existsById(anyLong())).thenReturn(true);
//        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
//        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of(item1));
//        when(commentRepository.findByItemIdIn(any())).thenReturn(List.of(comment1));
//
//        RequestDtoResponse actual = service.getItemRequestById(user1.getId(), itemRequest.getId());
//
//        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
//    }
//
//    @Test
//    void getItemRequestByInvalidUserIdTest() {
//        String expectedMessage = " \"Пользователь " + 999L + " отсутствует\"";
//
//        assertThatThrownBy(() -> service.getItemRequestById(999L, itemRequest.getId()))
//                .isInstanceOf(ResponseStatusException.class)
//                .message()
//                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
//    }
//
//    @Test
//    void getItemRequestByInvalidRequestIdTest() {
//        String expectedMessage = " \"Запрос " + 999L + " не найден\"";
//
//        when(userRepository.existsById(anyLong())).thenReturn(true);
//
//        assertThatThrownBy(() -> service.getItemRequestById(user1.getId(), 999L))
//                .isInstanceOf(ResponseStatusException.class)
//                .message()
//                .isEqualTo(HttpStatus.NOT_FOUND + expectedMessage);
//    }
//
//}
//
//
//

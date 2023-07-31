package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private User user1;
    private User user2;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        itemRequestDto = ItemRequestDto.builder()
                .description("test request description")
                .build();
    }

    @Test
    public void createItemRequestTest() {
        //given
        userRepository.save(user1);
        //when
        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        var findRequest = itemRequestService.getItemRequestById(user1.getId(), savedRequest.getId());
        //then
        assertThat(savedRequest).usingRecursiveComparison().ignoringFields("items", "created")
                .isEqualTo(findRequest);
    }

    @Test
    public void createItemRequestWhenRequesterNotFoundTest() {
        //given
        userRepository.save(user1);
        assertThatThrownBy(
                //when
                () -> itemRequestService.createItemRequest(itemRequestDto, 99L)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getPrivateRequestTest() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user2.getId());
        //when
        var privateRequests = itemRequestService
                .getOwnerRequests(PageRequest.of(0, 2), user2.getId());
        var findRequest = itemRequestService.getItemRequestById(user2.getId(), savedRequest.getId());
        //then
        assertThat(privateRequests.getRequests().get(0)).usingRecursiveComparison().isEqualTo(findRequest);
    }

    @Test
    public void getPrivateRequestWhenRequesterNotExistingRequestsTest() {
        //given
        userRepository.save(user1);
        assertThatThrownBy(
                //when
                () -> itemRequestService
                        .getOwnerRequests(PageRequest.of(0, 2), 55L)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getOtherRequestsTest() {
        //given
        userRepository.save(user1);
        userRepository.save(user2);
        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        var findRequest = itemRequestService.getItemRequestById(user1.getId(), savedRequest.getId());
        //when
        var otherRequest = itemRequestService.getUserRequests(PageRequest.of(0, 2), user2.getId());
        //then
        assertThat(otherRequest.getRequests().get(0)).usingRecursiveComparison().isEqualTo(findRequest);
    }

    @Test
    public void getOtherRequestsWhenRequesterNotFoundTest() {
        //given
        userRepository.save(user1);
        itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(
                //when
                () -> itemRequestService.getUserRequests(PageRequest.of(0, 2), 50L)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getItemRequestWhenUserNotFoundTest() {
        //given
        userRepository.save(user1);
        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(
                //when
                () -> itemRequestService.getItemRequestById(50L, savedRequest.getId())
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getItemRequestWhenRequestNotFoundTest() {
        //given
        userRepository.save(user1);
        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(
                //when
                () -> itemRequestService.getItemRequestById(savedRequest.getId(), 50L)
                //then
        ).isInstanceOf(ResponseStatusException.class);
    }
}
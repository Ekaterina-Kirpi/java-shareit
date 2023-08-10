//package ru.practicum.shareit.item;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@RunWith(SpringRunner.class)
//@DataJpaTest
//class ItemRepositoryTest {
//    @Autowired
//    private ItemRepository itemRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    private User user;
//    private  User user2;
//    private Item item;
//    private Item item2;
//
//    @BeforeEach
//    void setUp() {
//        user = userRepository.save(User.builder().name("User").email("user@test.ru").build());
//        item = itemRepository.save(Item.builder().name("Item").description("Description").available(true).owner(user).build());
//        user2 = userRepository.save(User.builder().name("User2").email("user@test2.ru").build());
//        item2 = itemRepository.save(Item.builder().name("Item2").description("Description").available(true).owner(user).build());
//    }
//
//    @AfterEach
//    void tearDown() {
//        userRepository.deleteAll();
//        itemRepository.deleteAll();
//    }
//
//    @Test
//    void testFindAllItemIdByOwnerId() {
//        Long ownerId = user.getId();
//        List<Long> itemIds = itemRepository.findAllItemIdByOwnerId(ownerId);
//
//        assertEquals(2, itemIds.size());
//        assertEquals(item.getId(), itemIds.get(0));
//        assertEquals(item2.getId(), itemIds.get(1));
//    }
//
//
//}
//

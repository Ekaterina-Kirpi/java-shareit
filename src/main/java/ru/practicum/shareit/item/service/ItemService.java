package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.enam.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;


    public ItemDtoResponse createItem(ItemDto item, Long userId) throws ResponseStatusException {
        Item itemNew = itemMapper.toItemFromItemDto(item);
        if (item.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(item.getRequestId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Запрос " + item.getRequestId() + " не найден"));

            itemNew.setRequest(itemRequest);
        }
        itemNew.setOwner(userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + userId + " не найден")));
        return itemMapper.toItemDtoResponseFromItem(itemRepository.save(itemNew));
    }


    public ItemDtoResponse updateItem(Long itemId, Long userId, ItemDtoUpdate item) {
        Item itemUp = itemRepository.findById(itemId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь " + itemId + " не найдена"));
        if (!itemUp.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "У пользователя " + userId + " не найдена вещь " + itemId);
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemUp.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemUp.setDescription(itemUp.getDescription());
        }
        if (item.getAvailable() != null) {
            itemUp.setAvailable(item.getAvailable());
        }

        return itemMapper.toItemDtoResponseFromItem(itemRepository.save(itemMapper.toItemFromItemDtoUpdate(item, itemUp)));
    }


    @Transactional(readOnly = true)
    public ItemDtoResponse getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, " Вещь " + itemId + " не найдена"));
        ItemDtoResponse itemDtoResponse = itemMapper.toItemDtoResponseFromItem(item);
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), Status.APPROVED)
                    .orElse(null);
            Booking nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), Status.APPROVED)
                    .orElse(null);
            itemDtoResponse.setLastBooking(itemMapper.toBookingShortDtoFromBooking(lastBooking));
            itemDtoResponse.setNextBooking(itemMapper.toBookingShortDtoFromBooking(nextBooking));
            return itemDtoResponse;
        }
        return itemDtoResponse;
    }


    @Transactional(readOnly = true)
    public ItemListDto getAllItemsOwner(Pageable pageable, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + userId + " не найден");
        }
        List<ItemDtoResponse> personalItems = itemRepository.findAllByOwnerIdOrderByIdAsc(pageable, userId).stream()
                .map(itemMapper::toItemDtoResponseFromItem).collect(Collectors.toList());
        for (ItemDtoResponse item : personalItems) {
            item.setLastBooking(itemMapper.toBookingShortDtoFromBooking(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(item.getId(),
                    LocalDateTime.now(), Status.APPROVED).orElse(null)));
            item.setNextBooking(itemMapper.toBookingShortDtoFromBooking(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                    item.getId(), LocalDateTime.now(), Status.APPROVED).orElse(null)
            ));
        }
        return ItemListDto.builder().items(personalItems).build();
    }

    @Transactional(readOnly = true)
    public ItemListDto search(Pageable pageable, String text) {
        if (text.isBlank()) {
            return ItemListDto.builder().items(new ArrayList<>()).build();
        }
        return ItemListDto.builder()
                .items(itemRepository
                        .findAllByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingAndAvailableTrue(pageable, text, text)
                        .stream()
                        .map(itemMapper::toItemDtoResponseFromItem)
                        .collect(Collectors
                                .toList()))
                .build();
    }


    public CommentDtoResponse createComment(Long itemId, Long userId, CommentDto commentDto) {
        if (!bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                Status.APPROVED, LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Пользователь  " + userId + " не арендовал вещь " + itemId);
        } else {
            User author = userRepository.findById(userId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + userId + " не найден"));
            Item item = itemRepository.findById(itemId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь " + itemId + " не найдена"));
            Comment comment = itemMapper.toCommentFromCommentDto(commentDto);
            comment.setItem(item);
            comment.setAuthor(author);
            comment.setCreated(LocalDateTime.now());
            return itemMapper.toCommentDtoResponseFromComment(commentRepository.save(comment));
        }
    }
}
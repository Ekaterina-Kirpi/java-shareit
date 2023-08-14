package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.utilits.ShareItPageRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDtoResponse createItem(ItemDto item, Long userId) throws ResponseStatusException {
        Item itemNew = itemMapper.toItemFromItemDto(item);
        if (item.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(item.getRequestId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Запрос " + item.getRequestId() + " не найден"));
            itemNew.setRequest(itemRequest);
        }
        itemNew.setOwner(userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + userId + " не найден")));
        return itemMapper.toItemDtoResponseFromItem(itemRepository.save(itemNew));
    }


    @Override
    public ItemDtoResponse updateItem(Long itemId, Long userId, ItemDtoUpdate item) {
        Item itemUp = itemRepository.findById(itemId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь " + itemId + " не найдена"));
        setComments(List.of(itemUp));
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


    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, " Вещь " + itemId + " не найдена"));
        setComments(List.of(item));
        ItemDtoResponse itemDtoResponse = itemMapper.toItemDtoResponseFromItem(item);
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), Status.APPROVED)
                    .orElse(null);
            Booking nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), Status.APPROVED)
                    .orElse(null);
            itemDtoResponse.setLastBooking(itemMapper.toBookingLimitDtoFromBooking(lastBooking));
            itemDtoResponse.setNextBooking(itemMapper.toBookingLimitDtoFromBooking(nextBooking));
            return itemDtoResponse;
        }
        return itemDtoResponse;
    }


    @Override
    @Transactional(readOnly = true)
    public ItemListDto getAllItemsOwner(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + userId + " не найден");
        }
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        setComments(items);
        List<ItemDtoResponse> personalItems = new ArrayList<>();

        List<Booking> beforeBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndStatusOrderByItemIdAscEndDesc(
                items.stream().map(Item::getId).collect(Collectors.toList()),
                LocalDateTime.now(), Status.APPROVED
        );
        List<Booking> afterBookings = bookingRepository.findAllByItemIdInAndStartAfterAndStatusOrderByItemIdAscStartAsc(
                items.stream().map(Item::getId).collect(Collectors.toList()),
                LocalDateTime.now(), Status.APPROVED
        );
        Map<Long, List<Booking>> beforeBookingsMap = beforeBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Booking>> afterBookingsMap = afterBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        for (Item item : items) {
            ItemDtoResponse itemDto = itemMapper.toItemDtoResponseFromItem(item);

            List<Booking> lastBookings = beforeBookingsMap.get(item.getId());
            if (lastBookings != null && !lastBookings.isEmpty()) {
                Booking lastBooking = lastBookings.get(0);
                itemDto.setLastBooking(itemMapper.toBookingLimitDtoFromBooking(lastBooking));
            } else {
                itemDto.setLastBooking(null);
            }

            List<Booking> nextBookings = afterBookingsMap.get(item.getId());
            if (nextBookings != null && !nextBookings.isEmpty()) {
                Booking nextBooking = nextBookings.get(0);
                itemDto.setNextBooking(itemMapper.toBookingLimitDtoFromBooking(nextBooking));
            } else {
                itemDto.setNextBooking(null);
            }

            personalItems.add(itemDto);
        }

        return ItemListDto.builder().items(personalItems).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemListDto search(String text, int from, int size) {
        Pageable pageable = new ShareItPageRequest(from, size);
        if (text.isBlank()) {
            return ItemListDto.builder().items(new ArrayList<>()).build();
        }
        List<Item> items = itemRepository
                .findAllByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingAndAvailableTrue(pageable, text, text);
        setComments(items);
        return ItemListDto.builder()
                .items(items
                        .stream()
                        .map(itemMapper::toItemDtoResponseFromItem)
                        .collect(toList()))
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

    @Transactional(readOnly = true)
    public void setComments(List<Item> items) {
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Comment> allComments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<Comment>> commentsMap = allComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        for (Item item : items) {
            List<Comment> comments = commentsMap.getOrDefault(item.getId(), Collections.emptyList());
            item.setComments(new HashSet<>(comments));
        }
    }
}

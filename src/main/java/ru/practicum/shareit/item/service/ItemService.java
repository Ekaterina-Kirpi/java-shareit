package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingLimitDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exception.UserOrItemNotFoundException;
import ru.practicum.shareit.validation.exception.UserOrItemNotValidException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public ItemDto getItem(Long itemId, Long userId) {
        userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new UserOrItemNotFoundException("Вещь с id: " + itemId + " не найдена"));
        ItemDto itemDto = itemMapper.itemToDto(item);
        List<Booking> bookings = bookingRepository.findByItemIdAndStatus(itemId, BookingStatus.APPROVED,
                Sort.by(Sort.Direction.ASC, "start"));
        List<BookingLimitDto> bookingLimit = bookings.stream()
                .map(bookingMapper::bookingLimitToDto)
                .collect(Collectors.toList());
        if (item.getUserId() == userId) {   // Бронирования показываем только владельцу вещи
            setBookings(itemDto, bookingLimit);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId,
                Sort.by(Sort.Direction.ASC, "created"));
        List<CommentDto> commentsDto = comments.stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentsDto);
        return itemDto;
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        User user = userService.getUserById(userId);
        List<Item> items = itemRepository.findAllByUserIdOrderById(user.getId());
        List<ItemDto> itemsDto = items.stream()
                .map(itemMapper::itemToDto)
                .collect(Collectors.toList());
        //Logger.logInfo(HttpMethod.GET, "/items",  items.toString());
        List<Booking> bookings = bookingRepository.findAllByOwnerId(userId,
                Sort.by(Sort.Direction.ASC, "start"));
        List<BookingLimitDto> bookingLimitDtoList = bookings.stream()
                .map(bookingMapper::bookingLimitToDto)
                .collect(Collectors.toList());
        //Logger.logInfo(HttpMethod.GET, "/items",  bookings.toString());
        List<Comment> comments = commentRepository.findAllByItemIdIn(
                items.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()),
                Sort.by(Sort.Direction.ASC, "created"));
        itemsDto.forEach(itemDto -> {
            setBookings(itemDto, bookingLimitDtoList);
            setComments(itemDto, comments);
        });
        return itemsDto;
    }

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = itemMapper.itemFromDto(itemDto);
        User owner = userService.getUserById(userId);
        newItem.setUserId(owner.getId());
        Item createdItem = itemRepository.save(newItem);
        return itemMapper.itemToDto(createdItem);
    }


    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemMapper.itemFromDto(itemDto);
        User user = userService.getUserById(userId);
        Item targetItem = itemRepository.findById(itemId).orElseThrow(() ->
                new UserOrItemNotFoundException("Вещь с id: " + itemId + " не найдена"));
        if (targetItem.getUserId() != user.getId()) {
            throw new UserOrItemNotFoundException("У пользователя с id: " + userId + " не найдена вещь с id: " + itemId);

        }
        if (item.getAvailable() != null) {
            targetItem.setAvailable(item.getAvailable());
        }
        if (StringUtils.hasLength(item.getName())) {
            targetItem.setName(item.getName());
        }
        if (StringUtils.hasLength(item.getDescription())) {
            targetItem.setDescription(item.getDescription());
        }
        Item itemSaved = itemRepository.save(targetItem);
        return itemMapper.itemToDto(itemSaved);
    }


    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new UserOrItemNotFoundException("Вещь с id: " + itemId + " не найдена"));
        itemRepository.deleteById(item.getId());
    }


    @Transactional(readOnly = true)
    public Collection<ItemDto> search(String text) {
        Collection<Item> items;
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            items = itemRepository.findByNameOrDescriptionLike(text.toLowerCase());
        }
        return items
                .stream()
                .map(itemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        Comment comment = commentMapper.commentFromDto(commentDto);
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new UserOrItemNotFoundException(
                ("Вещь с id: " + itemId + " не найдена")));
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatus(itemId, userId, BookingStatus.APPROVED,
                Sort.by(Sort.Direction.DESC, "start")).orElseThrow(() -> new UserOrItemNotFoundException(
                String.format("Пользователь с id %d не арендовал вещь с id %d.", userId, itemId)));
        //Logger.logInfo(HttpMethod.POST, "/items/" + itemId + "/comment", bookings.toString());
        bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).findAny().orElseThrow(() ->
                new UserOrItemNotValidException("Пользователь с id: " + userId +
                        " не может оставлять комментарии к вещи с id: " + itemId));
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        Comment commentSave = commentRepository.save(comment);
        return commentMapper.commentToDto(commentSave);
    }

    private void setBookings(ItemDto itemDto, List<BookingLimitDto> bookings) {
        itemDto.setLastBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId() == itemDto.getId() &&
                        booking.getStart().isBefore(LocalDateTime.now()))
                .reduce((a, b) -> b).orElse(null));
        itemDto.setNextBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId() == itemDto.getId() &&
                        booking.getStart().isAfter(LocalDateTime.now()))
                .reduce((a, b) -> a).orElse(null));
    }


    private void setComments(ItemDto itemDto, List<Comment> comments) {
        itemDto.setComments(comments.stream()
                .filter(comment -> comment.getItem().getId() == itemDto.getId())
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList()));
    }
}
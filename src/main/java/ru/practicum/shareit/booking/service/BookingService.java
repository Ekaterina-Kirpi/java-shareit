package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;
import ru.practicum.shareit.booking.enam.State;
import ru.practicum.shareit.booking.enam.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;


    @Transactional
    public BookingDtoResponse createBooking(Long bookerId, BookingDto bookingDto) {
        bookingDto.setStatus(Status.WAITING);
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Дата окончания бронирования не может быть раньше даты начала или равна ей");
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Вещь " + bookingDto.getItemId() + " не найдена"));
        if (!item.getOwner().getId().equals(bookerId)) {
            if (item.getAvailable()) {
                User user = userRepository.findById(bookerId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Пользователь " + bookerId + " не найден"));
                Booking booking = bookingMapper.toBookingFromBookingDto(bookingDto);
                booking.setItem(item);
                booking.setBooker(user);
                return bookingMapper.toBookingDtoResponseFromBooking(bookingRepository.save(booking));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Вещь " + item.getId() + " недоступна для бронирования");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Владелец не может бронировать свои вещи");
        }
    }


    @Transactional
    public BookingDtoResponse approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирование " + bookingId + " не найдено"));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Невозможно изменить статус, когда бронирование в статусе: " + booking.getStatus());
        }
        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            return bookingMapper.toBookingDtoResponseFromBooking(bookingRepository.save(booking));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + ownerId +
                    " не является владельцем вещи " + booking.getItem().getOwner().getId());
        }
    }


    @Transactional(readOnly = true)
    public BookingDtoResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирование " + bookingId + " не найдено "
                        + "¯\\_(ツ)_/¯"));
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь " + userId + " не бронировал и не является владелецем забронированной вещи");
        }
        return bookingMapper.toBookingDtoResponseFromBooking(booking);
    }


    @Transactional(readOnly = true)
    public BookingListDto getAllBookings(Pageable pageable, Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь " + userId + " не найден");
        } else {
            return getListBookings(pageable, state, userId, false);
        }
    }


    @Transactional(readOnly = true)
    public BookingListDto getAllBookingsOfOwner(Pageable pageable, Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + userId + " не найден");
        }
        if (!itemRepository.existsItemByOwnerId(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "У пользователя " + userId + " нет вещей для бронирования");
        } else {
            return getListBookings(pageable, state, userId, true);
        }

    }

    private BookingListDto getListBookings(Pageable pageable, String state, Long userId, Boolean isOwner) {
        List<Long> itemsId;
        switch (State.checkState(state.toUpperCase())) {
            case ALL:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookingRepository.findAllByItemIdInOrderByStartDesc(pageable, itemsId).stream()
                                    .map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList())).build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookingRepository.findAllByBookerIdOrderByStartDesc(pageable, userId).stream()
                                    .map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList())).build();
                }
            case CURRENT:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder().bookings(
                            bookingRepository.findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                            pageable, itemsId, LocalDateTime.now(), LocalDateTime.now()).stream()
                                    .map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList())).build();
                } else {
                    return BookingListDto.builder().bookings(
                            bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                            pageable, userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                                    .map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList())).build();
                }
            case PAST:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByItemIdInAndEndIsBeforeOrderByStartDesc(
                                            pageable, itemsId, LocalDateTime.now()
                                    ).stream().map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList()))
                            .build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
                                            pageable, userId, LocalDateTime.now()
                                    ).stream().map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList()))
                            .build();
                }
            case FUTURE:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByItemIdInAndStartIsAfterOrderByStartDesc(pageable, itemsId, LocalDateTime.now())
                                    .stream().map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList()))
                            .build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByBookerIdAndStartIsAfterOrderByStartDesc(pageable, userId, LocalDateTime.now())
                                    .stream().map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList()))
                            .build();
                }
            case WAITING:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByItemIdInAndStatusIsOrderByStartDesc(pageable, itemsId, Status.WAITING)
                                    .stream().map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList()))
                            .build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByBookerIdAndStatusIsOrderByStartDesc(pageable, userId, Status.WAITING)
                                    .stream().map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList()))
                            .build();
                }
            case REJECTED:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByItemIdInAndStatusIsOrderByStartDesc(pageable, itemsId, Status.REJECTED)
                                    .stream().map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList()))
                            .build();
                } else {
                    return BookingListDto.builder()
                            .bookings(bookingRepository
                                    .findAllByBookerIdAndStatusIsOrderByStartDesc(pageable, userId, Status.REJECTED)
                                    .stream().map(bookingMapper::toBookingDtoResponseFromBooking).collect(Collectors.toList()))
                            .build();
                }
            default:
                throw new StateException("Unknown state: " + state);
        }
    }
}
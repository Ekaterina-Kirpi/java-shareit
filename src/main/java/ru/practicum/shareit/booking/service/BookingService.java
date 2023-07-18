package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.AccessStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookingService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;


    public BookingDto createBooking(long bookerId, BookingInputDto bookingInputDto) {
        Booking booking = bookingMapper.bookingFromDto(bookingInputDto);
        User booker = userService.getUserById(bookerId);
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new UserOrItemNotFoundException("Вещь с id: " + booking.getItem().getId() + " не найдена"));
        checkBooking(bookerId, booking, item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        Booking bookingSave = bookingRepository.save(booking);
        return bookingMapper.bookingToDto(bookingSave);
    }

    @Transactional
    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved, AccessStatus access) {
        User owner = userService.getUserById(ownerId);
        Booking booking = getBookingById(bookingId, owner.getId(), access);
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new InvalidDataException("У бронирования с id: " + " статус " + BookingStatus.APPROVED.name());
        }
        if (approved) {
            log.info("Запрос на подтверждение бронирования id: " + bookingId);
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            log.info("Запрос на отклонение бронирования id: " + bookingId);
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking bookingSave = bookingRepository.save(booking);
        return bookingMapper.bookingToDto(bookingSave);
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId, Long userId, AccessStatus access) {
        User user = userService.getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new UserOrItemNotFoundException("Бронирование с id: " + bookingId + " не найдено"));
        if (isUnableToAccess(user.getId(), booking, access)) {
            throw new AccessException("У пользователя с id: " + userId +
                    " нет прав на просмотр бронирования с id: " + bookingId);
        }
        return booking;
    }

    @Transactional(readOnly = true)
    public BookingDto getBooking(long bookingId, long userId, AccessStatus access) {
        User user = userService.getUserById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new UserOrItemNotFoundException("Бронирование с id: " + bookingId + " не найдено"));
        if (isUnableToAccess(user.getId(), booking, access)) {
            throw new AccessException("У пользователя с id: " + userId +
                    " нет прав на просмотр бронирования с id: " + bookingId);

        }
        return bookingMapper.bookingToDto(booking);
    }


    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsUser(BookingState state, Long bookerId) {
        User booker = userService.getUserById(bookerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(booker.getId(), sort);
                break;

            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        BookingStatus.REJECTED, sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(),
                        LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(),
                        LocalDateTime.now(), sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(booker.getId(),
                        LocalDateTime.now(), sort);
                break;
            default:
                throw new ArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings
                .stream()
                .map(bookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)

    public List<BookingDto> getBookingsOwner(BookingState state, long ownerId) {
        User owner = userService.getUserById(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(owner.getId(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        BookingStatus.REJECTED, sort);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndEndBefore(owner.getId(),
                        LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStartAfter(owner.getId(),
                        LocalDateTime.now(), sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(owner.getId(),
                        LocalDateTime.now(), sort);
                break;

            default:
                throw new ArgumentException("Unknown state: UNSUPPORTED_STATUS");

        }
        return bookings.stream()
                .map(bookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }


    private boolean isUnableToAccess(long userId, Booking booking, AccessStatus access) {
        boolean isUnable = true;
        switch (access) {
            case OWNER:
                isUnable = booking.getItem().getUserId() != userId;
                break;
            case BOOKER:
                isUnable = booking.getBooker().getId() != userId;
                break;
            case OWNER_AND_BOOKER:
                isUnable = !(booking.getItem().getUserId() == userId || booking.getBooker().getId() == userId);
                break;
        }
        return isUnable;
    }

    private void checkBooking(Long bookerId, Booking booking, Item item) {
        if (bookerId.equals(item.getUserId())) {
            throw new AccessException("Владелец вещи не может бронировать свои вещи");
        }
        if (!item.getAvailable()) {
            throw new UserOrItemNotValidException("Вещь с id: " + item.getId() + " не доступна для бронирования");
        }
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new InvalidDataException("Даты бронирования выбраны некорректно");
        }
    }
}

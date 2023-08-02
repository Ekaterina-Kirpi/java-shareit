package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utilits.ShareItPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utilits.Constants.*;

@Controller
@Slf4j
@RequestMapping("/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public ResponseEntity<BookingDtoResponse> createBooking(@RequestHeader(USER_ID_HEADER) @Positive Long bookerId,
                                                            @Valid @RequestBody BookingDto bookingDto) {
        log.info("Запрос на создание бронирования");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(bookerId, bookingDto));
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> approveBooking(@RequestHeader(USER_ID_HEADER) @Positive Long ownerId,
                                                             @RequestParam boolean approved,
                                                             @PathVariable @Positive Long bookingId) {
        log.info("Запрос на подтверждение/отклонение бронирования {} пользователем {}", bookingId, ownerId);
        return ResponseEntity.ok()
                .body(bookingService.approveBooking(ownerId, bookingId, approved));
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> getBookingById(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {
        log.info("Запрос на получение бронирования {}  у пользователя {}", bookingId, userId);
        return ResponseEntity.ok()
                .body(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<BookingListDto> getAllBookings(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
            @PositiveOrZero Integer from,
            @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
            @Positive Integer size) {
        log.info("Запрос на получение всех бронирований у пользователя {}", userId);
        return ResponseEntity.ok()
                .body(bookingService.getAllBookings(PageRequest.of(from / size, size), userId, state));
    }

    @GetMapping("owner")
    public ResponseEntity<BookingListDto> getAllBookingsOfOwner(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
            @PositiveOrZero Integer from,
            @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
            @Positive Integer size) {
        log.info("Запрос на получение всех забронированных вещей у пользователя " + userId);
        return ResponseEntity.ok()
                .body(bookingService.getAllBookingsOfOwner(PageRequest.of(from / size, size), userId, state));
    }
}
package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
@Slf4j
@RequestMapping("/bookings")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {

    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<BookingDtoResponse> createBooking(@RequestHeader(userIdHeader) @Min(1) Long bookerId,
                                                            @Valid @RequestBody BookingDto bookingDto) {
        log.info("Запрос на создание бронирования");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(bookerId, bookingDto));
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> approveBooking(@RequestHeader(userIdHeader) @Min(1) Long ownerId,
                                                             @RequestParam boolean approved,
                                                             @PathVariable @Min(1) Long bookingId) {
        log.info("Запрос на подтверждение/отклонение бронирования " + bookingId + " пользователем " + ownerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.approveBooking(ownerId, bookingId, approved));
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingDtoResponse> getBookingById(
            @PathVariable @Min(1) Long bookingId,
            @RequestHeader(userIdHeader) @Min(1) Long userId) {
        log.info("Запрос на получение бронирования " + bookingId + " у пользователя " + userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<BookingListDto> getAllBookings(
            @RequestHeader(userIdHeader) @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Запрос на получение всех бронирований у пользователя " + userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getAllBookings(PageRequest.of(from / size, size), userId, state));
    }

    @GetMapping("owner")
    public ResponseEntity<BookingListDto> getAllBookingsOfOwner(
            @RequestHeader(userIdHeader) @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("Запрос на получение всех забронированных вещей у пользователя " + userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getAllBookingsOfOwner(PageRequest.of(from / size, size), userId, state));
    }
}
package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.AccessStatus;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(userIdHeader) Long userId,
                                                    @Valid @RequestBody BookingInputDto bookingInputDto) {
        log.info("Запрос на создание бронирования id: " + bookingInputDto.getId() + " у пользователя id: " + userId);
        return ResponseEntity.status(201).body(bookingService.createBooking(userId, bookingInputDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@PathVariable Long bookingId, @RequestParam boolean approved,
                                                     @RequestHeader(userIdHeader) Long userId) {
        log.info("Запрос на подтверждение/отклонение бронирования id: " + bookingId + " у пользователя id: " + userId);
        return ResponseEntity.ok().body(bookingService.approveBooking(userId, bookingId, approved, AccessStatus.OWNER));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId, @RequestHeader(userIdHeader) Long userId) {
        log.info("Запрос на получение бронирования id: " + bookingId + " у пользователя id: " + userId);
        return ResponseEntity.ok().body(bookingService.getBooking(bookingId, userId, AccessStatus.OWNER_AND_BOOKER));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings(@RequestParam(defaultValue = "ALL") String state,
                                                           @RequestHeader(userIdHeader) Long userId) {
        log.info("Запрос на получение всех бронирований у пользователя id: " + userId);
        return ResponseEntity.ok().body(bookingService.getBookingsUser(BookingState.checkState(state), userId));
    }

    // владелец
    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                                                  @RequestHeader(userIdHeader) Long userId) {
        log.info("Запрос на получение всех забронированных вещей у пользователя id: " + userId);
        return ResponseEntity.ok().body(bookingService.getBookingsOwner(BookingState.checkState(state), userId));
    }
}
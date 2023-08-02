package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;

public interface BookingService {
    BookingDtoResponse createBooking(Long bookerId, BookingDto bookingDto);
    BookingDtoResponse approveBooking(Long ownerId, Long bookingId, boolean approved);
    BookingDtoResponse getBookingById(Long bookingId, Long userId);
    BookingListDto getAllBookings(Pageable pageable, Long userId, String state);
    BookingListDto getAllBookingsOfOwner(Pageable pageable, Long userId, String state);

}

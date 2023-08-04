package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;

public interface BookingService {
    BookingDtoResponse createBooking(Long bookerId, BookingDto bookingDto);

    BookingDtoResponse approveBooking(Long ownerId, Long bookingId, boolean approved);

    BookingDtoResponse getBookingById(Long bookingId, Long userId);

    BookingListDto getAllBookings(Long userId, String state, int from, int size);

    BookingListDto getAllBookingsOfOwner(Long userId, String state, int from, int size);

}

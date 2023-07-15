package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingLimitDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    BookingDto bookingToDto(Booking booking);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingLimitDto bookingLimitToDto(Booking booking);

    @Mapping(target = "item.id", source = "itemId")
    Booking bookingFromDto(BookingInputDto bookingInputDto);
}
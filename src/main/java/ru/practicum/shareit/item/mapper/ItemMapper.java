package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingLimitDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "request.id", target = "requestId")
    ItemDtoResponse toItemDtoResponseFromItem(Item item);

    Item toItemFromItemDto(ItemDto itemDto);

    @Mapping(source = "booker.id", target = "bookerId")
    BookingLimitDto toBookingShortDtoFromBooking(Booking booking);

    Comment toCommentFromCommentDto(CommentDto commentDto);

    @Mapping(source = "author.name", target = "authorName")
    CommentDtoResponse toCommentDtoResponseFromComment(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item toItemFromItemDtoUpdate(ItemDtoUpdate itemDtoUpdate, @MappingTarget Item item);
}

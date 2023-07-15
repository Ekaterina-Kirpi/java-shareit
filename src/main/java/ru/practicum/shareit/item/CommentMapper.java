package ru.practicum.shareit.item;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {
    private final ModelMapper modelMapper;

    public CommentMapper() {
        modelMapper = new ModelMapper();
    }

    public CommentDto commentToDto(Comment comment) {
        return modelMapper.map(comment, CommentDto.class);
    }

    public Comment commentFromDto(CommentDto commentDto) {
        return modelMapper.map(commentDto, Comment.class);
    }
}

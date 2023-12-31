package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestListDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utilits.ShareItPageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utilits.Sort.SORT_BY_CREATED_DESC;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository users;

    @Override
    public ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        User user = users.findById(requesterId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + requesterId + " отсутствует"));
        ItemRequest itemRequestNew = itemRequestMapper.toItemRequestFromItemRequestDto(itemRequestDto);
        itemRequestNew.setRequester(user);
        itemRequestNew.setCreated(LocalDateTime.now());
        return itemRequestMapper.toItemRequestResponseDtoFromItemRequest(itemRequestRepository.save(itemRequestNew));
    }

    @Override
    public ItemRequestListDto getOwnerRequests(Long requesterId, int from, int size) {
        Pageable pageable = new ShareItPageRequest(from, size, SORT_BY_CREATED_DESC);
        if (!users.existsById(requesterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + requesterId + " отсутствует");
        }

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(pageable, requesterId);
        setItems(itemRequests);

        return ItemRequestListDto.builder()
                .requests(itemRequestMapper.toListRequestDtoToResponseFromListItemRequest(itemRequests)).build();
    }

    @Override
    public ItemRequestListDto getUserRequests(Long requesterId, int from, int size) {
        Pageable pageable = new ShareItPageRequest(from, size, SORT_BY_CREATED_DESC);
        if (!users.existsById(requesterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + requesterId + " отсутствует");
        }

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(pageable, requesterId);
        setItems(itemRequests);

        return ItemRequestListDto.builder()
                .requests(itemRequestMapper.toListRequestDtoToResponseFromListItemRequest(itemRequests)).build();
    }


    @Override
    public RequestDtoResponse getItemRequestById(Long userId, Long requestId) {
        if (!users.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + userId + " отсутствует");
        }

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрос " + requestId + " не найден")
        );
        setItems(List.of(itemRequest));

        return itemRequestMapper.toListRequestDtoToResponseFromListItemRequest(itemRequest);
    }

    @Transactional(readOnly = true)
    public void setItems(List<ItemRequest> itemRequests) {
        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<Comment>> commentsMap = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        for (Item item : items) {
            List<Comment> itemComments = commentsMap.getOrDefault(item.getId(), Collections.emptyList());
            item.setComments(new HashSet<>(itemComments));
        }
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> requestItems = items.stream()
                    .filter(item -> itemRequest.getId().equals(item.getRequest().getId()))
                    .collect(Collectors.toList());
            itemRequest.setItems(new HashSet<>(requestItems));
        }
    }
}


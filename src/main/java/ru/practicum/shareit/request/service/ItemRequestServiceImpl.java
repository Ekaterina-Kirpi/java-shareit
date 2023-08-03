package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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

import static ru.practicum.shareit.utilits.Sort.SORT_BY_CREATED_DESC;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
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
        return ItemRequestListDto.builder()
                .requests(itemRequestMapper.toListRequestDtoToResponseFromListItemRequest(itemRequestRepository.findAllByRequesterId(pageable,
                        requesterId))).build();
    }

    @Override
    public ItemRequestListDto getUserRequests(Long requesterId, int from, int size) {
        Pageable pageable = new ShareItPageRequest(from, size, SORT_BY_CREATED_DESC);
        if (!users.existsById(requesterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + requesterId + " отсутствует");
        }
        return ItemRequestListDto.builder()
                .requests(itemRequestMapper.toListRequestDtoToResponseFromListItemRequest(itemRequestRepository.findAllByRequesterIdNot(pageable,
                        requesterId))).build();
    }


    @Override
    public RequestDtoResponse getItemRequestById(Long userId, Long requestId) {
        if (!users.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + userId + " отсутствует");
        }
        return itemRequestMapper.toListRequestDtoToResponseFromListItemRequest(
                itemRequestRepository.findById(requestId)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Запрос " + requestId + " не найден")
                        )
        );
    }
}
package ru.practicum.shareit.utilits;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ShareItPageRequest extends PageRequest {

    protected ShareItPageRequest(int from, int size, Sort sort) {
        super(from, size, sort);
    }
}

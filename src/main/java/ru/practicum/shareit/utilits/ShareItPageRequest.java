package ru.practicum.shareit.utilits;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ShareItPageRequest extends PageRequest {

    public ShareItPageRequest() {
        this(Sort.unsorted());
    }

    public ShareItPageRequest(Sort sort) {
        this(0, 20, sort);
    }

    public ShareItPageRequest(int from, int size) {
        this(from, size, Sort.unsorted());
    }

    public ShareItPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
    }
}


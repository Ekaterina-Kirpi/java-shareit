package ru.practicum.shareit.utilits;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.StateException;

public class ShareItPageRequest extends PageRequest {

    //public ShareItPageRequest(Integer from, Integer size, Sort sort) {
    //   super(from / size, size, sort);


    protected ShareItPageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public static Pageable toMakePage(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }

        if (size <= 0 || from < 0) {
            throw new StateException("Уточнчите правильность параметров отображения");
        }

        int page = from / size;
        return PageRequest.of(page, size);
    }
}

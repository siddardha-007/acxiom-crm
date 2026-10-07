package com.acxiomcrm.util;

import com.acxiomcrm.dto.PageResponse;
import org.springframework.data.domain.Page;

import java.util.function.Function;

public final class PageResponseMapper {

    private PageResponseMapper() {
    }

    public static <T, R> PageResponse<R> map(
            Page<T> page,
            Function<T, R> mapper
    ) {

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(mapper)
                        .toList(),

                page.getNumber(),

                page.getSize(),

                page.getTotalElements(),

                page.getTotalPages(),

                page.isFirst(),

                page.isLast()
        );
    }
}
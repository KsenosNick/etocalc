package ru.home.etocalc.api.dto.response;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.Function;

/**
 * Универсальный DTO для возврата пагинированных данных.
 * Избавляет от проблем сериализации Spring Data Page и LazyInitializationException.
 */
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
    /**
     * Конвертирует Spring Data Page в наш безопасный DTO.
     * Маппинг происходит внутри транзакции, что гарантирует отсутствие LazyInitializationException.
     */
    public static <T, U> PageResponse<U> from(Page<T> page, Function<T, U> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
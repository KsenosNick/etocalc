package ru.home.etocalc.api.dto.response;

import java.util.List;

public record ImportResult(
        int created,
        int updated,
        int skipped,
        int failed,
        List<String> errors
) {
    public static ImportResult empty() {
        return new ImportResult(0, 0, 0, 0, List.of());
    }
}
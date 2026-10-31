package ru.home.etocalc.domain.enums;

public enum ImportMode {
    /** Пропустить, если такой кабель уже есть */
    SKIP,
    /** Перезаписать существующий */
    UPDATE,
    /** Создать копию в любом случае */
    DUPLICATE
}
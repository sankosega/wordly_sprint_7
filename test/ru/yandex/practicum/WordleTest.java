package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    @Test
    void testMainClassExists() {
        assertDoesNotThrow(() -> {
            Class.forName("ru.yandex.practicum.Wordle");
        });
    }
}

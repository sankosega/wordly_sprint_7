package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    private static PrintWriter log;

    @BeforeAll
    static void setUpAll() {
        log = new PrintWriter(System.out, true);
    }

    @Test
    void testLoadDictionary() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.loadDictionary("words_ru.txt");
        
        assertNotNull(dictionary);
        assertFalse(dictionary.isEmpty());
        assertTrue(dictionary.size() > 0);
    }

    @Test
    void testLoadDictionaryFileNotFound() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        
        assertThrows(DictionaryLoadException.class, () -> {
            loader.loadDictionary("nonexistent_file.txt");
        });
    }

    @Test
    void testLoadedWordsAreNormalized() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.loadDictionary("words_ru.txt");
        
        for (String word : dictionary.getWords()) {
            assertEquals(word, word.toLowerCase());
            assertFalse(word.contains("ё"));
            assertEquals(5, word.length());
        }
    }

    @Test
    void testLoadedWordsAreValid() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.loadDictionary("words_ru.txt");
        
        for (String word : dictionary.getWords()) {
            assertTrue(WordleDictionary.isValidFormat(word), 
                    "Word should be valid: " + word);
        }
    }
}

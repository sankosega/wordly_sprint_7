package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    private static PrintWriter log;
    private WordleDictionary dictionary;

    @BeforeAll
    static void setUpAll() {
        log = new PrintWriter(System.out, true);
    }

    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList("герой", "гонец", "слово", "книга", "ручка");
        dictionary = new WordleDictionary(words, log);
    }

    @Test
    void testNormalize() {
        assertEquals("герой", WordleDictionary.normalize("ГЕРОЙ"));
        assertEquals("герой", WordleDictionary.normalize("Герой"));
        assertEquals("елка", WordleDictionary.normalize("ёлка"));
        assertEquals("елка", WordleDictionary.normalize("ЁЛКА"));
        assertNull(WordleDictionary.normalize(null));
    }

    @Test
    void testIsValidFormat() {
        assertTrue(WordleDictionary.isValidFormat("герой"));
        assertTrue(WordleDictionary.isValidFormat("слово"));
        assertTrue(WordleDictionary.isValidFormat("ёлочь"));

        assertFalse(WordleDictionary.isValidFormat("hero"));
        assertFalse(WordleDictionary.isValidFormat("слон"));
        assertFalse(WordleDictionary.isValidFormat("словарь"));
        assertFalse(WordleDictionary.isValidFormat(""));
        assertFalse(WordleDictionary.isValidFormat(null));
        assertFalse(WordleDictionary.isValidFormat("12345"));
        assertFalse(WordleDictionary.isValidFormat("сло во"));
    }

    @Test
    void testContains() {
        assertTrue(dictionary.contains("герой"));
        assertTrue(dictionary.contains("ГЕРОЙ"));
        assertTrue(dictionary.contains("Герой"));
        assertFalse(dictionary.contains("автор"));
    }

    @Test
    void testGetRandomWord() {
        String word = dictionary.getRandomWord();
        assertNotNull(word);
        assertTrue(dictionary.contains(word));
    }

    @Test
    void testSize() {
        assertEquals(5, dictionary.size());
    }

    @Test
    void testIsEmpty() {
        assertFalse(dictionary.isEmpty());
        
        WordleDictionary emptyDict = new WordleDictionary(Arrays.asList(), log);
        assertTrue(emptyDict.isEmpty());
    }

    @Test
    void testGenerateHintAllCorrect() {
        String hint = WordleDictionary.generateHint("герой", "герой");
        assertEquals("+++++", hint);
    }

    @Test
    void testGenerateHintAllWrong() {
        String hint = WordleDictionary.generateHint("книга", "герой");
        assertEquals("---^-", hint);
    }

    @Test
    void testGenerateHintMixed() {
        String hint = WordleDictionary.generateHint("гонец", "герой");
        assertEquals("+^-^-", hint);
    }

    @Test
    void testGenerateHintPartialMatch() {
        String hint = WordleDictionary.generateHint("горка", "герой");
        assertEquals("+^+--", hint);
    }

    @Test
    void testGenerateHintDuplicateLetters() {
        String hint = WordleDictionary.generateHint("аааба", "баааа");
        assertEquals("^++^+", hint);
    }

    @Test
    void testIsWinningHint() {
        assertTrue(WordleDictionary.isWinningHint("+++++"));
        assertFalse(WordleDictionary.isWinningHint("++++^"));
        assertFalse(WordleDictionary.isWinningHint("++++-"));
        assertFalse(WordleDictionary.isWinningHint("-----"));
    }

    @Test
    void testFilterByHints() {
        Set<Character> excludedChars = new HashSet<>();
        Set<Character> requiredChars = new HashSet<>();
        char[] correctPositions = new char[5];
        @SuppressWarnings("unchecked")
        Set<Character>[] wrongPositions = new HashSet[5];
        for (int i = 0; i < 5; i++) {
            wrongPositions[i] = new HashSet<>();
        }
        Set<String> usedWords = new HashSet<>();

        correctPositions[0] = 'г';
        List<String> filtered = dictionary.filterByHints(excludedChars, requiredChars, 
                correctPositions, wrongPositions, usedWords);
        
        assertTrue(filtered.contains("герой"));
        assertTrue(filtered.contains("гонец"));
        assertFalse(filtered.contains("слово"));
    }

    @Test
    void testFilterByHintsWithExcludedChars() {
        Set<Character> excludedChars = new HashSet<>(Arrays.asList('г'));
        Set<Character> requiredChars = new HashSet<>();
        char[] correctPositions = new char[5];
        @SuppressWarnings("unchecked")
        Set<Character>[] wrongPositions = new HashSet[5];
        for (int i = 0; i < 5; i++) {
            wrongPositions[i] = new HashSet<>();
        }
        Set<String> usedWords = new HashSet<>();

        List<String> filtered = dictionary.filterByHints(excludedChars, requiredChars, 
                correctPositions, wrongPositions, usedWords);
        
        assertFalse(filtered.contains("герой"));
        assertFalse(filtered.contains("гонец"));
        assertTrue(filtered.contains("слово"));
    }

    @Test
    void testFilterByHintsWithRequiredChars() {
        Set<Character> excludedChars = new HashSet<>();
        Set<Character> requiredChars = new HashSet<>(Arrays.asList('к'));
        char[] correctPositions = new char[5];
        @SuppressWarnings("unchecked")
        Set<Character>[] wrongPositions = new HashSet[5];
        for (int i = 0; i < 5; i++) {
            wrongPositions[i] = new HashSet<>();
        }
        Set<String> usedWords = new HashSet<>();

        List<String> filtered = dictionary.filterByHints(excludedChars, requiredChars, 
                correctPositions, wrongPositions, usedWords);
        
        assertTrue(filtered.contains("книга"));
        assertTrue(filtered.contains("ручка"));
        assertFalse(filtered.contains("герой"));
    }

    @Test
    void testEmptyDictionaryException() {
        WordleDictionary emptyDict = new WordleDictionary(Arrays.asList(), log);
        assertThrows(EmptyDictionaryException.class, emptyDict::getRandomWord);
    }
}

package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    private static PrintWriter log;
    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeAll
    static void setUpAll() {
        log = new PrintWriter(System.out, true);
    }

    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList("герой", "гонец", "слово", "книга", "ручка", 
                "автор", "город", "гроза", "груша", "горка");
        dictionary = new WordleDictionary(words, log);
        game = new WordleGame(dictionary, log, "герой");
    }

    @Test
    void testGameInitialization() {
        assertEquals(6, game.getAttemptsLeft());
        assertFalse(game.isGameOver());
        assertFalse(game.isWon());
        assertEquals("герой", game.getAnswer());
    }

    @Test
    void testMakeGuessCorrect() throws WordleException {
        String hint = game.makeGuess("герой");
        
        assertEquals("+++++", hint);
        assertTrue(game.isGameOver());
        assertTrue(game.isWon());
        assertEquals(5, game.getAttemptsLeft());
    }

    @Test
    void testMakeGuessWrong() throws WordleException {
        String hint = game.makeGuess("книга");
        
        assertEquals("---^-", hint);
        assertFalse(game.isGameOver());
        assertFalse(game.isWon());
        assertEquals(5, game.getAttemptsLeft());
    }

    @Test
    void testMakeGuessMixed() throws WordleException {
        String hint = game.makeGuess("гонец");
        
        assertEquals("+^-^-", hint);
        assertFalse(game.isGameOver());
    }

    @Test
    void testMakeGuessInvalidFormat() {
        assertThrows(InvalidWordException.class, () -> game.makeGuess("слон"));
        assertThrows(InvalidWordException.class, () -> game.makeGuess("словарь"));
        assertThrows(InvalidWordException.class, () -> game.makeGuess(""));
        assertThrows(InvalidWordException.class, () -> game.makeGuess("12345"));
    }

    @Test
    void testMakeGuessWordNotInDictionary() {
        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeGuess("абвгд"));
    }

    @Test
    void testMakeGuessDuplicateWord() throws WordleException {
        game.makeGuess("книга");
        assertThrows(InvalidWordException.class, () -> game.makeGuess("книга"));
    }

    @Test
    void testGameOverAfterMaxAttempts() throws WordleException {
        game.makeGuess("книга");
        game.makeGuess("слово");
        game.makeGuess("ручка");
        game.makeGuess("автор");
        game.makeGuess("город");
        game.makeGuess("гроза");
        
        assertTrue(game.isGameOver());
        assertFalse(game.isWon());
        assertEquals(0, game.getAttemptsLeft());
    }

    @Test
    void testCannotGuessAfterGameOver() throws WordleException {
        game.makeGuess("герой");
        
        assertTrue(game.isGameOver());
        assertThrows(RuntimeException.class, () -> game.makeGuess("книга"));
    }

    @Test
    void testGetSuggestion() {
        String suggestion = game.getSuggestion();
        
        assertNotNull(suggestion);
        assertTrue(dictionary.contains(suggestion));
    }

    @Test
    void testGetSuggestionAfterGuess() throws WordleException {
        game.makeGuess("гонец");
        
        String suggestion = game.getSuggestion();
        assertNotNull(suggestion);
        assertTrue(dictionary.contains(suggestion));
    }

    @Test
    void testGetSuggestionReturnsNullAfterGameOver() throws WordleException {
        game.makeGuess("герой");
        
        assertNull(game.getSuggestion());
    }

    @Test
    void testGuessHistory() throws WordleException {
        game.makeGuess("книга");
        game.makeGuess("слово");
        
        List<String> history = game.getGuessHistory();
        assertEquals(2, history.size());
        assertEquals("книга", history.get(0));
        assertEquals("слово", history.get(1));
    }

    @Test
    void testHintHistory() throws WordleException {
        game.makeGuess("герой");
        
        List<String> hints = game.getHintHistory();
        assertEquals(1, hints.size());
        assertEquals("+++++", hints.get(0));
    }

    @Test
    void testNormalizationOnInput() throws WordleException {
        String hint = game.makeGuess("ГЕРОЙ");
        assertEquals("+++++", hint);
    }

    @Test
    void testYoReplacement() throws WordleException {
        List<String> words = Arrays.asList("елочк", "ручка");
        WordleDictionary dict = new WordleDictionary(words, log);
        WordleGame g = new WordleGame(dict, log, "елочк");
        
        String hint = g.makeGuess("ёлочк");
        assertEquals("+++++", hint);
    }

    @Test
    void testComputerCanPlayFullGame() {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary fullDict = loader.loadDictionary("words_ru.txt");
        WordleGame fullGame = new WordleGame(fullDict, log);
        
        int maxAttempts = 20;
        int attempts = 0;
        
        while (!fullGame.isGameOver() && attempts < maxAttempts) {
            String suggestion = fullGame.getSuggestion();
            if (suggestion == null) {
                break;
            }
            try {
                fullGame.makeGuess(suggestion);
            } catch (WordleException e) {
                fail("Suggestion should be valid: " + e.getMessage());
            }
            attempts++;
        }
        
        assertTrue(fullGame.isGameOver() || attempts >= maxAttempts);
    }
}

package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class TestRunner {
    
    private static PrintWriter log = new PrintWriter(System.out, true);
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Запуск тестов ===\n");

        testWordleDictionary();
        testWordleDictionaryLoader();
        testWordleGame();

        System.out.println("Результаты");
        System.out.println("Пройдено: " + passed);
        System.out.println("Провалено: " + failed);
    }

    private static void testWordleDictionary() {
        System.out.println("--- WordleDictionary Tests ---");

        test("normalize lowercase", () -> {
            assertEquals("герой", WordleDictionary.normalize("ГЕРОЙ"));
        });

        test("normalize ё to е", () -> {
            assertEquals("елка", WordleDictionary.normalize("ёлка"));
        });

        test("isValidFormat valid", () -> {
            assertTrue(WordleDictionary.isValidFormat("герой"));
        });

        test("isValidFormat invalid length", () -> {
            assertFalse(WordleDictionary.isValidFormat("слон"));
        });

        test("isValidFormat invalid chars", () -> {
            assertFalse(WordleDictionary.isValidFormat("hello"));
        });

        test("generateHint all correct", () -> {
            assertEquals("+++++", WordleDictionary.generateHint("герой", "герой"));
        });

        test("generateHint all wrong", () -> {
            assertEquals("---^-", WordleDictionary.generateHint("книга", "герой"));
        });

        test("generateHint mixed", () -> {
            assertEquals("+^-^-", WordleDictionary.generateHint("гонец", "герой"));
        });

        test("isWinningHint true", () -> {
            assertTrue(WordleDictionary.isWinningHint("+++++"));
        });

        test("isWinningHint false", () -> {
            assertFalse(WordleDictionary.isWinningHint("++++^"));
        });

        test("contains word", () -> {
            List<String> words = Arrays.asList("герой", "слово");
            WordleDictionary dict = new WordleDictionary(words, log);
            assertTrue(dict.contains("герой"));
            assertTrue(dict.contains("ГЕРОЙ"));
            assertFalse(dict.contains("книга"));
        });

        test("getRandomWord", () -> {
            List<String> words = Arrays.asList("герой", "слово");
            WordleDictionary dict = new WordleDictionary(words, log);
            String word = dict.getRandomWord();
            assertTrue(words.contains(word));
        });
    }

    private static void testWordleDictionaryLoader() {
        System.out.println("\n--- WordleDictionaryLoader Tests ---");

        test("loadDictionary success", () -> {
            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dict = loader.loadDictionary("words_ru.txt");
            assertTrue(dict.size() > 0);
        });

        test("loadDictionary words normalized", () -> {
            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dict = loader.loadDictionary("words_ru.txt");
            for (String word : dict.getWords()) {
                assertEquals(word, word.toLowerCase());
                assertEquals(5, word.length());
            }
        });

        test("loadDictionary file not found", () -> {
            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            try {
                loader.loadDictionary("nonexistent.txt");
                fail("Should throw exception");
            } catch (DictionaryLoadException e) {
                assertTrue(true);
            }
        });
    }

    private static void testWordleGame() {
        System.out.println("\n--- WordleGame Tests ---");

        test("game initialization", () -> {
            List<String> words = Arrays.asList("герой", "слово", "книга");
            WordleDictionary dict = new WordleDictionary(words, log);
            WordleGame game = new WordleGame(dict, log, "герой");
            
            assertEquals(6, game.getAttemptsLeft());
            assertFalse(game.isGameOver());
            assertFalse(game.isWon());
            assertEquals("герой", game.getAnswer());
        });

        test("makeGuess correct", () -> {
            List<String> words = Arrays.asList("герой", "слово");
            WordleDictionary dict = new WordleDictionary(words, log);
            WordleGame game = new WordleGame(dict, log, "герой");
            
            try {
                String hint = game.makeGuess("герой");
                assertEquals("+++++", hint);
                assertTrue(game.isWon());
                assertTrue(game.isGameOver());
            } catch (WordleException e) {
                fail(e.getMessage());
            }
        });

        test("makeGuess wrong", () -> {
            List<String> words = Arrays.asList("герой", "слово", "книга");
            WordleDictionary dict = new WordleDictionary(words, log);
            WordleGame game = new WordleGame(dict, log, "герой");
            
            try {
                String hint = game.makeGuess("книга");
                assertEquals("---^-", hint);
                assertFalse(game.isWon());
                assertFalse(game.isGameOver());
                assertEquals(5, game.getAttemptsLeft());
            } catch (WordleException e) {
                fail(e.getMessage());
            }
        });

        test("makeGuess invalid format", () -> {
            List<String> words = Arrays.asList("герой");
            WordleDictionary dict = new WordleDictionary(words, log);
            WordleGame game = new WordleGame(dict, log, "герой");
            
            try {
                game.makeGuess("слон");
                fail("Should throw InvalidWordException");
            } catch (InvalidWordException e) {
                assertTrue(true);
            } catch (WordleException e) {
                fail("Wrong exception type");
            }
        });

        test("makeGuess word not in dictionary", () -> {
            List<String> words = Arrays.asList("герой");
            WordleDictionary dict = new WordleDictionary(words, log);
            WordleGame game = new WordleGame(dict, log, "герой");
            
            try {
                game.makeGuess("абвгд");
                fail("Should throw WordNotFoundInDictionaryException");
            } catch (WordNotFoundInDictionaryException e) {
                assertTrue(true);
            } catch (WordleException e) {
                fail("Wrong exception type");
            }
        });

        test("makeGuess duplicate word", () -> {
            List<String> words = Arrays.asList("герой", "слово");
            WordleDictionary dict = new WordleDictionary(words, log);
            WordleGame game = new WordleGame(dict, log, "герой");
            
            try {
                game.makeGuess("слово");
                game.makeGuess("слово");
                fail("Should throw InvalidWordException");
            } catch (InvalidWordException e) {
                assertTrue(true);
            } catch (WordleException e) {
                fail("Wrong exception type");
            }
        });

        test("getSuggestion returns valid word", () -> {
            List<String> words = Arrays.asList("герой", "слово", "книга");
            WordleDictionary dict = new WordleDictionary(words, log);
            WordleGame game = new WordleGame(dict, log, "герой");
            
            String suggestion = game.getSuggestion();
            assertTrue(dict.contains(suggestion));
        });

        test("game over after 6 attempts", () -> {
            List<String> words = Arrays.asList("герой", "слово", "книга", "ручка", "автор", "город", "гроза");
            WordleDictionary dict = new WordleDictionary(words, log);
            WordleGame game = new WordleGame(dict, log, "герой");
            
            try {
                game.makeGuess("слово");
                game.makeGuess("книга");
                game.makeGuess("ручка");
                game.makeGuess("автор");
                game.makeGuess("город");
                game.makeGuess("гроза");
                
                assertTrue(game.isGameOver());
                assertFalse(game.isWon());
                assertEquals(0, game.getAttemptsLeft());
            } catch (WordleException e) {
                fail(e.getMessage());
            }
        });

        test("computer can play full game", () -> {
            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dict = loader.loadDictionary("words_ru.txt");
            WordleGame game = new WordleGame(dict, log);
            
            int maxAttempts = 20;
            int attempts = 0;
            
            while (!game.isGameOver() && attempts < maxAttempts) {
                String suggestion = game.getSuggestion();
                if (suggestion == null) break;
                try {
                    game.makeGuess(suggestion);
                } catch (WordleException e) {
                    fail("Suggestion should be valid: " + e.getMessage());
                }
                attempts++;
            }
            
            assertTrue(game.isGameOver() || attempts >= maxAttempts);
        });
    }

    private static void test(String name, Runnable testCase) {
        try {
            testCase.run();
            System.out.println("  ✓ " + name);
            passed++;
        } catch (AssertionError e) {
            System.out.println("  ✗ " + name + ": " + e.getMessage());
            failed++;
        } catch (Exception e) {
            System.out.println("  ✗ " + name + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
            failed++;
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
    }

    private static void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError("Expected true");
    }

    private static void assertFalse(boolean condition) {
        if (condition) throw new AssertionError("Expected false");
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }
}

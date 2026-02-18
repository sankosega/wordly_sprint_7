package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordleGame {

    public static final int MAX_ATTEMPTS = 6;

    private final String answer;
    private int attemptsLeft;
    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private boolean gameOver;
    private boolean won;

    private final Set<Character> excludedChars;
    private final Set<Character> requiredChars;
    private final char[] correctPositions;
    @SuppressWarnings("unchecked")
    private final Set<Character>[] wrongPositions = new HashSet[WordleDictionary.WORD_LENGTH];
    private final Set<String> usedWords;
    private final List<String> guessHistory;
    private final List<String> hintHistory;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.attemptsLeft = MAX_ATTEMPTS;
        this.gameOver = false;
        this.won = false;

        this.excludedChars = new HashSet<>();
        this.requiredChars = new HashSet<>();
        this.correctPositions = new char[WordleDictionary.WORD_LENGTH];
        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            wrongPositions[i] = new HashSet<>();
        }
        this.usedWords = new HashSet<>();
        this.guessHistory = new ArrayList<>();
        this.hintHistory = new ArrayList<>();

        log.println("Игра начата. Загаданное слово: " + answer);
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter log, String answer) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = WordleDictionary.normalize(answer);
        this.attemptsLeft = MAX_ATTEMPTS;
        this.gameOver = false;
        this.won = false;

        this.excludedChars = new HashSet<>();
        this.requiredChars = new HashSet<>();
        this.correctPositions = new char[WordleDictionary.WORD_LENGTH];
        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            wrongPositions[i] = new HashSet<>();
        }
        this.usedWords = new HashSet<>();
        this.guessHistory = new ArrayList<>();
        this.hintHistory = new ArrayList<>();

        log.println("Игра начата с заданным словом: " + this.answer);
    }

    public String makeGuess(String input) throws WordleException {
        if (gameOver) {
            throw new RuntimeException("Игра уже завершена");
        }

        String normalized = WordleDictionary.normalize(input);

        if (!WordleDictionary.isValidFormat(normalized)) {
            throw new InvalidWordException("Слово должно состоять из 5 русских букв");
        }

        if (!dictionary.contains(normalized)) {
            throw new WordNotFoundInDictionaryException(normalized);
        }

        if (usedWords.contains(normalized)) {
            throw new InvalidWordException("Это слово уже было использовано");
        }

        attemptsLeft--;
        usedWords.add(normalized);
        guessHistory.add(normalized);

        String hint = WordleDictionary.generateHint(normalized, answer);
        hintHistory.add(hint);

        updateHintState(normalized, hint);

        log.println("Попытка: " + normalized + " -> " + hint + " (осталось: " + attemptsLeft + ")");

        if (WordleDictionary.isWinningHint(hint)) {
            gameOver = true;
            won = true;
            log.println("Победа!");
        } else if (attemptsLeft <= 0) {
            gameOver = true;
            won = false;
            log.println("Поражение. Загаданное слово: " + answer);
        }

        return hint;
    }

    private void updateHintState(String guess, String hint) {
        for (int i = 0; i < WordleDictionary.WORD_LENGTH; i++) {
            char c = guess.charAt(i);
            char h = hint.charAt(i);

            if (h == '+') {
                correctPositions[i] = c;
                requiredChars.add(c);
            } else if (h == '^') {
                requiredChars.add(c);
                wrongPositions[i].add(c);
            } else {
                excludedChars.add(c);
            }
        }
    }

    public String getSuggestion() {
        if (gameOver) {
            return null;
        }

        List<String> candidates = dictionary.filterByHints(
                excludedChars, requiredChars, correctPositions, wrongPositions, usedWords);

        if (candidates.isEmpty()) {
            log.println("Нет подходящих слов для подсказки");
            return null;
        }

        Random random = new Random();
        String suggestion = candidates.get(random.nextInt(candidates.size()));
        log.println("Предложена подсказка: " + suggestion);
        return suggestion;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWon() {
        return won;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getGuessHistory() {
        return new ArrayList<>(guessHistory);
    }

    public List<String> getHintHistory() {
        return new ArrayList<>(hintHistory);
    }
}

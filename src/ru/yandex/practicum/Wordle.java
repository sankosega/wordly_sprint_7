package ru.yandex.practicum;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(LOG_FILE), StandardCharsets.UTF_8), true)) {

            log.println("Запуск игры Wordle");

            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary = loader.loadDictionary(DICTIONARY_FILE);

            WordleGame game = new WordleGame(dictionary, log);

            playGame(game, log);

        } catch (IOException e) {
            System.err.println("Ошибка создания лог-файла: " + e.getMessage());
        } catch (DictionaryLoadException | EmptyDictionaryException e) {
            System.err.println("Ошибка загрузки словаря: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void playGame(WordleGame game, PrintWriter log) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());

        System.out.println("WORDLE");
        System.out.println("Угадайте слово из 5 букв. У вас " + WordleGame.MAX_ATTEMPTS + " попыток.");
        System.out.println("Нажмите Enter для подсказки.");
        System.out.println();

        while (!game.isGameOver()) {
            System.out.println("Попыток осталось: " + game.getAttemptsLeft());
            System.out.print("> ");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                String suggestion = game.getSuggestion();
                if (suggestion != null) {
                    System.out.println("Подсказка: " + suggestion);
                    processGuess(game, suggestion, log);
                } else {
                    System.out.println("Нет подходящих слов для подсказки.");
                }
            } else {
                processGuess(game, input, log);
            }
        }

        printGameResult(game);
    }

    private static void processGuess(WordleGame game, String input, PrintWriter log) {
        try {
            String hint = game.makeGuess(input);
            System.out.println(WordleDictionary.normalize(input));
            System.out.println(hint);
            System.out.println();
        } catch (InvalidWordException e) {
            System.out.println("Ошибка: " + e.getMessage());
            log.println("Ошибка ввода: " + e.getMessage());
        } catch (WordNotFoundInDictionaryException e) {
            System.out.println("Ошибка: " + e.getMessage());
            log.println("Слово не найдено: " + e.getMessage());
        } catch (WordleException e) {
            System.out.println("Ошибка: " + e.getMessage());
            log.println("Игровая ошибка: " + e.getMessage());
        }
    }

    private static void printGameResult(WordleGame game) {
        System.out.println("=== ИГРА ОКОНЧЕНА ===");
        if (game.isWon()) {
            int attempts = WordleGame.MAX_ATTEMPTS - game.getAttemptsLeft();
            System.out.println("Поздравляем! Вы угадали слово за " + attempts + " попыток!");
        } else {
            System.out.println("Вы проиграли. Загаданное слово: " + game.getAnswer());
        }
    }
}

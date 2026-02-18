package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary loadDictionary(String filename) {
        log.println("Загрузка словаря из файла: " + filename);
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = WordleDictionary.normalize(line.trim());
                if (WordleDictionary.isValidFormat(normalized)) {
                    words.add(normalized);
                }
            }

        } catch (FileNotFoundException e) {
            throw new DictionaryLoadException("Файл словаря не найден: " + filename, e);
        } catch (IOException e) {
            throw new DictionaryLoadException("Ошибка чтения файла словаря: " + filename, e);
        }

        if (words.isEmpty()) {
            throw new EmptyDictionaryException("Словарь пуст или не содержит подходящих слов: " + filename);
        }

        log.println("Загружено слов из файла: " + words.size());
        return new WordleDictionary(words, log);
    }
}

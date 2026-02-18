package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordleDictionary {

    public static final int WORD_LENGTH = 5;
    private final List<String> words;
    private final Set<String> wordSet;
    private final Random random;
    private final PrintWriter log;

    public WordleDictionary(List<String> words, PrintWriter log) {
        this.words = new ArrayList<>(words);
        this.wordSet = new HashSet<>(words);
        this.random = new Random();
        this.log = log;
        log.println("Словарь загружен, слов: " + words.size());
    }

    public static String normalize(String word) {
        if (word == null) {
            return null;
        }
        return word.toLowerCase().replace('ё', 'е');
    }

    public static boolean isValidFormat(String word) {
        if (word == null || word.length() != WORD_LENGTH) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c < 'а' || c > 'я') {
                if (c != 'ё') {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean contains(String word) {
        return wordSet.contains(normalize(word));
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new EmptyDictionaryException("Словарь пуст");
        }
        return words.get(random.nextInt(words.size()));
    }

    public int size() {
        return words.size();
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public List<String> filterByHints(Set<Character> excludedChars,
                                       Set<Character> requiredChars,
                                       char[] correctPositions,
                                       Set<Character>[] wrongPositions,
                                       Set<String> usedWords) {
        List<String> filtered = new ArrayList<>();

        for (String word : words) {
            if (usedWords.contains(word)) {
                continue;
            }
            if (matchesHints(word, excludedChars, requiredChars, correctPositions, wrongPositions)) {
                filtered.add(word);
            }
        }

        log.println("Отфильтровано слов: " + filtered.size());
        return filtered;
    }

    private boolean matchesHints(String word,
                                  Set<Character> excludedChars,
                                  Set<Character> requiredChars,
                                  char[] correctPositions,
                                  Set<Character>[] wrongPositions) {
        for (int i = 0; i < WORD_LENGTH; i++) {
            char c = word.charAt(i);

            if (correctPositions[i] != 0 && correctPositions[i] != c) {
                return false;
            }

            if (wrongPositions[i] != null && wrongPositions[i].contains(c)) {
                return false;
            }
        }

        for (char c : requiredChars) {
            if (word.indexOf(c) == -1) {
                return false;
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            char c = word.charAt(i);
            if (excludedChars.contains(c) && !requiredChars.contains(c)) {
                return false;
            }
        }

        return true;
    }

    public static String generateHint(String guess, String answer) {
        StringBuilder hint = new StringBuilder();
        boolean[] answerUsed = new boolean[WORD_LENGTH];
        boolean[] guessMatched = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                guessMatched[i] = true;
                answerUsed[i] = true;
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guessMatched[i]) {
                hint.append('+');
            } else {
                char c = guess.charAt(i);
                boolean found = false;
                for (int j = 0; j < WORD_LENGTH; j++) {
                    if (!answerUsed[j] && answer.charAt(j) == c) {
                        found = true;
                        answerUsed[j] = true;
                        break;
                    }
                }
                hint.append(found ? '^' : '-');
            }
        }

        return hint.toString();
    }

    public static boolean isWinningHint(String hint) {
        return hint.equals("+++++");
    }
}

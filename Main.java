import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

public class Main {
    private String currentGuess;
    private char letter;
    private char guessLetter;
    private String currentWord;
    private char[] greenLetters;
    private char[][] yellowLetters;
    private char[] greyLetters;
    private int[] yellowCount;
    private int greyCount;
    private Scanner scanner;
    private Boolean validWord;
    private Set<String> usedWords;
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    // Constructor
    public Main() {
        this.currentGuess = "";
        this.letter = '\0';
        this.guessLetter = '\0';
        this.currentWord = "crane";
        this.greenLetters = new char[5];
        this.yellowLetters = new char[5][26];
        this.greyLetters = new char[26];
        this.yellowCount = new int[5];
        this.greyCount = 0;
        this.scanner = new Scanner(System.in);
        this.validWord = true;
        this.usedWords = new HashSet<>();
    }

    public static void main(String[] args) throws IOException {
        Main bot = new Main();
        bot.runProgram();
    }

    public void runProgram() throws IOException {
        // comment out when not testing
        Random rand = new Random();
        int randNum = rand.nextInt(5757);
        String testWord = "";
        BufferedReader reader = new BufferedReader(new java.io.FileReader("words.txt"));
        for (int i = 0; i < randNum; i++) {
            testWord = reader.readLine();
        }

        reader.close();

        // order of process
        for (int i = 0; i <= 5; i++) {
            //System.out.println("your test wordle word is " + GREEN + testWord + RESET); // be sure to reset to testWord
            if (currentWord == null) {
                scanner.close();
                System.exit(0);
            }
            System.out.println("input " + YELLOW + currentWord + RESET + " into wordle.");
            currentGuess = getAnswer(); // get answer from player input of the first word
            for (int j = 0; j < 5; j++) {
                if (currentGuess.charAt(j) == 'G') {
                    if (j == 4) {
                        System.out.println(GREEN + "YOU WIN!!!!" + RESET);
                        scanner.close();
                        System.exit(1);
                    }
                } else
                    break;
            }
            updateArray();
            currentWord = getNewWord();
        }
        System.out.println(RED + "you are out of tries" + RESET);
        scanner.close();
        System.exit(2);
    }

    private String getAnswer() {
        String input;
        boolean validInput = true;
        do {
            System.out.println("Enter answer (5 letters, only G, Y, or N):");
            input = scanner.nextLine().toUpperCase();
            if (input.length() != 5) {
                System.out.println("\nInvalid input, input can only be 5 letters.\n");
                validInput = false;
                continue;
            } else
                validInput = true;
            for (int i = 0; i < 5; i++) {
                char c = input.charAt(i);
                if (c != 'G' && c != 'Y' && c != 'N') {
                    System.out.println("\nInvalid input, input should only have G, Y, or N.\n");
                    validInput = false;
                    break;
                } else
                    validInput = true;
            }
        } while (!validInput);
        return input;
    }

    private void updateArray() {
        for (int i = 0; i < 5; i++) {
            guessLetter = currentGuess.charAt(i);
            letter = currentWord.charAt(i);
            switch (guessLetter) {
                case 'G':
                    greenLetters[i] = letter;
                    break;
                case 'Y':
                    yellowLetters[i][yellowCount[i]++] = letter;
                    break;
                case 'N':
                    greyLetters[greyCount++] = letter;
                    break;
                default:
                    System.out.println("invalid input");
                    break;
            }
        }
    }

    private String getNewWord() {
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader("words.txt"))) {
            while ((currentWord = reader.readLine()) != null) {
                if (usedWords.contains(currentWord))
                    continue;
                validWord = true;

                // green letter test
                for (int i = 0; i < 5; i++) {
                    if (!validWord)
                        break;
                    if (greenLetters[i] != '\0') {
                        if (currentWord.charAt(i) != greenLetters[i]) {
                            validWord = false;
                            usedWords.add(currentWord);
                            break;
                        }
                    }
                }

                // grey letter test
                for (int i = 0; i < greyLetters.length; i++) {
                    if (!validWord)
                        break;
                    if (greyLetters[i] != '\0') {
                        for (int j = 0; j < 5; j++) {
                            if (currentWord.charAt(j) == greyLetters[i]) {
                                validWord = false;
                                usedWords.add(currentWord);
                                break;
                            }
                        }
                    }

                }
                // yellow letter test
                for (int i = 0; i < yellowLetters.length; i++) {
                    for (int j = 0; j < yellowLetters[i].length; j++) {
                        if (yellowLetters[i][j] != '\0') {
                            if (currentWord.charAt(i) == yellowLetters[i][j]) {
                                validWord = false;
                                usedWords.add(currentWord);
                                break;
                            }
                        }
                    }
                }

                // green to grey comparison test
                for (int i = 0; i < greenLetters.length; i++) {
                    for (int j = 0; j < greyLetters.length; j++) {
                        if (greenLetters[i] == greyLetters[j] && greenLetters[i] != '\0' && greyLetters[j] != '\0') {
                            greyLetters[j] = '\0';
                            greyCount--;
                            System.out.println(greyCount);
                        }
                    }
                }

                if (validWord) {
                    break;
                }
            }
        } catch (java.io.IOException e) {
            System.out.println(RED + "something went wrong" + RESET);
            e.printStackTrace();
        }
        // System.out.println(RED + usedWords + RESET);
        // System.out.println(usedWords.size());
        System.out.println("\nKnown green letters: " + GREEN + java.util.Arrays.toString(greenLetters) + RESET);
        System.out.println("Known yellow letters: " + YELLOW + java.util.Arrays.deepToString(yellowLetters) + RESET);
        System.out.println("Known red letters: " + RED + java.util.Arrays.toString(greyLetters) + RESET + "\n");
        // System.out.println("new word is " + YELLOW + currentWord + RESET);
        return currentWord;
    }
}
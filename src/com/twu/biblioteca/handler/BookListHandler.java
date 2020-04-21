package com.twu.biblioteca.handler;

import com.twu.biblioteca.component.Book;
import com.twu.biblioteca.component.BookAlreadyExistError;
import com.twu.biblioteca.component.Library;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;

public class BookListHandler extends InputHandler {

    public static final int MAX_DISPLAY_ITEMS = 10;
    public static final String SHUFFLE_FLAG = "SHUFFLE";
    protected static final Library library = new Library("Biblioteca");

    private Book[] bookOptionReference;

    /**
     * Load mock data from JSON file
     */
    public static void loadData() {
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader("book_data.json")) {
            JSONArray jsonObject = (JSONArray) parser.parse(reader);
            for (Object o: jsonObject) {
                JSONObject book = (JSONObject) o;
                library.addBook(new Book(
                        (String) book.get("book_name"),
                        (String) book.get("author"),
                        Math.toIntExact((Long) book.get("publication_year"))));
            }
        } catch (IOException | ParseException | BookAlreadyExistError e) {
            e.printStackTrace();
        }
    }

    /**
     * Override parse input method
     */
    @Override
    protected String[] parseInput(String input) {
        if (input.equals(SHUFFLE_FLAG)) {
            this.run();
        }
        try {
            String[] parsedInput = super.parseInput(input);
            Book bookReference = this.bookOptionReference[Integer.parseInt(parsedInput[0])];
            return new String[]{ bookReference.getDescription().getIdentifier() };
        } catch (IndexOutOfBoundsException e) {
            this.redirectFromInvalidInput();
        }
        return new String[]{ };
    }

    /**
     * Print heading message
     */
    @Override
    protected void printHeading() {
        System.out.println("Here are some of our selections:");
    }

    /**
     * Print footer message
     */
    @Override
    protected void printFooter() {
        System.out.println("\nCurrent number of available books: " + library.getAvailableBookCount());
        System.out.print("Type a digit to check out the one you find interesting. ");
        System.out.println("You can type SHUFFLE to generate a new list of books.");
    }

    /**
     * Retrieve options given optional user input
     *
     * @param input User input
     * @return Array of options in string form
     */
    @Override
    protected String[] retrieveOptions(String... input) {
        ArrayList<Book> bookCollection = new ArrayList<>(library.getAvailableBooks());
        Collections.shuffle(bookCollection);

        int maximumReturn = Math.min(MAX_DISPLAY_ITEMS, bookCollection.size());
        String[] bookStringCollection = new String[maximumReturn];
        this.bookOptionReference = new Book[maximumReturn];

        int count = 0;
        for (Book book: bookCollection) {
            bookStringCollection[count] = book.getDescription().toString();
            this.bookOptionReference[count] = book;
            count++;
            if (count >= maximumReturn) break;
        }

        return bookStringCollection;
    }
}

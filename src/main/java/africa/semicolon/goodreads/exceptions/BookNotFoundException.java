package africa.semicolon.goodreads.exceptions;

public class BookNotFoundException extends GoodReadsException {
    public BookNotFoundException(String message, int statusCode) {
        super(message, statusCode);
    }
}

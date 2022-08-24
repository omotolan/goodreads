package africa.semicolon.goodreads.exceptions;

public class UserNotFoundException extends GoodReadsException {
    public UserNotFoundException(String message, int statusCode) {
        super(message, statusCode);
    }
}

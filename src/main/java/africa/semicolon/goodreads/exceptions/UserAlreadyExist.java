package africa.semicolon.goodreads.exceptions;

public class UserAlreadyExist extends GoodReadsException {

    public UserAlreadyExist(String message, int statusCode) {
        super(message, statusCode);
    }
}

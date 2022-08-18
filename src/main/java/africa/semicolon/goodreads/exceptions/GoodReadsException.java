package africa.semicolon.goodreads.exceptions;

public class GoodReadsException extends Exception {
    private final int statusCode;

    public GoodReadsException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

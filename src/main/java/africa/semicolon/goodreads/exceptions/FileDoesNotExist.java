package africa.semicolon.goodreads.exceptions;

public class FileDoesNotExist extends GoodReadsException {
    public FileDoesNotExist(String message, int statusCode) {
        super(message, statusCode);
    }
}

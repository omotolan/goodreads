package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.data.models.Book;
import africa.semicolon.goodreads.dto.BookDto;
import africa.semicolon.goodreads.dto.BookItemUploadRequest;
import africa.semicolon.goodreads.dto.Credentials;
import africa.semicolon.goodreads.exceptions.BookNotFoundException;
import africa.semicolon.goodreads.exceptions.GoodReadsException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface BookService {
    CompletableFuture<Map<String, Credentials>> generateUploadURLs(String fileExtension, String imageExtension) throws ExecutionException, InterruptedException;
    Book save(BookItemUploadRequest bookItemUploadRequest);
    Book findBookByTitle(String title) throws BookNotFoundException;
    Map<String, String> generateDownloadUrls(String fileName, String imageFileName) throws GoodReadsException, ExecutionException, InterruptedException;
    Map<String, Object> findAll(int pageNumber, int noOfItems);

    List<BookDto> getAllBooksForUser(String email);
}

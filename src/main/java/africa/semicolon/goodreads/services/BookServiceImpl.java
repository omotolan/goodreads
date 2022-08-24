package africa.semicolon.goodreads.services;

import africa.semicolon.goodreads.data.models.Book;
import africa.semicolon.goodreads.data.repository.BookRepository;
import africa.semicolon.goodreads.dto.BookDto;
import africa.semicolon.goodreads.dto.request.BookItemUploadRequest;
import africa.semicolon.goodreads.dto.Credentials;
import africa.semicolon.goodreads.exceptions.BookNotFoundException;
import africa.semicolon.goodreads.exceptions.FileDoesNotExist;
import africa.semicolon.goodreads.exceptions.GoodReadsException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class BookServiceImpl implements BookService {

    private final AmazonS3 amazonS3;
    private final String IMAGE_BUCKET;
    private final String FILE_BUCKET;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookServiceImpl(AmazonS3 amazonS3, BookRepository bookRepository, ModelMapper modelMapper) {
        this.amazonS3 = amazonS3;
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
        this.IMAGE_BUCKET = System.getenv("IMAGE_BUCKET");
        this.FILE_BUCKET = System.getenv("FILE_BUCKET");
    }


    @Override
    public CompletableFuture<Map<String, Credentials>> generateUploadURLs(String fileExtension, String imageExtension) throws ExecutionException, InterruptedException {
        String fileName = UUID.randomUUID() + fileExtension;
        String imageFileName = UUID.randomUUID() + imageExtension;

        String imageUploadUrl = generateUploadUrlForImage(imageFileName).get();
        String fileUploadUrl = generateUploadUrlForFile(fileName).get();

        Map<String, Credentials> map = new HashMap<>();
        map.put("fileCredentials", new Credentials(fileName, fileUploadUrl));
        map.put("imageCredentials", new Credentials(imageFileName, imageUploadUrl));

        return CompletableFuture.completedFuture(map);
    }

    @Async("taskExecutor")
    public CompletableFuture<String> generateUrl(String bucketName, String fileName, HttpMethod httpMethod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10);
        return CompletableFuture.completedFuture(amazonS3.generatePresignedUrl(bucketName, fileName, calendar.getTime(), httpMethod).toString());
    }

    @Override
    public Book save(BookItemUploadRequest bookItemUploadRequest) {
        Book book = modelMapper.map(bookItemUploadRequest, Book.class);
        return bookRepository.save(book);
    }

    @Override
    public Book findBookByTitle(String title) throws BookNotFoundException {
        Book book = bookRepository.findBookByTitleIgnoreCase(title);
        if (book == null) {
            throw new BookNotFoundException("Book not found", 404);
        }
        return book;
    }

    @Override
    public Map<String, String> generateDownloadUrls(String fileName, String imageFileName) throws GoodReadsException, ExecutionException, InterruptedException {
        if (!amazonS3.doesObjectExist(FILE_BUCKET, fileName)) {
            throw new FileDoesNotExist("File does not exist", 400);
        }
        if (!amazonS3.doesObjectExist(IMAGE_BUCKET, imageFileName)) {
            throw new FileDoesNotExist("File does not exist", 400);
        }
        String downloadUrlForFile = generateUrl(FILE_BUCKET, fileName, HttpMethod.GET).get();
        String downloadUrlForCoverImage = generateUrl(IMAGE_BUCKET, imageFileName, HttpMethod.GET).get();

        Map<String, String> map = new HashMap<>();
        map.put("file download url", downloadUrlForFile);
        map.put("cover image download url", downloadUrlForCoverImage);
        return map;
    }

    @Override
    public Map<String, Object> findAll(int numberOfPages, int numberOfItems) {
        Pageable pageable = PageRequest.of(numberOfPages, numberOfItems, Sort.by("title"));
        Page<Book> page = bookRepository.findAll(pageable);
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("totalNumberOfPages", page.getTotalPages());
        pageResult.put("totalNumberOfElementsInDatabase", page.getTotalElements());
        if (page.hasNext()) {
            pageResult.put("nextPage", page.nextPageable());
        }
        if (page.hasPrevious()) {
            pageResult.put("nextPage", page.previousPageable());
        }
        pageResult.put("books", page.getContent());
        pageResult.put("NumberOfElementsInPage", page.getNumberOfElements());
        pageResult.put("pageNumber", page.getNumber());
        pageResult.put("size", page.getSize());

        return pageResult;
    }

    @Override
    public List<BookDto> getAllBooksForUser(String email) {
        List<Book> list = bookRepository.findBookUploadedBy(email);
        List<BookDto> bookDtos = new ArrayList<>();
        for (Book book : list) {
            BookDto bookDto = modelMapper.map(book, BookDto.class);
            bookDtos.add(bookDto);
        }
        return bookDtos;
    }

    private CompletableFuture<String> generateUploadUrlForImage(String imageFileName) {
        log.info("Generating upload url for image");
        return generateUrl(IMAGE_BUCKET, imageFileName, HttpMethod.PUT);
    }

    private CompletableFuture<String> generateUploadUrlForFile(String fileName) {
        log.info("Generating upload url for file");
        return generateUrl(FILE_BUCKET, fileName, HttpMethod.PUT);
    }


}

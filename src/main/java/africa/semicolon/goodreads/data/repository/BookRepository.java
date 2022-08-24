package africa.semicolon.goodreads.data.repository;


import africa.semicolon.goodreads.data.models.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, Long> {
    Book findBookByTitleIgnoreCase(String title);

    List<Book> findBookUploadedBy(String email);
}

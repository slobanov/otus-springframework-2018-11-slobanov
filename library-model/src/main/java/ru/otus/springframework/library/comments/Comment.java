package ru.otus.springframework.library.comments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.otus.springframework.library.books.Book;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BOOK_ID")
    private @NonNull Book book;

    private @NonNull String text;

    @CreationTimestamp
    @Column(updatable = false, insertable = false)
    private Date created;

    @JsonIgnore
    public String getPrettyDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(created);
    }

    @JsonIgnore
    public Long getBookId() {
        return book.getId();
    }

}

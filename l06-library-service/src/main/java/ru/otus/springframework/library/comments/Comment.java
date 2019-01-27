package ru.otus.springframework.library.comments;

import lombok.*;

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
    private @NonNull Long bookId;
    private @NonNull String text;
    @Column(updatable = false, insertable = false)
    private Date created;

    public String getPrettyDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(created);
    }

}

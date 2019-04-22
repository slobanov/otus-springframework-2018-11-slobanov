package ru.otus.springframework.library.secutiry;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Document(collection = "user")
public class MongodbUser {

    @Id
    private ObjectId id;
    private String userName;
    private String password;
    private String roleString;

}
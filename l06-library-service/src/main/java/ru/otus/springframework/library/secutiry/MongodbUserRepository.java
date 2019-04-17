package ru.otus.springframework.library.secutiry;

import org.bson.types.ObjectId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Profile({"rest", "mvc"})
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
@Repository
interface MongodbUserRepository extends MongoRepository<MongodbUser, ObjectId> {
    Optional<MongodbUser> findByUserName(String username);
}

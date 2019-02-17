package ru.otus.springframework.library.dao.mongodb.mongock;

import com.github.cloudyrock.mongock.SpringBootMongock;
import com.github.cloudyrock.mongock.SpringBootMongockBuilder;
import com.mongodb.MongoClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import ru.otus.springframework.library.dao.mongodb.mongock.changelog.LibraryMongodbChangelog;

@Configuration
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
public class MongockConfig {

    @Bean
    public MySpringBootMongock mongock(
            ApplicationContext springContext,
            MongoClient mongoClient,
            @Value("${spring.data.mongodb.database}") String database
    ) {
        return new MySpringBootMongock(
                (SpringBootMongock) new SpringBootMongockBuilder(
                        mongoClient,
                        database,
                        LibraryMongodbChangelog.class.getPackageName()
                ).setApplicationContext(springContext)
                 .setLockQuickConfig()
                 .build());
    }


}

@RequiredArgsConstructor
class MySpringBootMongock implements ApplicationRunner, Ordered {

    private final SpringBootMongock springBootMongock;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @SneakyThrows
    @Override
    public void run(ApplicationArguments args) {
        springBootMongock.run(args);
    }
}

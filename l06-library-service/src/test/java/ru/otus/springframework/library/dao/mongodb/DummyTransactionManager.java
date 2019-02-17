package ru.otus.springframework.library.dao.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

@Profile("test-mongodb")
class DummyTransactionManager implements PlatformTransactionManager {

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) {
        return null;
    }

    @Override
    public void commit(TransactionStatus status) {

    }

    @Override
    public void rollback(TransactionStatus status) {

    }

}
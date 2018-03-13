package ru.stoloto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.TestEntity;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
@Transactional
public interface RepositoryForTest extends JpaRepository<TestEntity, Long>, QuerydslPredicateExecutor <TestEntity> {

    @Transactional(readOnly = true)
    Optional<TestEntity> findById(@Param("id")Long id );

    @Async
    @Transactional(readOnly = true)
    CompletableFuture<TestEntity> findOneById(@Param("id")Long id );

}

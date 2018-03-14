package ru.stoloto.repositories.mybatis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mybatis.TestEntity;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
@Transactional("transactionManager")
public interface RepositoryForTest extends JpaRepository<TestEntity, Long>{

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    Optional<TestEntity> findById(@Param("id")Long id );

    @Async
    @Transactional(readOnly = true)
    CompletableFuture<TestEntity> findOneById(@Param("id")Long id );

}

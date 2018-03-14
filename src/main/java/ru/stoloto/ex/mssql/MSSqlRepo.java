package ru.stoloto.repositories.mssql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.MSPerson;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
@Transactional
public interface MSSqlRepo extends JpaRepository<MSPerson, Long>{

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    Optional<MSPerson> findById(@Param("id") Long id);

    @Async
    @Transactional(readOnly = true)
    CompletableFuture<MSPerson> findOneById(@Param("id") Long id);

}

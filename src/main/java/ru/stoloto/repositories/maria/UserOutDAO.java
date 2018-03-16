package ru.stoloto.repositories.maria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mariadb.UserRebased;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
@Transactional("transactionManager")
public interface UserOutDAO extends JpaRepository<UserRebased, Integer>{

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    Optional<UserRebased> findById(@Param("id") Integer id);

    @Async
    @Transactional(readOnly = true)
    CompletableFuture<UserRebased> findOneById(@Param("id") Integer id);

}

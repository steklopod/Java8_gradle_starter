package ru.stoloto.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.User;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface MSSqlDAO extends JpaRepository<User, Long>{

//    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
//    Optional<User> findById(@Param("id") Long id);



}

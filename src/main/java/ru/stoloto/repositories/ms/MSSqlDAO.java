package ru.stoloto.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.Client;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface MSSqlDAO extends JpaRepository<Client, Long>{

//    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
//    Optional<Client> findById(@Param("id") Long id);



}

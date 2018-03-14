package ru.stoloto.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.MSPerson;

@Repository
@Transactional("atransactionManager")
public interface MSSqlRepo extends JpaRepository<MSPerson, Long>{

//    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
//    Optional<MSPerson> findById(@Param("id") Long id);



}

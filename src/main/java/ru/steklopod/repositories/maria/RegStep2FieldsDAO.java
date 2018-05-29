package ru.steklopod.repositories.maria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mariadb.RegStep2Fields;

import java.util.List;

@Repository
@Transactional("transactionManager")
public interface RegStep2FieldsDAO extends JpaRepository<RegStep2Fields, Integer> {

    String FIND_BY_ID = "SELECT * FROM rebased_stages WHERE customer_id = ?1";

    @Query(value = FIND_BY_ID, nativeQuery = true)
    List<RegStep2Fields> findByIdMy(@Param("customer_id") Integer id);
}

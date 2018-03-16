package ru.stoloto.repositories.maria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mariadb.UserUtmMarker;

@Repository
@Transactional("transactionManager")
public interface UserUTMMarkerDAO extends JpaRepository<UserUtmMarker, Integer>{


}

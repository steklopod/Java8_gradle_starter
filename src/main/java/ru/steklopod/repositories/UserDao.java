package ru.steklopod.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.User;

@Repository
@Transactional("transactionManager")
public interface UserDao extends JpaRepository<User, Integer> {

}

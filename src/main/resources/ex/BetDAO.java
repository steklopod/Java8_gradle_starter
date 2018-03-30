package ex;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.Bet;

import java.util.HashSet;

@Repository
@Transactional("transactionManager")
public interface BetDAO extends JpaRepository<Bet, Integer>{

    String ALL_IDS = "Select  Id from Bet";

    @Query(value = ALL_IDS, nativeQuery = true)
    HashSet<String> findAllIds();

    @Query("SELECT COUNT(u) FROM Bet u")
    Long selectCountOfBet();
}

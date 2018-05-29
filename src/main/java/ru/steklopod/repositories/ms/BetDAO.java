package ru.steklopod.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.steklopod.entities.mssql.Bet;

@Repository
public interface BetDAO extends JpaRepository<Bet, Integer> {

}

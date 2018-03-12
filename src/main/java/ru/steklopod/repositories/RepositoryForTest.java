package ru.steklopod.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.TestEntity;

import java.util.Optional;

@Repository
@Transactional
public interface RepositoryForTest extends JpaRepository<TestEntity, Long>{

    public Optional<TestEntity> findById(@Param("id")Long id );
}

package ru.steklopod.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "maria_init_db")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MariaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp today;

    public MariaEntity(String name) {
        this.name = name;
    }
}

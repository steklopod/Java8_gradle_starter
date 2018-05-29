package ru.steklopod.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "registrationstageupdatedate")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RegistrationSteps {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer clientId;

    @Column(name = "udate")
    private Timestamp passDate;

    @Column(name = "stage_id", columnDefinition = "TINYINT(4)")
    private Integer registrationStageId;
}

package ru.stoloto.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "registrtaion_steps")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegistrtionSteps {

    @Id
    private Integer id;

    @Column(name = "bc_step")
    private Byte bcStep;

    @Column(name = "pass_date")
    private Timestamp passDate;

    @Column(name = "reg_stage_id")
    private Byte regStageId;

    @Column(name = "email_confirmed")
    private Byte emailConfirmed;

    @Column(name = "phone_confirmed")
    private Byte phoneConfirmed;

    @Column(name = "registered_in_tsupis")
    private Byte registeredInTsupis;

    @Column(name = "identified_in_tsupis")
    private Byte identifiedInTsupis;

    @Column(name = "personality_confirmed")
    private Byte personalityConfirmed;
}

package ru.steklopod.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rebased_stages")
public class RegStep2Fields {

    @Id
    @Column(name = "customer_id")
    private Integer clientId;

    @Column(name = "registration_stage_id")
    private Integer registrationStageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegStep2Fields that = (RegStep2Fields) o;
        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(registrationStageId, that.registrationStageId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(clientId, registrationStageId);
    }
}

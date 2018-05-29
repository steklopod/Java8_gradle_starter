package ru.steklopod.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor

public class RegStepListObjById {

    private Integer clientId;

    private List<Integer> registrationStageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegStepListObjById that = (RegStepListObjById) o;
        return Objects.equals(clientId, that.clientId) &&
                Objects.equals(registrationStageId, that.registrationStageId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(clientId, registrationStageId);
    }
}

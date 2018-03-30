package ru.stoloto.entities.mssql;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ClientVerificationStep")
public class ClientVerificationStep implements Serializable {

    @Id
    private Integer id;

    @Column(name = "ClientId")
    private Integer clientId;

    @Column(name = "PartnerKYCStepId")
    private Integer step;

    @Column(name = "PassDate")
    private Timestamp passDate;

    @Column(name = "State")
    private byte state;

}

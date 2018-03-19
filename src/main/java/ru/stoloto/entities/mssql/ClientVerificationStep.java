package ru.stoloto.entities.mssql;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientVerificationStep {

    @Id
//    @Column(name = "Id")
    private long id;

    private long clientId;

    private long partnerKycStepId;
    private Timestamp created;
    private Timestamp passDate;
    private Timestamp expireDate;
    private long state;
    private String error;
    private String code;
    private String responseData;
    private long source;
    private String info;


}

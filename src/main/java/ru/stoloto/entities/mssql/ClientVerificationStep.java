package ru.stoloto.entities.mssql;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dbo.ClientVerificationStep")
public class ClientVerificationStep  implements Serializable {

    @Id
    @Column(name = "Id", columnDefinition = "int")
    private Integer id;

    @Column(name = "ClientId")
    private int clientId;

    @Column(name = "PartnerKYCStepId")
    @Nullable
    private Integer partnerKycStepId;

    @Column(name = "Created")
    private Timestamp created;

    @Column(name = "PassDate")
    private Timestamp passDate;

//    @Column(name = "ExpireDate")
//    private Timestamp expireDate;
//
//    @Column(name = "State")
//    private int state;
//
//    @Column(name = "Error")
//    private String error;
//
//    @Column(name = "Code")
//    private String code;
//
//    @Column(name = "ResponseData")
//    private String responseData;
//
//    @Column(name = "Source")
//    private int source;
//
//    @Column(name = "Info")
//    private String info;


}

package ru.stoloto.entities.mssql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "dbo.Client")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client implements Serializable {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Email")
    @Nullable
    private String email;

    @Column(name = "Phone")
    @Nullable
    private String phone;

    @Column(name = "PasswordSalt")
    private Long passwordSalt;

    @Column(name = "PasswordHash")
    @Nullable
    private String passwordHash;

    @Column(name = "Login")
    private String login;

    @Column(name = "Islocked")
    @Nullable
    private boolean active;

    @Column(name = "Created")
    private Timestamp registrationDate;

    @Column(name = "Modified")
    private Timestamp lastModify;

    @Column(name = "Gender")
    @Nullable
    private Integer gender;

    @Column(name = "BirthDate")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "MiddleName")
    @Nullable
    private String patronymic;

    @Column(name = "LastName")
    private String surname;

    @Column(name = "IsResident")
    @Nullable
    private boolean citizen;

    @Column(name = "Address")
    @Nullable
    private String addressString;

    @Column(name = "BirthCity")
    @Nullable
    private String birthPlace;

    @Column(name = "RegionId")
    @Nullable
    private Integer region;

    @Column(name = "City")
    @Nullable
    private String city;

    @Column(name = "DocNumber")
    @Nullable
    private String passport;

    @Column(name = "DocIssuedBy")
    @Nullable
    private String passportIssuer;

    @Column(name = "DocIssueCode")
    @Nullable
    private String passportIssuerCode;

    /**
     * Статус пользовательских данныхЖ
     * 1 - не вводились;
     * 2 - введены пользователем;
     * 3 - проверены по данным из ЦУПИС или оператором.
     * <p>
     * >>> В выгрузке одно значение = 1, остальные  =  0  ??? <<<
     */
    @Column(name = "IsSubscribedToNewsletter")
    @Nullable
    private boolean isSubscribedToNewsletter;

    @Column(name = "RegistrationSource")
    @Nullable
    private Integer registrationSource;

    @Column(name = "NotificationOptions")
    @Nullable
    private Integer notificationOptions;


}

package ru.stoloto.entities.mssql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "dbo.Client")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client implements Serializable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id; // customer_id,

    @Column(name = "Email")
    @Nullable
    private String email;

    @Column(name = "Phone")
    @Nullable
    private String phone;

//    TODO - проверить наличие
//    @Column(nullable = false)
//    private String password;

    @Column(name = "Login")
    private String login;

    //    TODO - перепроверить название и наличие
    @Column(name = "Islocked")
    @Nullable
    private boolean active;

    //    TODO - перепроверить тип (возможен long)
    @Column(name = "Created")
    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    //    TODO - перепроверить тип (возможен long)
    @Column(name = "Modified")
    @Temporal(TemporalType.DATE)
    private Date lastModify;

    /**
     * ПОЛ пользователя,  указанный на этапе регистрации:
     * 1 - NOT_SPECIFIED;
     * 2 - MALE;
     * 3 - FEMALE.
     * >>> 0 - не определен; "1- Мужской; 2 - женский.
     */
    @Column(name = "Gender")
    @Nullable
    private Integer gender;

    //    TODO - перепроверить тип (возможен long)
    @Column(name = "BirthDate")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "MiddleName")
    @Nullable
    private String patronymic; //Отчество пользователя

    @Column(name = "LastName")
    private String surname;

    //TODO - Если проставлен признак "1" > проставлять 'RUS'
    @Column(name = "IsResident")
    @Nullable
    private Integer citizenship;


    /**
     * ИПОЛЬЗУЕТСЯ ПАРСИНГ:
     * if (address != null) {
     * String[] addressArr = address.split("";"", 6);
     * if (addressArr.length == 6) {
     * user.setStreet(addressArr[0]);
     * user.setHouseNumber(addressArr[2]);
     * user.setBuilding(addressArr[3]);
     * user.setHousing(addressArr[1]);
     * user.setApartment(addressArr[4]);
     * }
     * }
     */
    //    TODO - распарсить
    @Column(name = "Address")
    private String addressString;

    @Column(name = "BirthCity")
    private String birthPlace; //есть только город рождения

    @Column(name = "RegionId")
    private Integer region;

    @Column(name = "City")
    private String city;

    /**
     * a) Если документ - паспорт РФ,
     * то " серия паспорта + номер паспорта + 'passportRus' "
     * б) Если загран паспорт
     * то прописывается просто номер
     */

//    TODO - распарсить
    @Column(name = "DocNumber")
    private String passport;

    @Column(name = "DocIssuedBy")
    private String passportIssuer;

    @Column(name = "DocIssueCode")
    private String passportIssuerCode;

    /**
     * Статус пользовательских данныхЖ
     * 1 - не вводились;
     * 2 - введены пользователем;
     * 3 - проверены по данным из ЦУПИС или оператором.
     *
     *    >>> В выгрузке одно значение = 1, остальные  =  0  ??? <<<
     */
//    TODO - не заполняется
    @Column(name = "IsSubscribedToNewsletter")
    private byte personalDataState;

    @Column(name = "RegistrationSource")
    private Integer registrationSource;// В выгрузке все значения =  42

    @Column(name = "NotificationOptions")
    private Integer notificationOptions; // В выгрузке все значения =  0 / 2 / 3 / NULL


}

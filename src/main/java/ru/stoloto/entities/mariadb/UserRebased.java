package ru.stoloto.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRebased implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    protected Integer version;

    @Column (unique=true)
    private String email;

    private String phone;

    @Column(nullable = false, columnDefinition = "VARCHAR(128)") //@NonNull
    private String password;

    @Column(name = "registration_stage_id",columnDefinition = "TINYINT(4)")
    @NonNull
    private Integer registrationStageId;

    @Nullable
    private String login;

//    @Column(columnDefinition = "TINYINT(1) default '0'")
    private Boolean blocked;

    @Column(name = "registration_date", columnDefinition = "datetime")
    @Nullable
    private Timestamp registrationDate;


    @Column(name = "last_modify")
    @Nullable
    private Timestamp lastModify;

    @Column(name = "gender_id", columnDefinition = "TINYINT(4)")
    @Nullable
    private Integer gender;

    @Column(name = "new_email")
    @Nullable
    private String newEmail;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    @Nullable
    private Date birthDate;

    @Column(name = "first_name")
    @Nullable
    private String firstName;

    @Nullable
    private String patronymic;

    @Nullable
    private String surname;

    @Nullable
    private String citizenship;

    @Column(name = "address_string", columnDefinition = "TEXT")
    @Nullable
    private String addressString;

    @Column(name = "kladr_code", columnDefinition = "VARCHAR(30)")
    @Nullable
    private String kladrCode;

    @Column(name = "birth_place")
    @Nullable
    private String birthPlace; //есть только город рождения

    @Nullable
    private String region;

    @Nullable
    private String city;

    @Nullable
    private String street;

    @Column(name = "house_number")
    @Nullable
    private String houseNumber;

    @Nullable
    private String building;

    @Nullable
    private String housing;

    @Nullable
    private String apartment;

    @Column(name = "skype_login")
    @Nullable
    private String skypeLogin;

    @Column(name = "passport_series", columnDefinition = "VARCHAR(10)")
    @Nullable
    private String passportSeries;

    @Column(name = "passport_number" /*, columnDefinition = "VARCHAR(10)"*/)
    @Nullable
    private String passportNumber;

    @Column(name = "passport_issuer", columnDefinition = "VARCHAR(1000)")
    @Nullable
    private String passportIssuer;

    @Column(name = "passport_issuer_code", columnDefinition = "VARCHAR(50)")
    @Nullable
    private String passportIssuerCode;

    @Column(name = "passport_date", columnDefinition = "VARCHAR(50)")
    @Temporal(TemporalType.DATE)
    @Nullable
    private Date passportDate;

    @Column(name = "customer_id", columnDefinition = "BIGINT(20)")
    @Nullable
    private Long customerId;

    @Column(name = "swarm_user_id", columnDefinition = "BIGINT(20)")
    @Nullable
    private Long swarmUserId;

    @Column(name = "email_confirmed", nullable = false)
    @NonNull
    private boolean emailConfirmed;

    @Column(name = "phone_confirmed", nullable = false)
    @NonNull
    private boolean phoneConfirmed;

    @Column(name = "registered_in_tsupis", nullable = false)
    @NonNull
    private boolean registeredInTsupis;

    @Column(name = "identified_in_tsupis", nullable = false)
    @NonNull
    private boolean identifiedInTsupis;

    @Column(name = "personality_confirmed", nullable = false)
    @NonNull
    private boolean personalityConfirmed;

    @Column(name = "locale_id")
    @NonNull
    private int localeID;

    @Column(name = "public_person_id", columnDefinition = "TINYINT(4)")
    @Nullable
    private Byte publicPersonId;

    @Column(name = "document_type_id", columnDefinition = "TINYINT(4)")
    @Nullable
    private Integer documentTypeId;

    @Column(name = "blacklist_check", columnDefinition = "TINYINT(4)", nullable = false)
    @NonNull
    private byte blacklistCheck;

    @Column(name = "country_id", columnDefinition = "INT(11)")
    @Nullable
    private Integer countryId;

    /**
     * Статус пользователя в ЦУПИС:
     * 1 - FULL;
     * 2 - ALTERNATIVE;
     * 3 - UNKNOWN.
     */
    @Column(name = "tsupis_status", columnDefinition = "TINYINT(4)")
    @Nullable
    private Byte tsupisStatus;

    /**
     * Статус идентификации в ЦУПИС:
     * 0 - FULL;
     * 1 - LIMITED.
     */
    @Column(name = "identState")
    @Nullable
    private Byte identState;

    /**
     * Тип подтверждения ПД:
     * 1 - столото;
     * 2 - евросеть;
     * 3 - skype;
     * 4 - contact;
     */
    @Column(name = "identType",columnDefinition = "TINYINT(4)")
    @Nullable
    private Byte identType;

    @Column(name = "snils_number", columnDefinition = "VARCHAR(20)")
    @Nullable
    private String snilsNumber;

    @Column(name = "inn_number", columnDefinition = "VARCHAR(12)")
    @Nullable
    private String innNumber;

    /**
     * Статус аккаунта в ЦУПИС:
     * 0 - активен;
     * 1 - заблокирован;
     * 2 - удалён;
     * 3 - ошибка;
     * 4 - отвязан от ЦУПИС.
     */
    @Column(name = "tsupis_account_status", columnDefinition = "TINYINT(4)")
    @Nullable
    private Byte tsupisAccountStatus;

    @Column(name = "tsupiserror_id", columnDefinition = "INT(11)")
    @Nullable
    private Integer tsupisError;

    @Column(name = "click_for_full")
    @Nullable
    private Boolean clickForFull;

    /**
     * Статус оферты, принята ли пользователем оферта:
     * 0 - оферта не подтверждена;
     * 1 - оферта подтверждена, ПДн не подтверждены;
     * 2 - оферта и ПДн подтверждены.
     */
    @Column(name = "offer_state", columnDefinition = "TINYINT(4)", nullable = false)
    @NonNull
    private byte offerState;

    /**
     * Статус пользовательских данных:
     * 1 - не вводились;
     * 2 - введены пользователем;
     * 3 - проверены по данным из ЦУПИС или оператором.
     */
    @Column(name = "personal_data_state", columnDefinition = "TINYINT(4)")
    @Nullable
    private Byte personalDataState;

    @Column(name = "registration_source")
    private Integer registrationSource;

    @Column(name = "notify_email")
    @Nullable
    private Boolean notifyEmail;

    @Column(name = "notify_phone")
    @Nullable
    private Boolean notifyPhone;

    @Column(name = "migration_state")
    @Nullable
    private Byte migrationState ;


    public UserRebased(String street, String houseNumber, String building, String housing, String apartment) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.building = building;
        this.housing = housing;
        this.apartment = apartment;
    }

}

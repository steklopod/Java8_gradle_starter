package ru.stoloto.entities.mariadb;

import lombok.*;
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

    public UserRebased(String street, String houseNumber, String building, String housing, String apartment) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.building = building;
        this.housing = housing;
        this.apartment = apartment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    protected Integer version = 1;

    @Column //(nullable = false)@NonNull
    @Nullable
    private String email;

    @Column //(nullable = false)@NonNull
    @Nullable
    private String phone;

    @Column(nullable = false, columnDefinition = "VARCHAR(128)") //@NonNull
    private String password;

//    TODO - OneToOne
//    @Column(name = "registration_stage_id", nullable = false, columnDefinition = "TINYINT(4)")
//    @NonNull
//    private Integer registrationStageId; //"4 - ожидание ЦУПИС, 10 - проверен"

    @Nullable
    private String login;

    @Column
    @NonNull
    private boolean active; // TODO- tinyint(1)

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
    private String patronymic; //Отчество пользователя

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
//    TODO - проставляется на основании выгрузки из ЦУПИС
    private boolean registeredInTsupis;

    @Column(name = "identified_in_tsupis", nullable = false)
    @NonNull
    private boolean identifiedInTsupis;

    //    TODO - заполняется расчетно, исходя из шага регистрации
    @Column(name = "personality_confirmed", nullable = false)
    @NonNull
    private boolean personalityConfirmed;

    //    TODO - проставляется всем по умолчанию русский 638 ??? https://mvf.klerk.ru/spr/spr63.htm
    @Column(name = "locale_id", columnDefinition = "INT(11)")
    @NonNull
    private int localeID;

    @Column(name = "public_person_id", columnDefinition = "TINYINT(4)")
    @Nullable
    private Byte publicPersonId;

    //  TODO - анализировать поле номер документа
    @Column(name = "document_type_id", columnDefinition = "TINYINT(4)")
    @Nullable
    private Integer documentTypeId;

    @Column(name = "blacklist_check", columnDefinition = "TINYINT(4)", nullable = false)
    @NonNull
    private byte blacklistCheck;

    //    TODO - Предлагаем для всех резидентво проставить RUS = ???
    @Column(name = "country_id", columnDefinition = "INT(11)")
    @Nullable
    private Integer countryId;

    /**
     * Статус пользователя в ЦУПИС:
     * 1 - FULL;
     * 2 - ALTERNATIVE;
     * 3 - UNKNOWN.
     */
//    TODO - из выгрузки ЦУПИС
    @Column(name = "tsupis_status", columnDefinition = "TINYINT(4)")
    @Nullable
    private Byte tsupisStatus;

    /**
     * Статус идентификации в ЦУПИС:
     * 0 - FULL;
     * 1 - LIMITED.
     */
    //    TODO - из выгрузки ЦУПИС
    @Column(name = "ident_state", columnDefinition = "TINYINT(4)")
    @Nullable
    private Byte identState;

    /**
     * Тип подтверждения ПД:
     * 1 - столото;
     * 2 - евросеть;
     * 3 - skype;
     * 4 - contact;
     */
    //    TODO - из выгрузки ЦУПИС
    @Column(columnDefinition = "TINYINT(4)")
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
    //    TODO - из выгрузки ЦУПИС
    @Column(name = "tsupis_account_status", columnDefinition = "TINYINT(4)")
    @Nullable
    private Byte tsupisAccountStatus;

    @Column(name = "tsupiserror_id", columnDefinition = "INT(11)")
    @Nullable
    private Integer tsupisError;

    @Column(name = "click_for_full", columnDefinition = "TINYINT(1)")
    @Nullable
    private Byte clickForFull;

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

    @Column(name = "is_subscribed_to_newsletter")
    @Nullable
    private boolean isSubscribedToNewsletter;

    @Column(name = "registration_source")
    private Integer registrationSource;

    @Column(name = "notification_options")
    @Nullable
    private Integer notificationOptions;

    /**
     * При загрузке клиентов, не завершивших регистрацию в ЦУПИС - очищаются перснональные данные.
     * В этом случае необходимо здесь сохранить информацию о том, что эти данные были очищены.
     */
//    TODO
    @Column(name = "is_finished_registration")
    private boolean isFinishedRegistration;


}

package ru.stoloto.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRebased implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    protected Integer version;

    @Column
//            (nullable = false)
//    @NonNull
    private String email;

    @Column(nullable = false)
    @NonNull
    private String phone;

    //        MD5, нет технической возможности перенести пароль
    @Column(nullable = false, columnDefinition = "VARCHAR(128)")
    @NonNull
//    TODO - перепроверить
    private String password;

    @Column(name = "registration_stage_id", nullable = false, columnDefinition = "TINYINT(4)")
//    @NonNull
    private int registrationStageId; //"4 - ожидание ЦУПИС, 10 - проверен"

    @Nullable
    private String login;

    // Оставляем пустым ?
    // * - Отсутствует в исходной
//    TODO - возможно замена на bit(1)
    @Column(columnDefinition = "TINYINT(1)")
//    @NonNull
    private boolean active;

    @Column(name = "registration_date")
    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Calendar registrationDate;


    //    TODO - уточнить название
//    На сайте необходимо новое поле
    @Column(name = "last_modify")
    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Calendar lastModify;

    /**
     * ПОЛ пользователя,  указанный на этапе регистрации:
     * 1 - NOT_SPECIFIED;
     * 2 - MALE;
     * 3 - FEMALE.
     * >>> 0 - не определен; "1- Мужской; 2 - женский.
     */
    //    @Convert(converter = GenderJpaEnumConverter.class)
    @Column(name = "gender_id", columnDefinition = "TINYINT(4)")
    @Nullable
    private int gender;

    //  Отсутств.
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

    //TODO - Если проставлен признак "1" > проставлять 'RUS'
    @Nullable
    private String citizenship;

    @Column(name = "address_string", columnDefinition = "TEXT")
    @Nullable
    private String addressString;



    @Column(name = "kladr_code", columnDefinition = "VARCHAR(30)")
    private String kladrCode;

    @Column(name = "birth_place")
    private String birthPlace; //есть только город рождения

    private String region;

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
//  >>> НАЧАЛО ПАРСИНГА ---------------------
    @Column(columnDefinition = "VARCHAR(255)")
    private String city;

    @Column(columnDefinition = "VARCHAR(255)")
    private String street;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(columnDefinition = "VARCHAR(255)")
    private String building; /* строение */

    @Column(columnDefinition = "VARCHAR(255)")
    private String housing; /* корпус */

    @Column(columnDefinition = "VARCHAR(255)")
    private String apartment; /*квартира*/
//  <<< КОНЕЦ ПАРСИНГА ---------------------
//
//    @Column(name = "skype_login")
//    private String skypeLogin;
//
//    /**
//     * a) Если документ - паспорт РФ,
//     * то " серия паспорта + номер паспорта + 'passportRus' "
//     * б) Если загран паспорт
//     * то прописывается просто номер
//     */
//    @Column(name = "passport_series", columnDefinition = "VARCHAR(10)")
//    private String passportSeries;
//    @Column(name = "passport_number", columnDefinition = "VARCHAR(10)")
//    private String passportNumber;
//
//
//    @Column(name = "passport_issuer", columnDefinition = "VARCHAR(1000)")
//    private String passportIssuer;
//
//    @Column(name = "passport_issuer_code", columnDefinition = "VARCHAR(50)")
//    private String passportIssuerCode;
//
//    @Column(name = "passport_date", columnDefinition = "VARCHAR(50)")
//    @Temporal(TemporalType.DATE)
//    private Date passportDate;
//
//    @Column(name = "customer_id", columnDefinition = "BIGINT(20)")
//    private Long customerId;
//
//    @Column(name = "swarm_user_id", columnDefinition = "BIGINT(20)")
//    private Long swarmUserId;
//
//    @Column(name = "email_confirmed")
//    private boolean emailConfirmed = false;
//
//    @Column(name = "phone_confirmed", nullable = false)
//    private boolean phoneConfirmed = false;
//
//    @Column(name = "registered_in_tsupis", nullable = false)
//    private boolean registeredInTsupis = false; //проставляется на основании выгрузки из ЦУПИС
//
//    @Column(name = "identified_in_tsupis", nullable = false)
//    private boolean identifiedInTsupis = false;
//
//    //    TODO - заполняется расчетно, исходя из шага регистрации
//    @Column(name = "personality_confirmed", nullable = false)
//    private boolean personalityConfirmed = false;
//
////    TODO - проставляется всем по умолчанию русский
//    @Column(name = "locale_id", columnDefinition = "INT(11)")
//    private int localeID;
//
//    @Column(name = "public_person_id", columnDefinition = "TINYINT(4)")
//    private byte publicPersonId;
//
//    //    TODO - анализировать поле номер документа
//    @Column(name = "document_type_id", columnDefinition = "TINYINT(4)")
//    private byte documentTypeId;
//
//    // * - Отсутствует в исходной
//    @Column(name = "blacklist_check", columnDefinition = "TINYINT(4)", nullable = false)
//    private byte blacklistCheck;
//
//    // * - Отсутствует в исходной
////    TODO - Предлагаем для всех резидентво проставить RUS
//    @Column(name = "country_id", columnDefinition = "INT(11)")
//    private byte countryId;
//
//
//// * - Отсутствует в исходной (проставляется на основании выгрузки из ЦУПИС)
//    /**
//     * Статус пользователя в ЦУПИС:
//     * 1 - FULL;
//     * 2 - ALTERNATIVE;
//     * 3 - UNKNOWN.
//     */
////    @Convert(converter = UserTsupisStatusJpaEnumConverter.class)
//    @Column(name = "tsupis_status", columnDefinition = "TINYINT(4)")
//    private byte tsupisStatus;
////    private UserTsupisStatus tsupisStatus;
//
//
//// * - Отсутствует в исходной (проставляется на основании выгрузки из ЦУПИС)
//    /**
//     * Статус идентификации в ЦУПИС:
//     * 0 - FULL;
//     * 1 - LIMITED.
//     */
////  @Convert(converter = UserIdentStateJpaEnumConverter.class)
//    @Column(name = "ident_state", columnDefinition = "TINYINT(4)")
//    private byte identState;
////     private UserIdentState identState;
//
//
//// * - Отсутствует в исходной (проставляется на основании выгрузки из ЦУПИС)
//    /**
//     * Тип подтверждения ПД:
//     * 1 - столото;
//     * 2 - евросеть;
//     * 3 - skype;
//     * 4 - contact;
//     */
////    @Convert(converter = UserIdentTypeJpaEnumConverter.class)
//    @Column(columnDefinition = "TINYINT(4)")
//    private byte identType;
////    private UserIdentType identType;
//
//    // * - Отсутствует в исходной (необяз.)
//
//    @Column(name = "snils_number", columnDefinition = "VARCHAR(20)")
//    private String snilsNumber;
//
//    // * - Отсутствует в исходной (необяз.)
//    @Column(name = "inn_number", columnDefinition = "VARCHAR(12)")
//    private String innNumber;
//
//    // * - Отсутствует в исходной (проставляется на основании выгрузки из ЦУПИС)
//    /**
//     * Статус аккаунта в ЦУПИС:
//     * 0 - активен;
//     * 1 - заблокирован;
//     * 2 - удалён;
//     * 3 - ошибка;
//     * 4 - отвязан от ЦУПИС.
//     */
////    @Convert(converter = TsupisAccountStatusJpaEnumConverter.class)
//    @Column(name = "tsupis_account_status", columnDefinition = "TINYINT(4)")
//    private byte tsupisAccountStatus;
////    private TsupisUserAccountStatus tsupisAccountStatus;
//
//
//    // * - Отсутствует в исходной
////    @ManyToOne
////    @JoinColumn(name = "tsupiserror_id")
//    @Column(name = "tsupiserror_id", columnDefinition = "INT(11)")
//    private int tsupisError;
////    private TsupisError tsupisError;
//
//
//    // * - Отсутствует в исходной
//    @Column(name = "click_for_full", columnDefinition = "TINYINT(1)")
//    private byte clickForFull;
//
//// * - Отсутствует в исходной
//    /**
//     * Статус оферты, принята ли пользователем оферта:
//     * 0 - оферта не подтверждена;
//     * 1 - оферта подтверждена, ПДн не подтверждены;
//     * 2 - оферта и ПДн подтверждены.
//     */
////    TODO - Автоматически проставлять всем значение 0
//    @Column(name = "offer_state", columnDefinition = "TINYINT(4)", nullable = false)
//    private byte offerState;
//
//    /**
//     * Статус пользовательских данныхЖ
//     * 1 - не вводились;
//     * 2 - введены пользователем;
//     * 3 - проверены по данным из ЦУПИС или оператором.
//     */
////    TODO - не заполняется
//    @Column(name = "personal_data_state", columnDefinition = "TINYINT(4)")
//    private byte personalDataState;
//
//
//    @Column(name = "registration_source")
//    private Integer registrationSource;
//
//    @Column(name = "notification_options")
//    private Integer notificationOptions;
//
//    /**
//     * При загрузке клиентов, не завершивших регистрацию в ЦУПИС - очищаются перснональные данные.
//     * В этом случае необходимо здесь сохранить информацию о том, что эти данные были очищены.
//     */
//    @Column(name = "is_finished_registration")
//    private boolean isFinishedRegistration;


}

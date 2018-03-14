package ru.stoloto.entities.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRebased implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    protected int version = 1;

//    @Column(nullable = false)
    private String email;

//    @Column(nullable = false)
    private String phone;

//    MD5, нет технической возможности перенести пароль
    //@Column(nullable = false)
    private String password;

    @Column(name = "registration_stage_id", nullable = false)
    private byte registrationStageId;

    private String login;

    //Оставляем пустым
    //@Column(nullable = false)
    private byte active;

    @Column(name = "registration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate = new Date();


//    TODO - уточнить название
//    На сайте необходимо новое поле
    @Column(name = "last_modify")
    private Date lastModify = new Date();

//    @Convert(converter = GenderJpaEnumConverter.class)
//    @Column(name = "gender_id", columnDefinition = "TINYINT")
//    private Gender gender = Gender.NOT_SPECIFIED;


    public UserRebased(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}

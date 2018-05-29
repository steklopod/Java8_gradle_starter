package ru.steklopod.entities.mariadb;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "useremailconfirmationcode")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEmailConfirm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer clientId;

    private String code;

    @Nullable
    @Column(name = "creation_date")
    private Timestamp creationDate;

    private Boolean checked = false;

    @Column(name = "email_confirm_date")
    private Timestamp emailConfirmDate;

    public UserEmailConfirm(Integer clientId) {
        this.clientId = clientId;
    }
}

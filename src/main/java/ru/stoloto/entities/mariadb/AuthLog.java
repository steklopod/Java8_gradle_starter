package ru.stoloto.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "authlog")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthLog {
// * - Отсутствует в исходной
//    TODO - не заполняется

    @Id
    private Integer id;

    @Column(name = "date_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateTime;

    @Column(name = "user_IP", columnDefinition = "VARCHAR(23)")
    private String userIP;

    @Column(columnDefinition = "TEXT")
    private String headers;

    @Column(name = "is_success", columnDefinition = "TINYINT(1)")
//    TODO - возможно замена на bit(1)
    private boolean isSuccess;

    @Column(name = "user_id", columnDefinition = "INT(11)", nullable = false)
    private String userID; // внутренний id пользователя

    @Column(name = "customer_id", columnDefinition = "INT(11)", nullable = false)
    private long customerId; // внешний (bc) id пользователя

    @Column(name = "fio", columnDefinition = "VARCHAR(2000)", nullable = false)
    private long fio;
}

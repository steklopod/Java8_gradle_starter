package ru.stoloto.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "authlog")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthLog {

    @Id
    private Integer id;

    @Column(name = "date_time", nullable = false)
    @NonNull
    private Timestamp dateTime;

    @Column(name = "user_IP", columnDefinition = "VARCHAR(23)")
    @Nullable
    private String userIP;

    @Column(columnDefinition = "TEXT")
    @Nullable
    private String headers;

    @Column(name = "is_success", columnDefinition = "TINYINT(1)")
    @Nullable
    private boolean isSuccess;

    @Column(name = "user_id", columnDefinition = "INT(11)", nullable = false)
    @NonNull
    private String userID; // внутренний id пользователя

    @Column(name = "customer_id", columnDefinition = "INT(11)", nullable = false)
    @NonNull
    private long customerId; // внешний (bc) id пользователя

    @Column(name = "fio", columnDefinition = "VARCHAR(2000)", nullable = false)
    @NonNull
    private long fio;
}

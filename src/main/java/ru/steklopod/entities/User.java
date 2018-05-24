package ru.steklopod.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

import static ru.steklopod.entities.User.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {
    public static final String TABLE_NAME = "user_test";
    public static final String USER_ID_COLUMN_NAME = "user_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = USER_ID_COLUMN_NAME)
    private Integer id;

    private Timestamp burn;

    private String name;

    public User(String name) {
        this.name = name;
    }
}

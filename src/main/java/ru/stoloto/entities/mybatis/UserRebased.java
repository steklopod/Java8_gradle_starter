package ru.stoloto.entities.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "client")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRebased implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    protected int version = 1;

    private String email;
    private String phone;
    private String password;


    public UserRebased(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}

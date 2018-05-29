package ru.steklopod.entities.mssql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "Bet")
@AllArgsConstructor
@NoArgsConstructor
public class Bet {

    @Column(name = "ClientId")
    @Id
    private Integer id;
    @Column(name = "Source")
    private Integer source;
}

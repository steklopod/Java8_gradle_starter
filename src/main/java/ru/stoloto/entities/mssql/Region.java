package ru.stoloto.entities.mssql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "Region")
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    @Id
    @Column(name = "Id")
    private Integer id;

    private String alpha3Code;

    private String alpha2Code;


}

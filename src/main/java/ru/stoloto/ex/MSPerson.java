package ru.stoloto.entities.mssql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person_ms")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MSPerson implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private boolean isMan;

    public MSPerson(String name) {
        this.name = name;
    }

    public MSPerson(String name, boolean isMan) {
        this.name = name;
        this.isMan = isMan;
    }
}

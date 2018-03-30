package ex;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Bet")
@Data
public class Bet implements Serializable {

    @Id
    @Column(name = "Id")
    private Integer id;

}

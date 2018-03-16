package ru.stoloto.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_utm_marker")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUtmMarker {
    // * - Отсутствует в исходной
    //    TODO - не заполняется
    @Id
    @Column(name = "user_id", columnDefinition = "INT(20)")
    private Integer userID;

    // * - Отсутствует в исходной
    //    TODO - не заполняется
    @Column(name = "utm_markers", columnDefinition = "varchar(4000)")
    private String utmMarkers;
}

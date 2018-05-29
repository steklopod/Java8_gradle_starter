package ru.steklopod.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

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

    @Id
    @Column(name = "user_id", columnDefinition = "INT(20)")
    @NonNull
    private Integer userID;

    @Column(name = "utm_markers", columnDefinition = "varchar(4000)")
    @NonNull
    private String utmMarkers;
}

package ru.stoloto.entities.mariadb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Table(name = "user_with_exception")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserWithException {
//    @Id
//    @Column(name = "customer_id")
    private Integer id;

    @Builder.Default private Boolean isSameId = false;
    @Builder.Default private Boolean isTest = false;
    @Builder.Default private Boolean notNullCashDeskId = false;
    @Builder.Default private Boolean isPhoneExist = false;
    @Builder.Default private Boolean isEmailExist = false;
    @Builder.Default private Boolean isLoginExist = false;
    @Builder.Default private Boolean isCustomerIdInBetTable = false;
    @Builder.Default private Boolean isEmptyEmail = false;
    @Builder.Default private Boolean isEmptyPhone = false;

    public UserWithException(Integer id) {
        this.id = id;
    }
}

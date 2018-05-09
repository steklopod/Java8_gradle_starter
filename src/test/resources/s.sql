CREATE TABLE user_with_exception (
  customer_id       INTEGER NOT NULL,
  isEmailExist      BIT,
  isLoginExist      BIT,
  isPhoneExist      BIT,
  isSameId          BIT,
  isTest            BIT,
  notNullCashDeskId BIT,
  PRIMARY KEY (customer_id)
)
  ENGINE = MyISAM

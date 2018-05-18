CREATE TABLE user (
  id       INTEGER NOT NULL,
  isEmailExist      BIT,
  isLoginExist      BIT,
  PRIMARY KEY (id)
)
  ENGINE = MyISAM

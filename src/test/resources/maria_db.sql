DELETE FROM user
WHERE registration_stage_id IS NULL;


SELECT count(*)
FROM user
WHERE registration_stage_id IS NULL;


SELECT *
FROM user
WHERE registration_stage_id IS NULL;

SELECT registration_date
FROM user
WHERE user.customer_id = 13434090;

SELECT DISTINCT email
FROM user;

SELECT *
FROM user
WHERE email IN ('anastasia.zelenskaya@stoloto.ru');


ALTER TABLE user
  ADD COLUMN IF NOT EXISTS migration_state TINYINT (4);


ALTER TABLE user
  ADD COLUMN IF NOT EXISTS registration_source TINYINT (4);


SELECT
  Id,
  Name,
  FirstName,
  Phone
FROM user
WHERE phone IN ('23000', '79822609102');

SELECT DISTINCT count(email)
FROM user;

SELECT count(*)
FROM user
WHERE login IS NULL;

SELECT login
FROM user
WHERE login = 'Natasha15021@yandex.ru';





SELECT count(*) from registrationstageupdatedate_copy where udate is NOT NULL;

SELECT count(*) from registrationstageupdatedate;

SELECT Created from Client where Email = 'vladvasin86@mail.ru';


SELECT count(*)
FROM user_copy
WHERE phone in('71111111111, 79287013338, 79371946967');

SELECT count(*)
FROM user
WHERE migration_state = 2;

SELECT count(*)
FROM user
WHERE phone in('71111111111, 79287013338, 79371946967');

SELECT last_modify, phone, first_name
FROM user
WHERE phone IN ('79287013338');


SELECT phone, first_name, email, login
FROM user_copy
WHERE phone IN ('79287013338, 79371946967');





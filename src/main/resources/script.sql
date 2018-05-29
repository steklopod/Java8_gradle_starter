ALTER TABLE `user`
  ADD COLUMN IF NOT EXISTS `migration_state` TINYINT (4);
ALTER TABLE `user`
  ADD COLUMN IF NOT EXISTS reg_source TINYINT (4);
ALTER TABLE `user`
  ADD COLUMN IF NOT EXISTS last_modify DATETIME;
ALTER TABLE `user`
  ADD COLUMN IF NOT EXISTS email_confirmation_date DATETIME;
ALTER TABLE `user`
  ADD COLUMN IF NOT EXISTS notify_email BIT (1);
ALTER TABLE `user`
  ADD COLUMN IF NOT EXISTS notify_phone BIT (1);
ALTER TABLE `user`
  ADD COLUMN IF NOT EXISTS resident BIT (1);
ALTER TABLE `user`
  MODIFY passport_number VARCHAR(25);
ALTER TABLE `useremailconfirmationcode`
  ADD COLUMN IF NOT EXISTS `email_confirm_date` DATETIME;


SET GLOBAL max_connections = 150000;

CREATE TABLE IF NOT EXISTS rebased_stages
(
  customer_id           INT NOT NULL,
  registration_stage_id INT NULL
);
TRUNCATE TABLE rebased_stages;
TRUNCATE TABLE useremailconfirmationcode;

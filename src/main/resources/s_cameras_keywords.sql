CREATE TABLE s_cameras_keywords
(
  label VARCHAR(50),
  value VARCHAR(50),
  id    SERIAL NOT NULL
    CONSTRAINT s_cameras_keywords_id_pk
    PRIMARY KEY,
  icon  VARCHAR(20),
  stamp VARCHAR(30)
);

CREATE UNIQUE INDEX s_cameras_keywords_label_uindex
  ON s_cameras_keywords (label);

CREATE UNIQUE INDEX s_cameras_keywords_value_uindex
  ON s_cameras_keywords (value);

CREATE UNIQUE INDEX s_cameras_keywords_id_uindex
  ON s_cameras_keywords (id);

COMMENT ON TABLE s_cameras_keywords IS 'ключевые слова для поиска контроля лиц';


INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp) VALUES ('', '', 28081, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('stdpr-office-fl2-cam3', 'stdpr-office-fl2-cam3', 28082, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('без группы', 'без группы', 28083, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('stdpr-office-cam4', 'stdpr-office-cam4', 28084, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('stdpr-office-fl2-cam1', 'stdpr-office-fl2-cam1', 28085, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('входовая камера', 'входовая камера', 28086, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('г.Москва ул.Трифоновская д.2', 'г.Москва ул.Трифоновская д.2', 28087, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('Кр. Площадь', 'Кр. Площадь', 28088, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('камера в самом дульнем углу офиса', 'камера в самом дульнем углу офиса', 28089, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('Cam 0-1-копия', 'Cam 0-1-копия', 28090, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('CD3-5', 'CD3-5', 28091, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('stdpr-office-cam-@@@', 'stdpr-office-cam-@@@', 28092, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('ул. Ташкентская', 'ул. Ташкентская', 28093, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('угловая камера', 'угловая камера', 28094, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('DE3-8', 'DE3-8', 28095, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('Самаркандский б-р', 'Самаркандский б-р', 28096, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('камера около лестницы на втором этаже', 'камера около лестницы на втором этаже', 28097, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp) VALUES ('A2-2', 'A2-2', 28098, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp) VALUES ('A2-0', 'A2-0', 28099, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('окололестничная камера', 'окололестничная камера', 28100, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('Cam 2-2', 'Cam 2-2', 28101, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('Cam 2-0', 'Cam 2-0', 28102, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('Cam 0-1', 'Cam 0-1', 28103, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('ZEL-876', 'ZEL-876', 28104, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('stdpr-office-cam-@@@-копия', 'stdpr-office-cam-@@@-копия', 28105, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('Cam 3-8', 'Cam 3-8', 28106, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('Cam 3-5', 'Cam 3-5', 28107, NULL, NULL);
INSERT INTO face_control.s_cameras_keywords (label, value, id, icon, stamp)
VALUES ('ул. Тургенева', 'ул. Тургенева', 28108, NULL, NULL);
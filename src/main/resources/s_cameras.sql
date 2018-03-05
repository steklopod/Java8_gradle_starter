CREATE TABLE s_cameras
(
  camera       VARCHAR(50) NOT NULL
    CONSTRAINT s_cameras_pk
    PRIMARY KEY,
  name         VARCHAR(50),
  place_text   VARCHAR(100),
  latitude     NUMERIC,
  longitude    NUMERIC,
  note         VARCHAR(100),
  min_proc     NUMERIC(6, 2),
  territory_id NUMERIC
    CONSTRAINT s_cameras_fk_territory_id
    REFERENCES s_camera_territory,
  group_id     NUMERIC
    CONSTRAINT s_cameras_fk_group_id
    REFERENCES s_camera_group,
  azimut       NUMERIC(6, 2)
);

CREATE INDEX s_cameras_fk_territory_id
  ON s_cameras (territory_id);

CREATE INDEX s_cameras_fk_group_id
  ON s_cameras (group_id);


INSERT INTO face_control.s_cameras (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
VALUES ('CD3-5', 'Cam 3-5', 'ул. Ташкентская', 55.81814, 37.45188, '', 2.00, 3, 5, 111.25);
INSERT INTO face_control.s_cameras (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
VALUES ('DE3-8', 'Cam 3-8', 'Самаркандский б-р', 55.82047, 37.44677, 'коммент', 2.00, 3, 8, 160.05);
INSERT INTO face_control.s_cameras (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
VALUES ('ZEL-876', 'Cam 0-1', 'Кр. Площадь', 55.82011, 37.42017, NULL, 41.00, NULL, NULL, 87.62);
INSERT INTO face_control.s_cameras (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
VALUES ('A2-2', 'Cam 2-2', 'ул. Тургенева', 55.81208, 37.45776, NULL, 2.00, 2, 2, 100.45);
INSERT INTO face_control.s_cameras (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
VALUES
  ('stdpr-office-fl2-cam3', 'угловая камера', 'камера в самом дульнем углу офиса', 55.78804638811499, 37.61073004138847,
   NULL, 70.00, NULL, NULL, 178.50);
INSERT INTO face_control.s_cameras (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
VALUES ('stdpr-office-fl2-cam1', 'окололестничная камера', 'камера около лестницы на втором этаже', 55.78790161140799,
        37.61087219846627, NULL, 70.00, 10, NULL, 337.77);
INSERT INTO face_control.s_cameras (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
VALUES
  ('stdpr-office-cam4', 'входовая камера', 'г.Москва ул.Трифоновская д.2', 55.787844303812875, 37.610764910105665, NULL,
   70.00, 10, NULL, 155.42);
INSERT INTO face_control.s_cameras (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
VALUES ('stdpr-office-cam---', 'Cam 0-1-копия', NULL, 55.81816, 37.48506, '', 38.00, 1, NULL, 357.80);
INSERT INTO face_control.s_cameras (camera, name, place_text, latitude, longitude, note, min_proc, territory_id, group_id, azimut)
VALUES ('stdpr-office-cam', 'Cam 0-1', NULL, 55.81823, 37.48605, '', 38.00, 1, NULL, 90.91);
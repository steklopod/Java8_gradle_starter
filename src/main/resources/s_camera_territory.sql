CREATE TABLE s_camera_territory
(
  territory_id     NUMERIC DEFAULT nextval('face_control.camera_territory_id_seq' :: REGCLASS) NOT NULL
    CONSTRAINT s_camera_territory_pk
    PRIMARY KEY,
  territory_name   VARCHAR(512),
  territory_define VARCHAR(512)
);


INSERT INTO face_control.s_camera_territory (territory_id, territory_name, territory_define)
VALUES (4, '4. Четвертая', NULL);
INSERT INTO face_control.s_camera_territory (territory_id, territory_name, territory_define)
VALUES (3, '3. Третья', NULL);
INSERT INTO face_control.s_camera_territory (territory_id, territory_name, territory_define)
VALUES (8, 'Стадион "Спартак"', NULL);
INSERT INTO face_control.s_camera_territory (territory_id, territory_name, territory_define)
VALUES (2, '2. Вторая', 'Описание терр.');
INSERT INTO face_control.s_camera_territory (territory_id, territory_name, territory_define)
VALUES (7, '7. Территория', 'Define');
INSERT INTO face_control.s_camera_territory (territory_id, territory_name, territory_define)
VALUES (1, '1. Первая++@@', 'Первое описание');
INSERT INTO face_control.s_camera_territory (territory_id, territory_name, territory_define)
VALUES (10, 'офис', 'описание');
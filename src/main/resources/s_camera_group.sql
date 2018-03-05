CREATE TABLE s_camera_group
(
  group_id     NUMERIC DEFAULT nextval('face_control.camera_group_id_seq' :: REGCLASS) NOT NULL
    CONSTRAINT s_camera_group_pk
    PRIMARY KEY,
  group_name   VARCHAR(512),
  territory_id NUMERIC                                                                 NOT NULL
    CONSTRAINT s_camera_group_territory_id_fk
    REFERENCES s_camera_territory,
  group_define VARCHAR(512)
);

CREATE INDEX s_camera_group_fk_territory_id
  ON s_camera_group (territory_id);


INSERT INTO face_control.s_camera_group (group_id, group_name, territory_id, group_define) VALUES (6, '3.2', 3, NULL);
INSERT INTO face_control.s_camera_group (group_id, group_name, territory_id, group_define) VALUES (7, '3.3', 3, NULL);
INSERT INTO face_control.s_camera_group (group_id, group_name, territory_id, group_define) VALUES (1, '2.1', 2, NULL);
INSERT INTO face_control.s_camera_group (group_id, group_name, territory_id, group_define) VALUES (8, '3.4', 3, NULL);
INSERT INTO face_control.s_camera_group (group_id, group_name, territory_id, group_define)
VALUES (4, '1.1', 1, 'Define');
INSERT INTO face_control.s_camera_group (group_id, group_name, territory_id, group_define) VALUES (3, '1.2', 1, '');
INSERT INTO face_control.s_camera_group (group_id, group_name, territory_id, group_define) VALUES (5, '3.1', 3, '');
INSERT INTO face_control.s_camera_group (group_id, group_name, territory_id, group_define)
VALUES (2, '2.2', 2, 'Define 2-2');
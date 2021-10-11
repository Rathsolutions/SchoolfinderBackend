INSERT INTO criteria (id,criteria_name) VALUES (-1,'test');
INSERT INTO criteria (id,criteria_name) VALUES (-2,'test1');
INSERT INTO criteria (id,criteria_name) VALUES (-3,'test2');

INSERT INTO person (id,prename,lastname,email,phone_number) VALUES (-1,'karl','test','karl@test.de','0815');
INSERT INTO person (id,prename,lastname,email,phone_number) VALUES (-2,'karl2','test','karl2@test.de','0816');

INSERT INTO project (id, default_icon, project_name, scaling) VALUES (-1, '0x00', 'testproj1', 1.0);
INSERT INTO project (id, default_icon, project_name, scaling) VALUES (-2, '0xff', 'testproj2', 1.5);

INSERT INTO school (id, short_school_name, school_name, latitude, longitude, school_picture, alternative_picture_text,primary_project_id) VALUES (-1,'shortTestschool','testschool','1.111','2.222','image1', 'text1', -1);
INSERT INTO school (id, short_school_name, school_name, latitude, longitude, school_picture, alternative_picture_text,primary_project_id) VALUES (-2,'shortTestschool2','testschool2','2.222','1.111','image2', 'text2', -1);
INSERT INTO school (id, short_school_name, school_name, latitude, longitude, school_picture, alternative_picture_text,primary_project_id) VALUES (-3,'shortTestschool3','testschool3','3.333','4.444','image3', 'text3', -2);

INSERT INTO functionality (id, name) VALUES (-1, 'testfunc1');
INSERT INTO functionality (id, name) VALUES (-2, 'testfunc2');

INSERT INTO person_school_mapping (id,person_id, school_id, functionality_id) VALUES (0,(SELECT id FROM Person where prename='karl'),(SELECT id FROM School where school_name='testschool'), -1);
INSERT INTO person_school_mapping (id,person_id, school_id, functionality_id) VALUES (-1,(SELECT id FROM Person where prename='karl2'),(SELECT id FROM School where school_name='testschool2'), -2);
INSERT INTO person_school_mapping (id,person_id, school_id, functionality_id) VALUES (-2,(SELECT id FROM Person where prename='karl'),(SELECT id FROM School where school_name='testschool3'), -1);

INSERT INTO school_criteria_mapping (school_id, criteria_id) VALUES ((SELECT id FROM School where school_name='testschool'), (SELECT id FROM Criteria where criteria_name='test'));
INSERT INTO school_criteria_mapping (school_id, criteria_id) VALUES ((SELECT id FROM School where school_name='testschool2'), (SELECT id FROM Criteria where criteria_name='test1'));
INSERT INTO school_criteria_mapping (school_id, criteria_id) VALUES ((SELECT id FROM School where school_name='testschool3'), (SELECT id FROM Criteria where criteria_name='test'));
INSERT INTO school_criteria_mapping (school_id, criteria_id) VALUES ((SELECT id FROM School where school_name='testschool3'), (SELECT id FROM Criteria where criteria_name='test1'));

INSERT INTO area (id, area, area_institution_position, color, name) VALUES (-1, 'SRID=3857;POLYGON ((891032.2520110907 6143429.787979117, 955239.3557706389 6137926.321942585, 930779.5067193825 6177673.576650876, 891032.2520110907 6143429.787979117))', 'POINT (8.004278906249999 48.230358945402884)', '0x00', 'testarea1');
INSERT INTO area (id, area, area_institution_position, color, name) VALUES (-2, 'SRID=3857;POLYGON ((1012720.0010410914 6291411.8747392185, 1028007.4066981267 6262060.055877711, 1056136.2331070714 6287131.401155248, 1012720.0010410914 6291411.8747392185))', 'POINT (9.2402408203125 49.075859013135954)', '0xff', 'testarea2');

INSERT INTO school_project_mapping (school_id ,project_id) VALUES ((SELECT id FROM School where school_name='testschool'), (SELECT id FROM Project where project_name='testproj1'));
INSERT INTO school_project_mapping (school_id ,project_id) VALUES ((SELECT id FROM School where school_name='testschool2'), (SELECT id FROM Project where project_name='testproj1'));
INSERT INTO school_project_mapping (school_id ,project_id) VALUES ((SELECT id FROM School where school_name='testschool3'), (SELECT id FROM Project where project_name='testproj2'));

ALTER SEQUENCE area_id_seq increment by 3; 
ALTER SEQUENCE criteria_id_seq increment by 3; 
ALTER SEQUENCE functionality_id_seq increment by 3; 
ALTER SEQUENCE person_id_seq increment by 3; 
ALTER SEQUENCE person_school_mapping_id_seq increment by 3; 
ALTER SEQUENCE school_id_seq increment by 3; 


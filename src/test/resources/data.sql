INSERT INTO Criteria (id,criteria_name) VALUES (0,'test');
INSERT INTO Criteria (id,criteria_name) VALUES (1,'test1');
INSERT INTO Criteria (id,criteria_name) VALUES (2,'test2');
INSERT INTO Person (id,prename,lastname,email,phone_number) VALUES (0,'karl','test','karl@test.de','0815');
INSERT INTO Person (id,prename,lastname,email,phone_number) VALUES (1,'karl2','test','karl2@test.de','0816');
INSERT INTO School (id,school_name, latitude, longitude, color, school_picture) VALUES (0,'testschool','1.111','2.222','ff0000','0x00');
INSERT INTO School (id,school_name, latitude, longitude, color, school_picture) VALUES (1,'testschool2','2.222','1.111','ff0000','0xFF');
INSERT INTO School (id,school_name, latitude, longitude, color, school_picture) VALUES (2,'testschool3','3.333','4.444','ff0000','0xAF');
INSERT INTO person_school_mapping (id,person_id, school_id, functionality) VALUES (0,(SELECT id FROM Person where prename='karl'),(SELECT id FROM School where school_name='testschool'), 0);
INSERT INTO person_school_mapping (id,person_id, school_id, functionality) VALUES (1,(SELECT id FROM Person where prename='karl2'),(SELECT id FROM School where school_name='testschool2'), 1);
INSERT INTO person_school_mapping (id,person_id, school_id, functionality) VALUES (2,(SELECT id FROM Person where prename='karl'),(SELECT id FROM School where school_name='testschool3'), 0);
INSERT INTO school_criteria_mapping (school_id, criteria_id) VALUES ((SELECT id FROM School where school_name='testschool'), (SELECT id FROM Criteria where criteria_name='test'));
INSERT INTO school_criteria_mapping (school_id, criteria_id) VALUES ((SELECT id FROM School where school_name='testschool2'), (SELECT id FROM Criteria where criteria_name='test1'));
INSERT INTO school_criteria_mapping (school_id, criteria_id) VALUES ((SELECT id FROM School where school_name='testschool3'), (SELECT id FROM Criteria where criteria_name='test'));
INSERT INTO school_criteria_mapping (school_id, criteria_id) VALUES ((SELECT id FROM School where school_name='testschool3'), (SELECT id FROM Criteria where criteria_name='test1'));

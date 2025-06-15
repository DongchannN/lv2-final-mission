INSERT INTO gym(location, name, phone_number)
VALUES ('location1', 'name1', '01099999999'),
       ('location2', 'name2', '01099991111'),
       ('location33', 'name3', '01099992222');

INSERT INTO trainer(credit_price, gym_id, description, image_url, name)
VALUES (10, 1, 'description1', 'url1', 'name1'),
       (20, 1, 'description2', 'url2', 'name2'),
       (10, 2, 'description3', 'url3', 'name3'),
       (20, 2, 'description4', 'url4', 'name4'),
       (10, 3, 'description5', 'url5', 'name5');

INSERT INTO trainer_schedule(time, trainer_id)
VALUES ('13:00', 1),
       ('14:00', 1),
       ('15:00', 1),
       ('16:00', 1),
       ('13:00', 2),
       ('14:00', 2),
       ('15:00', 2),
       ('16:00', 2),
       ('13:00', 3),
       ('14:00', 3),
       ('15:00', 3),
       ('16:00', 3),
       ('13:00', 4),
       ('14:00', 4),
       ('15:00', 4),
       ('16:00', 4),
       ('13:00', 5),
       ('14:00', 5),
       ('15:00', 5),
       ('16:00', 5);

INSERT INTO member(credit_amount, name, nickname, password, phone_number, gym_id)
VALUES (1000, 'channie1', 'chan1', '1234', '01012341234', 1),
       (1000, 'channie2', 'chan2', '1234', '01012341235', 2),
       (1000, 'channie3', 'chan3', '1234', '01012341236', 3);

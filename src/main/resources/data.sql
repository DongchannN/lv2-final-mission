INSERT INTO gym(location, name, phone_number)
VALUES ('location', 'name', '01099999999');

INSERT INTO trainer(credit_price, gym_id, description, image_url, name)
VALUES (10, 1, 'description', 'url', 'name');

INSERT INTO trainer_schedule(time, trainer_id)
VALUES ('13:00', 1),
       ('14:00', 1),
       ('15:00', 1),
       ('16:00', 1);

INSERT INTO member(credit_amount, name, nickname, password, phone_number, gym_id)
VALUES (1000, 'channie', 'chan', '1234', '01012341234', 1);

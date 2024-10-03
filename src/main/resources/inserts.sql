-- Geographical
INSERT INTO Region VALUES ('Västra Götaland');
INSERT INTO Municipality VALUES ('Göteborg', 'Västra Götaland');
INSERT INTO Municipality VALUES ('Borås', 'Västra Götaland');
INSERT INTO Municipality VALUES ('Härryda', 'Västra Götaland');
INSERT INTO Municipality VALUES ('Trollhättan', 'Västra Götaland');

INSERT INTO Region VALUES ('Halland');
INSERT INTO Municipality VALUES ('Halmstad', 'Halland');
INSERT INTO Municipality VALUES ('Falkenberg', 'Halland');
INSERT INTO Municipality VALUES ('Kungsbacka', 'Halland');
INSERT INTO Municipality VALUES ('Varberg', 'Halland');

-- Citizens
INSERT INTO Citizen VALUES ('0305251111', 'Hassan', 'Khan','hejsan123', 'Trollhättan');
INSERT INTO Citizen VALUES ('0311261111', 'David', 'Golebiak', 'tjabatjena', 'Härryda');
INSERT INTO Citizen VALUES ('9603291111', 'Sebastian', 'Kolbel', 'hejhallo', 'Varberg');

-- Polls
INSERT INTO Poll VALUES (0,'0305251111', 'Borås', 'Municipal', 'Tear down Borås!', 'Valid argument tbh!', '2024-09-28', '2024-09-30', 0, 0, 0);
INSERT INTO Poll VALUES (1,'0311261111', 'Göteborg', 'Regional', 'Dance prohibition!', 'Please no dancing!', '2024-10-01', '2024-10-07', 0, 0, 0);
INSERT INTO Poll VALUES (2,'0311261111', 'Varberg', 'National', 'Redirect pension fund!', 'I think we should redirect our pension fund to stock options on the oil market!', '2024-10-03', '2024-10-10', 0, 0, 0);

INSERT INTO Casted VALUES ('0305251111', 1);
INSERT INTO Casted VALUES ('0311261111', 1);
INSERT INTO Casted VALUES ('0311261111', 2);

-- Query that will be mapped to the record in Citizen.java
SELECT id, firstName, lastName, pass, home AS municipality, region
FROM Citizen c JOIN Municipality m ON c.home = m.name;

-- Query that will be mapped to the record in Poll.java
SELECT id, home AS municipality, region, level, title, body, startDate, endDate, blank, favor, against
FROM Poll p JOIN Municipality m ON p.home = m.name;
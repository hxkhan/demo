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

-- Past Polls
INSERT INTO Poll VALUES (0,'0305251111', 'Trollhättan', 'Municipal', 'Tear down MÅG', 'Valid argument tbh!', '2024-09-01', '2024-09-07', 0, 0, 0);
INSERT INTO Poll VALUES (1,'0311261111', 'Härryda', 'Regional', 'Dance prohibition!', 'Please no dancing!', '2024-10-01', '2024-10-05', 0, 0, 0);

-- Active Polls
INSERT INTO Poll VALUES (2,'0311261111', 'Härryda', 'Regional', 'Prohibit Alcohol on campus!', 'Self explanatory!', '2024-10-01', '2024-10-31', 0, 0, 0);
INSERT INTO Poll VALUES (3,'0305251111', 'Trollhättan', 'National', 'Invade Denmark!', 'Take back what is ours!', '2024-10-01', '2024-10-31', 0, 0, 0);

-- Future Polls
INSERT INTO Poll VALUES (4, '9603291111', 'Varberg', 'Regional', 'Redirect pension fund!', 'I think we should redirect our VGR pension fund to stock options on the oil market!', '2024-10-21', '2024-10-25', 0, 0, 0);
INSERT INTO Poll VALUES (5, '0311261111', 'Härryda', 'National', 'Leave EU!', 'Keep Sweden Swedish, we make our own decisions about our future! USB C sucks!', '2024-11-01', '2024-11-30', 0, 0, 0);
INSERT INTO PollTopic VALUES (5, 'Economy');

-- News
INSERT INTO News (id, title, body, date) VALUES (0, 'Igor Bogdanoff death', 'Lived a great life.', '2022-12-28 12:00:00');
INSERT INTO News VALUES (1, 'Example News', 'This is an example of how news would look like here', '2024-10-04 19:54:32');

-- NewsComment
INSERT INTO NewsComment VALUES(0,'0311261111','rip lived a legend','2022-12-29 12:00:00');

-- SpecialRole
INSERT INTO SpecialRole VALUES('0311261111','Press Secretary');

-- Query that will be mapped to the record in Citizen.java
SELECT id, firstName, lastName, pass, home AS municipality, region
FROM Citizen c JOIN Municipality m ON c.home = m.name;

-- Query that will be mapped to the record in Poll.java
SELECT id, home AS municipality, region, level, title, body, startDate, endDate, blank, favor, against
FROM Poll p JOIN Municipality m ON p.home = m.name;
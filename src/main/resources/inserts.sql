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
INSERT INTO Poll (home, level, title, body, startDate, endDate, blank, favor, against)
    VALUES ('Borås', 'Municipal', 'Tear down Borås!', 'Valid argument tbh!', '2024-09-28', '2024-09-30', 0, 0, 0);
INSERT INTO Poll (home, level, title, body, startDate, endDate, blank, favor, against)
    VALUES ('Göteborg', 'Regional', 'Dance prohibition!', 'Please no dancing!', '2024-10-01', '2024-10-07', 0, 0, 0);
INSERT INTO Poll (home, level, title, body, startDate, endDate, blank, favor, against)
    VALUES ('Varberg', 'National', 'Redirect pension fund!', 'I think we should redirect our pension fund to stock options on the oil market!', '2024-10-03', '2024-10-10', 0, 0, 0);

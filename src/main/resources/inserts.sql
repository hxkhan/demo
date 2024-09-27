-- Geographical
INSERT INTO Region VALUES ('Västra Götaland');
INSERT INTO Region VALUES ('Halland');
INSERT INTO Region VALUES ('Skåne');
INSERT INTO Region VALUES ('Stockholms län');

INSERT INTO Municipality VALUES ('Göteborg');
INSERT INTO Municipality VALUES ('Härryda');
INSERT INTO Municipality VALUES ('Varberg');
INSERT INTO Municipality VALUES ('Trollhättan');

-- Citizens
INSERT INTO Citizen VALUES ('0305251111', 'Hassan', 'Khan','hejsan123', 'Trollhättan', 'Västra Götaland');
INSERT INTO Citizen VALUES ('0311261111', 'David', 'Golebiak', 'tjabatjena', 'Härryda', 'Västra Götaland');
INSERT INTO Citizen VALUES ('9603291111', 'Sebastian', 'Kolbel', 'hejhallo', 'Varberg', 'Västra Götaland');

-- Polls
INSERT INTO Poll (creator, level, title, body, startDate, endDate, blank, favor, against) VALUES ('0305251111', 'National', 'Dance prohibition!', 'Please no dancing!', '2024-10-01', '2024-10-07', 0, 0, 0);
INSERT INTO Poll (creator, level, title, body, startDate, endDate, blank, favor, against) VALUES ('9603291111', 'Municipal', 'Redirect pension fund!', 'I think we should redirect our pension fund to stock options on the oil market!', '2024-10-03', '2024-10-10', 0, 0, 0);

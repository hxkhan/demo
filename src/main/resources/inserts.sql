
-- Area
INSERT INTO Area VALUES ('Göteborg','Municipal','Västra Götaland');
INSERT INTO Area (name, level) VALUES ('Västra Götaland','County');
INSERT INTO Area (name, level) VALUES ('Halland', 'County');
INSERT INTO Area VALUES ('Varberg', 'Municipal', 'Halland');

INSERT INTO Municipality VALUES ('Göteborg');
INSERT INTO Municipality VALUES ('Varberg');

-- Citizens
INSERT INTO Citizens VALUES ('0305251111', 'Hassan Khan','hejsan123', 'Göteborg');
INSERT INTO Citizens VALUES ('0311261111', 'David Golebiak', 'tjabatjena', 'Göteborg');
INSERT INTO Citizens VALUES ('9603291111', 'Sebastian Kolbel', 'hejhallo', 'Varberg');

-- Referendums
INSERT INTO Referendum VALUES (1, 'Västra Götaland', 'Dance prohibition!', 'Please no dancing!', '2024-10-01', '2024-10-07');
INSERT INTO Referendum VALUES (2, 'Varberg', 'Redirect pension fund!', 'I think we should redirect our pension fund to stock options on the oil market!', '2024-10-03', '2024-10-10');

INSERT INTO RefResults VALUES (1, 1, 0, 0);
INSERT INTO RefResults VALUES (2, 0, 0, 0);

INSERT INTO RefRoll VALUES ('0305251111', 1);

-- testing queries
-- finds valid citizens for a given referendum
SELECT * FROM Citizens JOIN
(SELECT name FROM Area JOIN Referendum ON Area.name = Referendum.area
WHERE Referendum.id = 1
UNION
SELECT name FROM Area JOIN Referendum ON Area.partof = Referendum.area
WHERE Referendum.id = 1)
AS Valid
WHERE Citizens.home = Valid.name
AND Citizens.id NOT IN
(Select citizen FROM RefRoll
WHERE referendum = 1)
AND Citizens.id = '9603291111';

-- Checks if given citizen voted in referendum
SELECT * FROM RefRoll
WHERE citizen = '0305251111'
AND referendum = 1;

-- checks if referendum is open
SELECT * FROM Referendum
WHERE id = 1
AND CURRENT_DATE >= startDate
AND CURRENT_DATE <= endDate;
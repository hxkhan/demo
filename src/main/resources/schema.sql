-- TO RESET EVERYTHING
DROP TABLE IF EXISTS Casted CASCADE;
DROP TABLE IF EXISTS Poll CASCADE;
DROP TABLE IF EXISTS Citizen CASCADE;
DROP TABLE IF EXISTS Municipality CASCADE;
DROP TABLE IF EXISTS Region CASCADE;

-- INIT
CREATE TABLE Region (
    name TEXT PRIMARY KEY
);

CREATE TABLE Municipality (
    name TEXT PRIMARY KEY,
    region TEXT NOT NULL REFERENCES Region(name)
);

CREATE TABLE Citizen (
    id CHAR(10) PRIMARY KEY,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    pass TEXT NOT NULL,
    home TEXT NOT NULL REFERENCES Municipality(name),

    CHECK (id ~ '^\d+?$')
);

CREATE TABLE Poll (
    id INT PRIMARY KEY,
    creator TEXT REFERENCES Citizen(id),
    -- home is where this poll was created (creator's region/municipality at the time of creation)
    -- will also be very good for statistics later on
    home TEXT NOT NULL REFERENCES Municipality(name),

    -- level decides if this will be a national, regional or municipal poll
    level TEXT NOT NULL,
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,

    -- votes
    blank INT NOT NULL CHECK (blank >= 0),
    favor INT NOT NULL CHECK (favor >= 0),
    against INT NOT NULL CHECK (against >= 0),

    CHECK (level IN ('Municipal', 'Regional', 'National')),
    CHECK (startDate < endDate)
);

-- one vote per citizen/poll and there is no record of who voted what like the group decided; very safe very secure
CREATE TABLE Casted (
    voter CHAR(10) REFERENCES Citizen(id),
    poll INT REFERENCES Poll(id),

    PRIMARY KEY (voter, poll)
);

/* A voting procedure in Database.java would be two updates in a single transaction

    BEGIN TRANSACTION;
    INSERT INTO Casted VALUES ('0305251111', 2);
    UPDATE Poll SET favor = favor + 1 WHERE id = 2;
    COMMIT;

*/


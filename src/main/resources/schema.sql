-- TO RESET EVERYTHING
DROP TABLE IF EXISTS SpecialRole CASCADE;
DROP TABLE IF EXISTS CastedOpinion CASCADE;
DROP TABLE IF EXISTS NewsComment CASCADE;
DROP TABLE IF EXISTS News CASCADE;
DROP TABLE IF EXISTS Icon CASCADE;

DROP TABLE IF EXISTS Casted CASCADE;
DROP TABLE IF EXISTS PollTopic CASCADE;

DROP TABLE IF EXISTS Poll CASCADE;
DROP TABLE IF EXISTS PollTopic CASCADE;
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
    CHECK (startDate <= endDate)
);

CREATE TABLE PollTopic (
    id INT REFERENCES Poll(id),
    topic TEXT NOT NULL,

    PRIMARY KEY (id, topic)
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



-- NEWS

CREATE TABLE News (
    id INT PRIMARY KEY,
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    date DATETIME NOT NULL
);

CREATE TABLE CastedOpinion (
    newsId INT REFERENCES News(id),
    citizenId CHAR(10) REFERENCES Citizen(id),
    favorable BOOLEAN NOT NULL,
    PRIMARY KEY(newsId, citizenId)
);

CREATE TABLE SpecialRole (
    citizenId CHAR(10) PRIMARY KEY REFERENCES Citizen(id),
    role TEXT NOT NULL CHECK (role IN ('Press Secretary')) -- Add the rest of the roles later e.g. 'Minister'
);

CREATE TABLE NewsComment (
    newsId INT REFERENCES News(id),
    citizenId CHAR(10) REFERENCES Citizen(id),
    comment TEXT NOT NULL,
    date DATETIME NOT NULL,
    PRIMARY KEY (newsId, citizenId, date)
);

CREATE TABLE Icon (
    citizenId CHAR(10) PRIMARY KEY REFERENCES Citizen(id),
    icon BLOB NOT NULL
);
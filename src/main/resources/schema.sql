-- TO RESET EVERYTHING
DROP TABLE IF EXISTS RefResult;
DROP TABLE IF EXISTS RefRoll;
DROP TABLE IF EXISTS Referendum;
DROP TABLE IF EXISTS Area;
DROP TABLE IF EXISTS Citizens;


-- INIT

CREATE TABLE Citizens (
    id CHAR(10) PRIMARY KEY,
    name TEXT NOT NULL,
    pass TEXT NOT NULL,
    CHECK (id ~ '^\d+?$')
);

-- area of concern
CREATE TABLE Area (
    name TEXT PRIMARY KEY,
    level TEXT NOT NULL,
    partof TEXT DEFAULT ('Not municipal'),

    CHECK (level IN ('National', 'County', 'Municipal'))
);

CREATE TABLE Municipality (
    name TEXT REFERENCES Area(name)
);


CREATE TABLE Referendum (
    id TEXT PRIMARY KEY,
    area TEXT NOT NULL REFERENCES Area(name),
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,

    CHECK (startDate < endDate)
);

CREATE TABLE RefResults (
    referendum TEXT PRIMARY KEY REFERENCES Referendum(id),
    blank INT NOT NULL CHECK (blank >= 0),
    favor INT NOT NULL CHECK (favor >= 0),
    against INT NOT NULL CHECK (against >= 0)
);

CREATE TABLE RefRoll (
    citizen CHAR(10) REFERENCES Citizens(id),
    referendum TEXT REFERENCES Referendum(id),

    PRIMARY KEY (citizen, referendum)
);


-- Mock data for us
INSERT INTO Citizens VALUES ('0305251111', 'Hassan Khan','hejsan123');
INSERT INTO Citizens VALUES ('0311261111', 'David Golebiak', 'tjabatjena');
INSERT INTO Citizens VALUES ('9603291111', 'Sebastian Kolbel', 'hejhallo');

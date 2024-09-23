-- TO RESET EVERYTHING
DROP TABLE IF EXISTS RefResults;
DROP TABLE IF EXISTS RefRoll;
DROP TABLE IF EXISTS Referendum;
DROP TABLE IF EXISTS Citizens;
DROP TABLE IF EXISTS Municipality;
DROP TABLE IF EXISTS Area;


-- INIT
CREATE TABLE Area (
    name TEXT PRIMARY KEY,
    level TEXT NOT NULL,
    partof TEXT DEFAULT ('Not municipal'),

    CHECK (level IN ('National', 'County', 'Municipal'))
);

CREATE TABLE Municipality (
    name TEXT PRIMARY KEY REFERENCES Area(name)
);

CREATE TABLE Citizens (
    id CHAR(10) PRIMARY KEY,
    name TEXT NOT NULL,
    pass TEXT NOT NULL,
    home TEXT REFERENCES Municipality(name),
    CHECK (id ~ '^\d+?$')
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
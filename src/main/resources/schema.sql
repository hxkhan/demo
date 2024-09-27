-- TO RESET EVERYTHING
DROP TABLE IF EXISTS Poll;
DROP TABLE IF EXISTS Citizen;
DROP TABLE IF EXISTS Region;
DROP TABLE IF EXISTS Municipality;

-- INIT
CREATE TABLE Region (
    name TEXT PRIMARY KEY
);

CREATE TABLE Municipality (
    name TEXT PRIMARY KEY
);

CREATE TABLE Citizen (
    id CHAR(10) PRIMARY KEY,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    pass TEXT NOT NULL,
    municipality TEXT NOT NULL REFERENCES Municipality(name),
    region TEXT NOT NULL REFERENCES Region(name),
    CHECK (id ~ '^\d+?$')
);

CREATE TABLE Poll (
    id INT PRIMARY KEY AUTO_INCREMENT,
    creator CHAR(10) NOT NULL REFERENCES Citizen(id),
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
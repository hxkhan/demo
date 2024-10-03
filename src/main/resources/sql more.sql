CREATE TABLE News (
    id INT PRIMARY KEY,
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    date DATE NOT NULL
);

CREATE TABLE CastedOpinion (
    newsId INT REFERENCES News(id),
    citizenId CHAR(10) REFERENCES Citizen(id),
    favorable BOOLEAN NOT NULL,
    PRIMARY KEY(newsId, citizenId)
);

CREATE TABLE SpecialRole (
    citizenId CHAR(10) REFERENCES Citizen(id),
    Role TEXT NOT NULL CHECK (Role IN ('Press Secretary')) -- Add the rest of the roles later e.g. 'Minister'
)
-- INIT
CREATE TABLE Users (
    id CHAR(10) PRIMARY KEY, -- 030525xxxx
    name TEXT NOT NULL
);

INSERT INTO Users VALUES ('030525xxxx', 'Hassan Khan');
INSERT INTO Users VALUES ('031126xxxx', 'David Golebiak');


-- TO RESET
DROP TABLE IF EXISTS Users;
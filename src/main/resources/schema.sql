-- TO RESET EVERYTHING
DROP TABLE IF EXISTS Citizens;

-- INIT

-- Users table

CREATE TABLE Citizens (
    id CHAR(10) PRIMARY KEY,
    name TEXT NOT NULL,
    pass TEXT NOT NULL,
    CHECK (id ~ '^\d+?$')
);

-- Mock data for us
INSERT INTO Citizens VALUES ('0305251111', 'Hassan Khan','hejsan123');
INSERT INTO Citizens VALUES ('0311261111', 'David Golebiak', 'tjabatjena');
INSERT INTO Citizens VALUES ('9603291111', 'Sebastian Kolbel', 'hejhallo');

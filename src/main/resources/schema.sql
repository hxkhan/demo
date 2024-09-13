-- TO RESET EVERYTHING
DROP TABLE IF EXISTS Citizens;

-- INIT

-- Users table
CREATE TABLE Citizens (
    id CHAR(10) PRIMARY KEY, -- 030525xxxx
    name TEXT NOT NULL
);

-- Mock data for us
INSERT INTO Citizens VALUES ('030525xxxx', 'Hassan Khan');
INSERT INTO Citizens VALUES ('031126xxxx', 'David Golebiak');

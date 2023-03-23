CREATE TABLE IF NOT EXISTS Customer(
                                       id VARCHAR(100) PRIMARY KEY ,
                                       name VARCHAR(100) NOT NULL ,
                                       address VARCHAR(100) NOT NULL
);
CREATE TABLE IF NOT EXISTS Students(
    id VARCHAR(100) PRIMARY KEY ,
    name VARCHAR(300) NOT NULL ,
    address VARCHAR(500)NOT NULL
);




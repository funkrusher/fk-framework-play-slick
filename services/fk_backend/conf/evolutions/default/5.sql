-- !Ups

CREATE TABLE test2
(
    id          INT(7) NOT NULL AUTO_INCREMENT,
    description VARCHAR(50),
    PRIMARY KEY (id)
);


-- !Downs

DROP TABLE test2;

-- !Ups

CREATE TABLE language (
                          id              INT(7)        NOT NULL AUTO_INCREMENT,
                          cd              CHAR(2)       NOT NULL,
                          description     VARCHAR(50),
                          PRIMARY KEY (id)
);

CREATE TABLE author (
                        id              BIGINT(255)   NOT NULL AUTO_INCREMENT,
                        first_name      VARCHAR(50),
                        last_name       VARCHAR(50)   NOT NULL,
                        date_of_birth   DATE NULL,
                        year_of_birth   INT(7),
                        created_at      timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                        reviewed_at     timestamp NULL,
                        distinguished   INT(1),
                        PRIMARY KEY (id)
);

CREATE TABLE book (
                      id              BIGINT(255) NOT NULL AUTO_INCREMENT,
                      author_id       BIGINT(255)  NOT NULL,
                      title           VARCHAR(400) NOT NULL,
                      published_in    INT(7)     NOT NULL,
                      language_id     INT(7)     NOT NULL,
                      PRIMARY KEY (id),
                      CONSTRAINT fk_book_author     FOREIGN KEY (author_id)   REFERENCES author(id),
                      CONSTRAINT fk_book_language   FOREIGN KEY (language_id) REFERENCES language(id)
);

CREATE TABLE book_store (
    name            VARCHAR(400) NOT NULL UNIQUE
);

CREATE TABLE book_to_book_store (
                                    name            VARCHAR(400) NOT NULL,
                                    book_id         BIGINT(255)  NOT NULL,
                                    stock           INT,
                                    PRIMARY KEY(name, book_id),
                                    CONSTRAINT fk_b2bs_book_store FOREIGN KEY (name)        REFERENCES book_store (name) ON DELETE CASCADE,
                                    CONSTRAINT fk_b2bs_book       FOREIGN KEY (book_id)     REFERENCES book (id)         ON DELETE CASCADE
);

-- !Downs

DROP TABLE book_to_book_store;
DROP TABLE book_store;
DROP TABLE book;
DROP TABLE author;
DROP TABLE language;
-- !Ups

CREATE TEMPORARY TABLE author_temp_data (
    id INT AUTO_INCREMENT PRIMARY KEY
);
CREATE TEMPORARY TABLE book_temp_data (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    author_id INT
);
CREATE TEMPORARY TABLE book_store_temp_data (
    book_store_id INT AUTO_INCREMENT PRIMARY KEY
);
CREATE TEMPORARY TABLE book_to_book_store_temp_data (
    book_id INT,
    book_store_id INT
);

-- 1000 authors
INSERT INTO author_temp_data (id) WITH RECURSIVE random_data AS (SELECT 1 AS row_number UNION ALL SELECT row_number + 1 FROM random_data WHERE row_number < 1000)
SELECT row_number FROM random_data;

-- 10000 books, equally divided on the 1000 authors
INSERT INTO book_temp_data (book_id, author_id) WITH RECURSIVE random_data AS (SELECT 1 AS row_number UNION ALL SELECT row_number + 1 FROM random_data WHERE row_number < 10000)
SELECT row_number, row_number % 1000 + 1 FROM random_data;

-- 100 book stores
INSERT INTO book_store_temp_data (book_store_id) WITH RECURSIVE random_data AS (SELECT 1 AS row_number UNION ALL SELECT row_number + 1 FROM random_data WHERE row_number < 100)
SELECT row_number FROM random_data;

-- 50000 books to bookstore relations. each of the 10000 books is available in 5 book-stores.
INSERT INTO book_to_book_store_temp_data (book_id, book_store_id) WITH RECURSIVE random_data AS (SELECT 1 AS row_number UNION ALL SELECT row_number + 1 FROM random_data WHERE row_number < 10000)
SELECT row_number, row_number % 99 + 1 FROM random_data;

DELETE FROM book_store;
DELETE FROM book;
DELETE FROM book_to_book_store;
DELETE FROM author;

INSERT INTO language (id, cd, description) VALUES (1, 'en', 'English');
INSERT INTO language (id, cd, description) VALUES (2, 'de', 'Deutsch');
INSERT INTO language (id, cd, description) VALUES (3, 'fr', 'Français');
INSERT INTO language (id, cd, description) VALUES (4, 'pt', 'Português');

INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10001 , 'George'  , 'Orwell' , DATE '1903-06-26', 1903         );
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10002 , 'Paulo'   , 'Coelho' , DATE '1947-08-24', 1947         );
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10003 , 'Robert'  , 'Musil' , DATE '1903-06-26', 1903         );
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10004 , 'Claudia Simone'   , 'Dorchain' , DATE '1947-08-24', 1947);
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10005 , 'Georgia'  , 'Agamben' , DATE '1903-06-26', 1903);
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10006 , 'Leo'   , 'Tolstoi' , DATE '1947-08-24', 1947);
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10007 , 'Fjodor'  , 'Dostojewski' , DATE '1903-06-26', 1903);
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10008 , 'Franz'   , 'Kafka' , DATE '1947-08-24', 1947         );
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10009 , 'Hermann'  , 'Hesse' , DATE '1903-06-26', 1903         );
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10010 , 'Thomas'   , 'Mann' , DATE '1947-08-24', 1947         );
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10011 , 'Sybille'  , 'Berg' , DATE '1903-06-26', 1903         );
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10012 , 'Julia'   , 'Zeh' , DATE '1947-08-24', 1947         );
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10013 , 'Frank'  , 'Herbert' , DATE '1903-06-26', 1903);
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10014 , 'Michael'   , 'Hedges' , DATE '1947-08-24', 1947);
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10015 , 'Anna'  , 'Sutter' , DATE '1903-06-26', 1903         );
INSERT INTO author (id, first_name, last_name, date_of_birth    , year_of_birth)
VALUES           (10016 , 'Rainer'   , 'Mai' , DATE '1947-08-24', 1947         );

INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10001 , 10001    , '1984'        , 1948        , 1          );
INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10002 , 10001    , 'Animal Farm' , 1945        , 1          );
INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10003 , 10002    , 'O Alquimista', 1988        , 4          );
INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10004 , 10003    , 'Brida1'       , 1990        , 2          );
INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10005 , 10003    , 'Brida2'       , 1990        , 2          );
INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10006 , 10004    , 'Brida3'       , 1990        , 2          );
INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10007 , 10008    , 'Brida4'       , 1990        , 2          );
INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10008 , 10008    , 'Brida5'       , 1990        , 2          );
INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10009 , 10009    , 'Brida6'       , 1990        , 2          );
INSERT INTO book (id, author_id, title         , published_in, language_id)
VALUES         (10010 , 10010    , 'Brida7'       , 1990        , 2          );

INSERT INTO book_store VALUES ('Orell Füssli');
INSERT INTO book_store VALUES ('Ex Libris');
INSERT INTO book_store VALUES ('Buchhandlung im Volkshaus');

INSERT INTO book_to_book_store VALUES ('Orell Füssli'             , 10001, 10010);
INSERT INTO book_to_book_store VALUES ('Orell Füssli'             , 10002, 10010);
INSERT INTO book_to_book_store VALUES ('Orell Füssli'             , 10003, 10010);
INSERT INTO book_to_book_store VALUES ('Ex Libris'                , 10001, 10001 );
INSERT INTO book_to_book_store VALUES ('Ex Libris'                , 10003, 10002 );
INSERT INTO book_to_book_store VALUES ('Buchhandlung im Volkshaus', 10003, 10001 );

-- Copy the data from the temporary table to your target table
INSERT INTO author (id, first_name, last_name, date_of_birth, year_of_birth)
SELECT id,  RAND() , RAND(), '1903-05-26', 1903 FROM author_temp_data;

INSERT INTO book (id, author_id, title, published_in, language_id)
SELECT book_id, author_id, RAND(), 1903, 2 FROM book_temp_data;

INSERT INTO book_store (name)
SELECT book_store_id FROM book_store_temp_data;

INSERT INTO book_to_book_store (name, book_id, stock)
SELECT book_store_id, book_id, RAND() FROM book_to_book_store_temp_data;

-- Drop the temporary table
DROP TEMPORARY TABLE book_to_book_store_temp_data;
DROP TEMPORARY TABLE book_store_temp_data;
DROP TEMPORARY TABLE book_temp_data;
DROP TEMPORARY TABLE author_temp_data;

-- !Downs

DELETE FROM book_store;
DELETE FROM book;
DELETE FROM book_to_book_store;
DELETE FROM author;
-- !Ups

-- we first need to enlargen the play_evolutions table, because typical SQL-files are bigger than the given limit!
ALTER TABLE `play_evolutions` MODIFY COLUMN `apply_script` LONGTEXT;
ALTER TABLE `play_evolutions` MODIFY COLUMN `revert_script` LONGTEXT;

-- !Downs

ALTER TABLE `play_evolutions` MODIFY COLUMN `revert_script` TEXT;
ALTER TABLE `play_evolutions` MODIFY COLUMN `apply_script` TEXT;
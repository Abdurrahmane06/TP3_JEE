CREATE DATABASE IF NOT EXISTS `student_tracker`;
USE `student_tracker`;

-- Créer l'utilisateur springstudent (si pas encore fait)
-- CREATE USER 'springstudent'@'localhost' IDENTIFIED BY 'springstudent';
-- GRANT ALL PRIVILEGES ON student_tracker.* TO 'springstudent'@'localhost';
-- FLUSH PRIVILEGES;

CREATE TABLE IF NOT EXISTS `student` (
  `id`         INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45),
  `last_name`  VARCHAR(45),
  `email`      VARCHAR(45),
  PRIMARY KEY (`id`)
);

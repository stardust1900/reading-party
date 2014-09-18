DROP DATABASE IF EXISTS `readingparty`;
CREATE DATABASE `readingparty` DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
USE 'readingparty';
GRANT ALL PRIVILEGES ON readingparty.* TO 'readingparty'@'localhost' IDENTIFIED BY 'readingparty' WITH GRANT OPTION;
FLUSH PRIVILEGES;
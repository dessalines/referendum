-- ---
-- Globals
-- ---

-- SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET FOREIGN_KEY_CHECKS=0;

-- ---
-- Table 'poll'
-- 
-- ---

DROP TABLE IF EXISTS `poll`;
    
CREATE TABLE `poll` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `poll_type_id` INTEGER NULL DEFAULT NULL,
  `discussion_id` INTEGER NULL DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  `private_password` VARCHAR(140) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`discussion_id`)
);

-- ---
-- Table 'ballot_item'
-- 
-- ---

DROP TABLE IF EXISTS `ballot_item`;
    
CREATE TABLE `ballot_item` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `ballot_id` INTEGER NULL DEFAULT NULL,
  `candidate_id` INTEGER NULL DEFAULT NULL,
  `rank` INTEGER NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'candidate'
-- 
-- ---

DROP TABLE IF EXISTS `candidate`;
    
CREATE TABLE `candidate` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `poll_id` INTEGER NULL DEFAULT NULL,
  `discussion_id` INTEGER NULL DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`discussion_id`)
);

-- ---
-- Table 'discussion'
-- 
-- ---

DROP TABLE IF EXISTS `discussion`;
    
CREATE TABLE `discussion` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `subject` VARCHAR(140) NOT NULL DEFAULT 'NULL',
  `text` MEDIUMTEXT NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'comment'
-- 
-- ---

DROP TABLE IF EXISTS `comment`;
    
CREATE TABLE `comment` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `discussion_id` INTEGER NULL DEFAULT NULL,
  `text` MEDIUMTEXT NULL DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'comment_tree'
-- 
-- ---

DROP TABLE IF EXISTS `comment_tree`;
    
CREATE TABLE `comment_tree` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `parent_id` INTEGER NULL DEFAULT NULL,
  `child_id` INTEGER NULL DEFAULT NULL,
  `path_length` INTEGER NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'poll_type'
-- 
-- ---

DROP TABLE IF EXISTS `poll_type`;
    
CREATE TABLE `poll_type` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `name` VARCHAR(255) NOT NULL DEFAULT 'NULL',
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'tag'
-- 
-- ---

DROP TABLE IF EXISTS `tag`;
    
CREATE TABLE `tag` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `name` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'poll_tag'
-- 
-- ---

DROP TABLE IF EXISTS `poll_tag`;
    
CREATE TABLE `poll_tag` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `poll_id` INTEGER NULL DEFAULT NULL,
  `tag_id` INTEGER NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'ballot'
-- 
-- ---

DROP TABLE IF EXISTS `ballot`;
    
CREATE TABLE `ballot` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `poll_id` INTEGER NULL DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'user'
-- 
-- ---

DROP TABLE IF EXISTS `user`;
    
CREATE TABLE `user` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `ip_address` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'full_user'
-- 
-- ---

DROP TABLE IF EXISTS `full_user`;
    
CREATE TABLE `full_user` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  `password_encrypted` VARCHAR(512) NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Foreign Keys 
-- ---

ALTER TABLE `poll` ADD FOREIGN KEY (poll_type_id) REFERENCES `poll_type` (`id`);
ALTER TABLE `poll` ADD FOREIGN KEY (discussion_id) REFERENCES `discussion` (`id`);
ALTER TABLE `poll` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `ballot_item` ADD FOREIGN KEY (ballot_id) REFERENCES `ballot` (`id`);
ALTER TABLE `ballot_item` ADD FOREIGN KEY (candidate_id) REFERENCES `candidate` (`id`);
ALTER TABLE `candidate` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`);
ALTER TABLE `candidate` ADD FOREIGN KEY (discussion_id) REFERENCES `discussion` (`id`);
ALTER TABLE `candidate` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `comment` ADD FOREIGN KEY (discussion_id) REFERENCES `discussion` (`id`);
ALTER TABLE `comment` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `comment_tree` ADD FOREIGN KEY (parent_id) REFERENCES `comment` (`id`);
ALTER TABLE `comment_tree` ADD FOREIGN KEY (child_id) REFERENCES `comment` (`id`);
ALTER TABLE `poll_tag` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`);
ALTER TABLE `poll_tag` ADD FOREIGN KEY (tag_id) REFERENCES `tag` (`id`);
ALTER TABLE `ballot` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`);
ALTER TABLE `ballot` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `full_user` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);

-- ---
-- Table Properties
-- ---

-- ALTER TABLE `poll` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `ballot_item` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `candidate` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `discussion` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `comment` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `comment_tree` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `poll_type` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `tag` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `poll_tag` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `ballot` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `user` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `full_user` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ---
-- Test Data
-- ---

-- INSERT INTO `poll` (`id`,`poll_type_id`,`discussion_id`,`user_id`,`private_password`) VALUES
-- ('','','','','');
-- INSERT INTO `ballot_item` (`id`,`ballot_id`,`candidate_id`,`rank`) VALUES
-- ('','','','');
-- INSERT INTO `candidate` (`id`,`poll_id`,`discussion_id`,`user_id`) VALUES
-- ('','','','');
-- INSERT INTO `discussion` (`id`,`subject`,`text`) VALUES
-- ('','','');
-- INSERT INTO `comment` (`id`,`discussion_id`,`text`,`user_id`) VALUES
-- ('','','','');
-- INSERT INTO `comment_tree` (`id`,`parent_id`,`child_id`,`path_length`) VALUES
-- ('','','','');
-- INSERT INTO `poll_type` (`id`,`name`) VALUES
-- ('','');
-- INSERT INTO `tag` (`id`,`name`) VALUES
-- ('','');
-- INSERT INTO `poll_tag` (`id`,`poll_id`,`tag_id`) VALUES
-- ('','','');
-- INSERT INTO `ballot` (`id`,`poll_id`,`user_id`) VALUES
-- ('','','');
-- INSERT INTO `user` (`id`,`ip_address`) VALUES
-- ('','');
-- INSERT INTO `full_user` (`id`,`user_id`,`name`,`password_encrypted`) VALUES
-- ('','','','');
SET FOREIGN_KEY_CHECKS=1;
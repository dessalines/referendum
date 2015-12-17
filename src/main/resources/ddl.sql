-- ---
-- Globals
-- ---

-- SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
-- SET FOREIGN_KEY_CHECKS=0;

-- ---
-- Table 'poll'
-- 
-- ---

DROP TABLE IF EXISTS `poll`;
    
CREATE TABLE `poll` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `poll_type_id` INTEGER NULL DEFAULT NULL,
  `discussion_id` INTEGER NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'ballot'
-- 
-- ---

DROP TABLE IF EXISTS `ballot`;
    
CREATE TABLE `ballot` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
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
  `discussion_id` INTEGER NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'discussion'
-- 
-- ---

DROP TABLE IF EXISTS `discussion`;
    
CREATE TABLE `discussion` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `subject` VARCHAR(144) NOT NULL DEFAULT 'NULL',
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
-- Foreign Keys 
-- ---

ALTER TABLE `poll` ADD FOREIGN KEY (poll_type_id) REFERENCES `poll_type` (`id`);
ALTER TABLE `poll` ADD FOREIGN KEY (discussion_id) REFERENCES `discussion` (`id`);
ALTER TABLE `ballot` ADD FOREIGN KEY (candidate_id) REFERENCES `candidate` (`id`);
ALTER TABLE `candidate` ADD FOREIGN KEY (discussion_id) REFERENCES `discussion` (`id`);
ALTER TABLE `comment` ADD FOREIGN KEY (discussion_id) REFERENCES `discussion` (`id`);
ALTER TABLE `poll_tag` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`);
ALTER TABLE `poll_tag` ADD FOREIGN KEY (tag_id) REFERENCES `tag` (`id`);

-- ---
-- Table Properties
-- ---

-- ALTER TABLE `poll` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `ballot` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `candidate` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `discussion` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `comment` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `comment_tree` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `poll_type` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `tag` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `poll_tag` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ---
-- Test Data
-- ---

-- INSERT INTO `poll` (`id`,`poll_type_id`,`discussion_id`) VALUES
-- ('','','');
-- INSERT INTO `ballot` (`id`,`candidate_id`,`rank`) VALUES
-- ('','','');
-- INSERT INTO `candidate` (`id`,`discussion_id`) VALUES
-- ('','');
-- INSERT INTO `discussion` (`id`,`subject`,`text`) VALUES
-- ('','','');
-- INSERT INTO `comment` (`id`,`discussion_id`,`text`) VALUES
-- ('','','');
-- INSERT INTO `comment_tree` (`id`,`parent_id`,`child_id`,`path_length`) VALUES
-- ('','','','');
-- INSERT INTO `poll_type` (`id`,`name`) VALUES
-- ('','');
-- INSERT INTO `tag` (`id`,`name`) VALUES
-- ('','');
-- INSERT INTO `poll_tag` (`id`,`poll_id`,`tag_id`) VALUES
-- ('','','');
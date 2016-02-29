-- ---
-- Globals
-- ---

-- SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET FOREIGN_KEY_CHECKS=0;

-- modified replace:

-- `modified` TIMESTAMP NOT NULL,
-- `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
-- 
-- ---
-- Table 'poll'
-- 
-- ---

DROP TABLE IF EXISTS `poll`;
    
CREATE TABLE `poll` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `aid` VARCHAR(255) NULL DEFAULT NULL,
  `poll_type_id` INTEGER NULL DEFAULT NULL,
  `poll_sum_type_id` INTEGER NULL DEFAULT NULL,
  `discussion_id` INTEGER NULL DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  `private_password` VARCHAR(140) NULL DEFAULT NULL,
  `full_user_only` TINYINT(1) NOT NULL DEFAULT 0,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`discussion_id`),
  KEY (`aid`)
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
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
  `subject` VARCHAR(140) NULL DEFAULT NULL,
  `text` MEDIUMTEXT NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'comment'
-- 
-- ---

DROP TABLE IF EXISTS `comment`;
    
CREATE TABLE `comment` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `aid` VARCHAR(255) NULL DEFAULT NULL,
  `discussion_id` INTEGER NULL DEFAULT NULL,
  `text` MEDIUMTEXT NULL DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `read` TINYINT(1) NOT NULL DEFAULT 0,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY (`aid`)
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
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY (`parent_id`, `child_id`)
);

-- ---
-- Table 'poll_type'
-- 
-- ---

DROP TABLE IF EXISTS `poll_type`;
    
CREATE TABLE `poll_type` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `name` VARCHAR(255) NOT NULL DEFAULT 'NULL',
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`)
);

-- ---
-- Table 'tag'
-- 
-- ---

DROP TABLE IF EXISTS `tag`;
    
CREATE TABLE `tag` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `aid` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  `name` VARCHAR(50) NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`),
  KEY (`name`),
  KEY (`aid`)
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
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`poll_id`, `tag_id`)
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
  `candidate_id` INTEGER NULL DEFAULT NULL,
  `rank` INTEGER NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`poll_id`, `user_id`, `candidate_id`)
);

-- ---
-- Table 'user'
-- 
-- ---

DROP TABLE IF EXISTS `user`;
    
CREATE TABLE `user` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `aid` VARCHAR(255) NULL DEFAULT NULL,
  `ip_address` VARCHAR(255) NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY (`aid`)
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
  `email` VARCHAR(255) NULL DEFAULT NULL,
  `password_encrypted` VARCHAR(512) NULL DEFAULT NULL,
  `created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`),
  UNIQUE KEY (`email`)
);

-- ---
-- Table 'poll_sum_type'
-- 
-- ---

DROP TABLE IF EXISTS `poll_sum_type`;
    
CREATE TABLE `poll_sum_type` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `name` VARCHAR(255) NOT NULL DEFAULT 'NULL',
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`)
);

-- ---
-- Table 'comment_rank'
-- 
-- ---

DROP TABLE IF EXISTS `comment_rank`;
    
CREATE TABLE `comment_rank` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `comment_id` INTEGER NULL DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  `rank` INTEGER NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`comment_id`, `user_id`)
);

-- ---
-- Table 'login'
-- 
-- ---

DROP TABLE IF EXISTS `login`;
    
CREATE TABLE `login` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  `auth` VARCHAR(255) NOT NULL DEFAULT 'NULL',
  `expire_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);


-- ---
-- Table 'poll_visit'
-- 
-- ---

DROP TABLE IF EXISTS `poll_visit`;
    
CREATE TABLE `poll_visit` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  `poll_id` INTEGER NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'tag_visit'
-- 
-- ---

DROP TABLE IF EXISTS `tag_visit`;
    
CREATE TABLE `tag_visit` (
  `id` INTEGER NULL AUTO_INCREMENT DEFAULT NULL,
  `user_id` INTEGER NULL DEFAULT NULL,
  `tag_id` INTEGER NULL DEFAULT NULL,
  `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);



-- ---
-- Foreign Keys 
-- ---
SELECT concat('alter table ',table_schema,'.',table_name,' DROP FOREIGN KEY ',constraint_name,';')
FROM information_schema.table_constraints
WHERE constraint_type='FOREIGN KEY'
AND table_schema='dd';     



-- if the second term is deleted, the first term is removed

ALTER TABLE `poll` ADD FOREIGN KEY (poll_type_id) REFERENCES `poll_type` (`id`);
ALTER TABLE `poll` ADD FOREIGN KEY (poll_sum_type_id) REFERENCES `poll_sum_type` (`id`);
ALTER TABLE `poll` ADD FOREIGN KEY (discussion_id) REFERENCES `discussion` (`id`) ON DELETE CASCADE;
ALTER TABLE `poll` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `candidate` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`) ON DELETE CASCADE;
ALTER TABLE `candidate` ADD FOREIGN KEY (discussion_id) REFERENCES `discussion` (`id`) ON DELETE CASCADE;
ALTER TABLE `candidate` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `comment` ADD FOREIGN KEY (discussion_id) REFERENCES `discussion` (`id`);
ALTER TABLE `comment` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `comment_tree` ADD FOREIGN KEY (parent_id) REFERENCES `comment` (`id`);
ALTER TABLE `comment_tree` ADD FOREIGN KEY (child_id) REFERENCES `comment` (`id`);
ALTER TABLE `tag` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `poll_tag` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`) ON DELETE CASCADE;
ALTER TABLE `poll_tag` ADD FOREIGN KEY (tag_id) REFERENCES `tag` (`id`);
ALTER TABLE `ballot` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`) ON DELETE CASCADE;
ALTER TABLE `ballot` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `ballot` ADD FOREIGN KEY (candidate_id) REFERENCES `candidate` (`id`) ON DELETE CASCADE;
ALTER TABLE `full_user` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `comment_rank` ADD FOREIGN KEY (comment_id) REFERENCES `comment` (`id`);
ALTER TABLE `comment_rank` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `login` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `poll_visit` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `poll_visit` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`) ON DELETE CASCADE;
ALTER TABLE `tag_visit` ADD FOREIGN KEY (user_id) REFERENCES `user` (`id`);
ALTER TABLE `tag_visit` ADD FOREIGN KEY (tag_id) REFERENCES `tag` (`id`);
-- ---
-- Table Properties
-- ---

-- ALTER TABLE `poll` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
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
-- ALTER TABLE `poll_sum_type` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ---
-- Test Data
-- ---

-- INSERT INTO `poll` (`id`,`poll_type_id`,`poll_sum_type_id`,`discussion_id`,`user_id`,`private_password`) VALUES
-- ('','','','','','');
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
-- INSERT INTO `ballot` (`id`,`poll_id`,`user_id`,`candidate_id`,`rank`) VALUES
-- ('','','','','');
-- INSERT INTO `user` (`id`,`ip_address`) VALUES
-- ('','');
-- INSERT INTO `full_user` (`id`,`user_id`,`name`,`password_encrypted`) VALUES
-- ('','','','');
-- INSERT INTO `poll_sum_type` (`id`,`name`) VALUES
-- ('','');


-- Create the trending tables and keys
DROP TABLE IF EXISTS `poll_visit_trending_hour`;
CREATE TABLE `poll_visit_trending_hour` (
  `poll_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`poll_id`),
  INDEX(`z_score`)
);
ALTER TABLE `poll_visit_trending_hour` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`) ON DELETE CASCADE;

DROP TABLE IF EXISTS `poll_visit_trending_day`;
CREATE TABLE `poll_visit_trending_day` (
  `poll_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`poll_id`),
  INDEX(`z_score`)
);
ALTER TABLE `poll_visit_trending_day` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`) ON DELETE CASCADE;

DROP TABLE IF EXISTS `poll_visit_trending_week`;
CREATE TABLE `poll_visit_trending_week` (
  `poll_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`poll_id`),
  INDEX(`z_score`)
);
ALTER TABLE `poll_visit_trending_week` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`) ON DELETE CASCADE;

DROP TABLE IF EXISTS `poll_visit_trending_month`;
CREATE TABLE `poll_visit_trending_month` (
  `poll_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`poll_id`),
  INDEX(`z_score`)
);
ALTER TABLE `poll_visit_trending_month` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`) ON DELETE CASCADE;

DROP TABLE IF EXISTS `poll_visit_trending_year`;
CREATE TABLE `poll_visit_trending_year` (
  `poll_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`poll_id`),
  INDEX(`z_score`)
);
ALTER TABLE `poll_visit_trending_year` ADD FOREIGN KEY (poll_id) REFERENCES `poll` (`id`) ON DELETE CASCADE;

-- the tag tables:

-- Create the trending tables and keys
DROP TABLE IF EXISTS `tag_visit_trending_hour`;
CREATE TABLE `tag_visit_trending_hour` (
  `tag_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  INDEX(`z_score`)
);
ALTER TABLE `tag_visit_trending_hour` ADD FOREIGN KEY (tag_id) REFERENCES `tag` (`id`) ON DELETE CASCADE;

DROP TABLE IF EXISTS `tag_visit_trending_day`;
CREATE TABLE `tag_visit_trending_day` (
  `tag_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  INDEX(`z_score`)
);
ALTER TABLE `tag_visit_trending_day` ADD FOREIGN KEY (tag_id) REFERENCES `tag` (`id`) ON DELETE CASCADE;

DROP TABLE IF EXISTS `tag_visit_trending_week`;
CREATE TABLE `tag_visit_trending_week` (
  `tag_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  INDEX(`z_score`)
);
ALTER TABLE `tag_visit_trending_week` ADD FOREIGN KEY (tag_id) REFERENCES `tag` (`id`) ON DELETE CASCADE;

DROP TABLE IF EXISTS `tag_visit_trending_month`;
CREATE TABLE `tag_visit_trending_month` (
  `tag_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  INDEX(`z_score`)
);
ALTER TABLE `tag_visit_trending_month` ADD FOREIGN KEY (tag_id) REFERENCES `tag` (`id`) ON DELETE CASCADE;

DROP TABLE IF EXISTS `tag_visit_trending_year`;
CREATE TABLE `tag_visit_trending_year` (
  `tag_id` INTEGER DEFAULT NULL,
  `hits` INTEGER NOT NULL DEFAULT '0',
  `avg_hits` double DEFAULT NULL,
  `std_hits` double DEFAULT NULL,
  `z_score` double DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  INDEX(`z_score`)
);
ALTER TABLE `tag_visit_trending_year` ADD FOREIGN KEY (tag_id) REFERENCES `tag` (`id`) ON DELETE CASCADE;


SET FOREIGN_KEY_CHECKS=1;


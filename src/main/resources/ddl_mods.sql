


-- Sample data
INSERT INTO `poll_type` (`id`,`name`) 
VALUES ('1','STV');

INSERT INTO `user` (`id`,`ip_address`) 
VALUES('1','test');

-- Discussion for sample poll
INSERT INTO `discussion` (`id`,`subject`,`text`) VALUES
('1','subject 1','blehrp');

-- A poll
INSERT INTO `poll` (`id`,`poll_type_id`,`discussion_id`,`user_id`) VALUES
('1','1','1','1');

-- Discussions for candidates
INSERT INTO `discussion` (`id`,`subject`,`text`) VALUES
('2','subject 2','asdf');
INSERT INTO `discussion` (`id`,`subject`,`text`) VALUES
('3','subject 3','sdfg');
INSERT INTO `discussion` (`id`,`subject`,`text`) VALUES
('4','subject 4','dfgh');
INSERT INTO `discussion` (`id`,`subject`,`text`) VALUES
('5','subject 5','fghj');

-- 4 candidates
INSERT INTO `candidate` (`id`,`poll_id`,`discussion_id`,`user_id`) VALUES
('1','1','2','1');

INSERT INTO `candidate` (`id`,`poll_id`,`discussion_id`,`user_id`) VALUES
('2','1','3','1');

INSERT INTO `candidate` (`id`,`poll_id`,`discussion_id`,`user_id`) VALUES
('3','1','4','1');

INSERT INTO `candidate` (`id`,`poll_id`,`discussion_id`,`user_id`) VALUES
('4','1','5','1');

-- Two Ballots(no single user restriction yet)
INSERT INTO `ballot` (`id`,`poll_id`,`user_id`) VALUES
('1','1','1');

INSERT INTO `ballot` (`id`,`poll_id`,`user_id`) VALUES
('2','1','1');

-- 2 ballot items per ballot
INSERT INTO `ballot_item` (`id`,`ballot_id`,`candidate_id`,`rank`) VALUES
('1','1','1','1');
INSERT INTO `ballot_item` (`id`,`ballot_id`,`candidate_id`,`rank`) VALUES
('2','1','2','2');
INSERT INTO `ballot_item` (`id`,`ballot_id`,`candidate_id`,`rank`) VALUES
('3','2','4','1');
INSERT INTO `ballot_item` (`id`,`ballot_id`,`candidate_id`,`rank`) VALUES
('4','2','2','2');

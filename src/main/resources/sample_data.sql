INSERT INTO `poll_type` (id, name)
	VALUES (1,'Range');
INSERT INTO `poll_sum_type` (id, name)
	VALUES (1,'Average'),(2,'Median'),(3,'Normalized');
INSERT INTO `user` (id, aid, ip_address) 
	VALUES (1,'1','test1'),(2,'2','test'),(3,'3','test3');
INSERT INTO `full_user` (id, user_id, name, email, password_encrypted) 
	VALUES (1,2, 'DerpyMcFish', 'asdf@gmail.com', 'testPass');
INSERT INTO `discussion` (`id`,`subject`,`text`)
	VALUES (1,'Who is the best Jedi?','###No alternate universe stuff\r\n\r\n **Got it?**\r\n\r\n'),
	(2,'Luke Skywalker saved his dad.... end of story','Not much more to say.'),
	(3,'Darth Vader - cmon, who hasn\'t he killed?','He killed the sidius. no one can beat that'),
	(4,'Mace windu.... but only because purple.','Can\'t stop the purple'),
	(5,'Yoda is my homeboy. Remember that part in episode two where he was flying around dooku like gangbustas? Sorry, no one can beat that.','### mmmmmk\r\n\r\nbada\r\n\r\n\r\nbing'),
	(6,'Which side of the force is better?',''),(7,'Does a wookie wear pants?',''),(8,'Why does the chewbacca wear pants?',''),
	(9,'Best force power?',''),(10,'Prequels or Sequals: Which and why',''),(11,'An untagged one',''),(12,'A user-only one','');
INSERT INTO `poll` (id,aid,poll_type_id,poll_sum_type_id,discussion_id,user_id,private_password,full_user_only) 
	VALUES (1,'1',1,1,1,1,NULL,0),(2,'2',1,1,6,2,NULL,0),(3,'3',1,1,7,3,NULL,0),
	(4,'4',1,1,8,3,NULL,0),(5,'5',1,1,9,3,NULL,0),(6,'6',1,1,10,1,NULL,0),(7,'7',1,1,11,2,'test',0),
	(8,'8',1,1,12,3,NULL,1);
INSERT INTO `candidate` (`id`,`poll_id`,`discussion_id`,`user_id`)
	VALUES (1,1,2,1),(2,1,3,1),(3,1,4,2),(4,1,5,3);
INSERT INTO `ballot` (`id`,`poll_id`,`user_id`,`candidate_id`,`rank`)
	VALUES (1,1,1,1,82),(5,1,1,2,20),(8,1,1,4,32),(9,1,2,2,38),(10,1,3,2,67);
INSERT INTO `tag` (`id`, `aid`, `user_id`, `name`) 
	VALUES (1,'1',1,'Star Trek'),(2,'2',2,'Star Wars'),(3,'3',3,'Lord of the Rings');
INSERT INTO `poll_tag` (`id`, `poll_id`, `tag_id`) 
	VALUES (1,1,3), (2,1,2),(3,2,2),(4,3,2),(5,4,2),(6,5,2),(7,6,2);
INSERT INTO `poll_visit` (`id`,`user_id`,`poll_id`,`created`)
	VALUES (1,1,1,'2016-02-08 14:22:54'), (2,2,2,'2016-01-08 14:22:54'),(3,1,3,'2016-02-07 14:22:54'),
	(4,1,4,'2016-02-07 14:22:54'),(5,2,5,'2016-02-07 14:22:54'),(6,1,6,'2016-02-07 14:22:54'),
	(7,3,7,'2016-02-07 14:22:54');
INSERT INTO `tag_visit` (`id`,`user_id`,`tag_id`,`created`)
	VALUES (1,1,1,'2016-02-08 14:22:54'),(2,3,2,'2016-01-08 14:22:54'),(3,3,3,'2016-02-07 14:22:54');

INSERT INTO `comment` (id,aid,discussion_id, text, user_id)
	VALUES (1,'1',1,'Level 1',1),(2,'2',1,'Level 2',2),(3,'3',1,'Level 2',3),(4,'4',1,'Level 3',2),(5,'5',1,'Level 4',1),
	(6,'6',1,'Level 1',2);
INSERT INTO `comment_tree` (parent_id, child_id, path_length) 
	VALUES 	(1,1,0) , (1,2,1) , (1,3,1) , (1,4,2) , (1,5,3)	,
			(2,2,0) , 
			(3,3,0) ,                     (3,4,1) ,	(3,5,2)	,
			(4,4,0) ,					  			(4,5,1)	,
			(5,5,0)	,
			(6,6,0)
			;
INSERT INTO `comment_rank` (comment_id, user_id, rank)
	VALUES (2,1,30),(1,2,60),(2,3,50),(3,2,75),(4,3,12),(4,1,81);



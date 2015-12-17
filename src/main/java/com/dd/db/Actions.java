package com.dd.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dd.db.Tables.*;

// http://ondras.zarovi.cz/sql/demo/?keyword=dd_tyhou

public class Actions {

	static final Logger log = LoggerFactory.getLogger(Actions.class);

	
	public static String createPoll(String subject, String text) {
		
		// First create a discussion
		Discussion d = DISCUSSION.createIt("subject", subject,
				"text", text);
		
		POLL.createIt("poll_type_id", 1,
				"discussion_id", d.getId().toString());
		
		return "Poll created";
		
		
	}
	
	public static String createCandidate(String pollId, String subject, String text) {
		
		// First create a discussion
		Discussion d = DISCUSSION.createIt("subject", subject,
				"text", text);
		
		CANDIDATE.createIt("poll_id", pollId,
				"discussion_id", d.getId().toString());
		
		return "Candidate created";
		
	}
	
	

}

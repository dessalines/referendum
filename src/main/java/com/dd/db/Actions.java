package com.dd.db;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

import com.dd.DataSources;
import com.dd.db.Tables.User;
import com.dd.db.Tables.UserView;
import com.dd.tools.Tools;
import com.dd.voting.ballot.RankedBallot;
import com.dd.voting.candidate.RankedCandidate;
import com.dd.voting.election.STVElection;
import com.dd.voting.election.STVElection.Quota;

import static com.dd.db.Tables.*;
import static com.dd.tools.Tools.ALPHA_ID;

// http://ondras.zarovi.cz/sql/demo/?keyword=dd_tyhou

public class Actions {

	static final Logger log = LoggerFactory.getLogger(Actions.class);

	public static String createEmptyPoll(String userId) {
		
		// First create a discussion
		Discussion d = DISCUSSION.createIt();
		
		Poll p = POLL.createIt("discussion_id", d.getId().toString(),
				"user_id", userId,
				"poll_type_id", 1);
		
		return p.getId().toString();
	}
	
	public static String savePoll(String pollId, String subject, String text, String password) {
		
		Poll p = POLL.findFirst("id = ?", pollId);
		
		if (password != null) {
			p.set("private_password", password).saveIt();
		}
		
		Discussion d = DISCUSSION.findFirst("id = ?", p.getString("discussion_id"));
		d.set("subject", subject,
				"text", text).saveIt();
		
		return "Poll Saved";
		
		
	}
	
	public static String createCandidate(String userId, String pollId, String subject, String text) {
		
		// First create a discussion
		Discussion d = DISCUSSION.createIt("subject", subject,
				"text", text);
		
		CANDIDATE.createIt("poll_id", pollId,
				"discussion_id", d.getId().toString());
		
		return "Candidate created";
		
	}

	public static String createBallot(String userId, String pollId, 
			Map<String, String> vars) {
		
		// TODO always just create a new ballot for now
		// In the future just get the current one if it doesn't exist
		Ballot b = BALLOT.createIt("poll_id", pollId,
				"user_id", userId);
		
		
		for (Entry<String, String> e: vars.entrySet()) {
			BALLOT_ITEM.createIt("ballot_id", b.getId(),
					"candidate_id", e.getKey(),
					"rank", e.getValue());
		}
		
		return "Ballot created";
		
		
	}

	public static String runSTVElection(String pollId) {
		
		
		// Get all the ballots for the poll
		List<BallotItemView> dbBallots = BALLOT_ITEM_VIEW.find("poll_id = ?", pollId);
		
		
		// Group them up by ballot id to ballot items
		Map<Integer, List<RankedCandidate>> ballotMap = new HashMap<>();
		for (BallotItemView dbBallot : dbBallots) {
			Integer ballotId = Integer.valueOf(dbBallot.getString("ballot_id"));
			
			RankedCandidate rc = new RankedCandidate(dbBallot.getInteger("candidate_id"), 
					dbBallot.getInteger("rank"));
			
			log.info(Tools.GSON2.toJson(rc));
			
			
			// The list needs to be created
			if (ballotMap.get(ballotId) == null) {
				List<RankedCandidate> rcs = new ArrayList<>();
				rcs.add(rc);
				ballotMap.put(ballotId, rcs);
			} else {
				List<RankedCandidate> rcs = ballotMap.get(ballotId);
				rcs.add(rc);
			}
			
		}
		
		// Add them to a list of ballots
		List<RankedBallot> ballots = new ArrayList<>();
		for (Entry<Integer, List<RankedCandidate>> e : ballotMap.entrySet()) {
			RankedBallot rb = new RankedBallot(e.getValue());
			ballots.add(rb);
			log.info("ballot id = " + e.getKey());
			log.info(Tools.GSON2.toJson(rb));
		}
		
		
		STVElection election = new STVElection(Quota.DROOP, ballots, 1);
		
		String json = Tools.GSON.toJson(election.getRounds());
		
		log.info(json);
		
		return json;
		
	}
	
	
	public static String setCookiesForLogin(UserView uv, Response res) {

		Integer expireSeconds = DataSources.EXPIRE_SECONDS;

//		long now = new Date().getTime();

//		long expireTime = now + expireSeconds*1000;

//		Timestamp expireTS = new Timestamp(expireTime);


		// Not sure if this is necessary yet
		Boolean secure = false;

		// Set some cookies for that users login
//		res.cookie("auth", auth, expireSeconds, secure);
		res.cookie("uid", uv.getId().toString(), expireSeconds, secure);
//		res.cookie("user_name", client.getString("name"), expireSeconds, secure);

		return "Logged in";



	}
	
	public static UserView getUserFromCookie(Request req, Response res) {
		
		String uid = req.cookie("uid");

		UserView uv = null;
		BigInteger id = null;

		Tools.dbInit();
		// If no cookie, fetch user by ip address
		if (uid == null) {
			uv = USER_VIEW.findFirst("ip_address = ?", req.ip());
			
			if (uv != null) {
				Actions.setCookiesForLogin(uv, res);
			}
			
		} else {
			id = ALPHA_ID.decode(uid);
			uv = USER_VIEW.findFirst("id = ?" , id);
		}

		if (uv == null) {
			User user = USER.createIt("ip_address", req.ip());
			uv = USER_VIEW.findFirst("id = ?", user.getId());
			Actions.setCookiesForLogin(uv, res);
		}
		Tools.dbClose();
		
		return uv;
	}
	
	

}

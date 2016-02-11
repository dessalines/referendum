package com.referendum.db;

import static com.referendum.db.Tables.*;
import static com.referendum.tools.Tools.ALPHA_ID;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.javalite.activejdbc.DBException;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.referendum.DataSources;
import com.referendum.db.Tables.Ballot;
import com.referendum.db.Tables.CommentView;
import com.referendum.db.Tables.Discussion;
import com.referendum.db.Tables.Poll;
import com.referendum.db.Tables.User;
import com.referendum.tools.Tools;
import com.referendum.voting.ballot.RangeBallot;
import com.referendum.voting.candidate.RangeCandidate;
import com.referendum.voting.candidate.RangeCandidateResult;
import com.referendum.voting.election.RangeElection;
import com.referendum.voting.voting_system.choice_type.RangeVotingSystem;
import com.referendum.voting.voting_system.choice_type.RangeVotingSystem.RangeVotingSystemType;

// http://ondras.zarovi.cz/sql/demo/?keyword=dd_tyhou

public class Actions {

	static final Logger log = LoggerFactory.getLogger(Actions.class);

	public static String createEmptyPoll(String userId) {

		// First create a discussion
		Discussion d = DISCUSSION.createIt();

		Poll p = POLL.createIt("discussion_id", d.getId().toString(),
				"user_id", userId,
				"poll_type_id", 1,
				"poll_sum_type_id", 1);
		p.set("aid", Tools.ALPHA_ID.encode(BigInteger.valueOf(p.getLong("id")))).saveIt();

		return p.getId().toString();
	}

	public static String savePoll(String userId, String pollId, 
			String subject, String text, String password,
			String pollSumTypeId, Boolean fullUsersOnly, Response res) {

		Poll p = POLL.findFirst("id = ? and user_id = ?", pollId, userId);

		if (p == null) {
			throw new NoSuchElementException("Wrong User");
		}

		p.set("private_password", password,
				"poll_sum_type_id", pollSumTypeId,
				"full_user_only", fullUsersOnly).saveIt();


		Discussion d = DISCUSSION.findFirst("id = ?", p.getString("discussion_id"));
		d.set("subject", subject,
				"text", text).saveIt();

		if (password != null) {
			res.removeCookie("poll_password_" + pollId);
			res.cookie("poll_password_" + pollId, password);
		}

		return "Poll Saved";


	}

	public static String unlockPoll(String pollId, String password, Response res) {

		Poll p = POLL.findFirst("id = ? ", pollId);


		if (password.equals(p.getString("private_password"))) {
			res.removeCookie("poll_password_" + pollId);
			res.cookie("poll_password_" + pollId, password);
		} else {
			throw new NoSuchElementException("Incorrect password");
		}

		return "Poll Unlocked";


	}

	public static String deletePoll(String userId, String pollId) {

		Poll p = POLL.findFirst("id = ? and user_id = ?", pollId, userId);

		if (p == null) {
			throw new NoSuchElementException("Wrong User");
		}

		Discussion d = DISCUSSION.findFirst("id = ?", p.getString("discussion_id"));
		d.delete();

		p.delete();



		return "Poll Deleted";


	}

	public static String deleteCandidate(String userId, String candidateId) {

		Candidate c = CANDIDATE.findFirst("id = ? and user_id = ?", candidateId, userId);

		if (c == null) {
			throw new NoSuchElementException("Wrong User");
		}

		Discussion d = DISCUSSION.findFirst("id = ?", c.getString("discussion_id"));
		d.delete();

		c.delete();

		return "Candidate Deleted";


	}

	public static String createCandidate(String userId, String pollId, String subject, String text) {

		// First create a discussion
		Discussion d = DISCUSSION.createIt("subject", subject,
				"text", text);

		CANDIDATE.createIt("poll_id", pollId,
				"discussion_id", d.getId().toString(),
				"user_id", userId);

		return "Candidate created";

	}

	public static String saveCandidate(String userId, String candidateId, String subject, String text) {

		// find the candidate
		Candidate c = CANDIDATE.findFirst("id = ? and user_id = ?", candidateId, userId);

		if (c == null) {
			throw new NoSuchElementException("Wrong User");
		}

		// Update the discussion
		Discussion d = DISCUSSION.findFirst("id = ?", c.getInteger("discussion_id"));

		d.set("subject", subject,
				"text", text).saveIt();


		return "Candidate updated";

	}

	public static String editComment(String userId, String commentId, String text) {

		// find the candidate
		Comment c = COMMENT.findFirst("id = ? and user_id = ?", commentId, userId);

		if (c == null) {
			throw new NoSuchElementException("Wrong User");
		}

		c.set("text", text).saveIt();


		return "Comment updated";

	}

	public static String deleteComment(String userId, String commentId) {

		// find the candidate
		Comment c = COMMENT.findFirst("id = ? and user_id = ?", commentId, userId);

		if (c == null) {
			throw new NoSuchElementException("Wrong User");
		}

		c.set("deleted", true).saveIt();


		return "Comment deleted";

	}

	public static String createComment(String userId, String discussionId, 
			List<String> parentBreadCrumbs, String text) {

		List<String> pbs = (parentBreadCrumbs != null) ? new ArrayList<String>(parentBreadCrumbs) : 
			new ArrayList<String>();


		// find the candidate
		Comment c = COMMENT.createIt("discussion_id", discussionId, 
				"text", text,
				"user_id", userId);
		c.set("aid", Tools.ALPHA_ID.encode(BigInteger.valueOf(c.getLong("id")))).saveIt();


		String childId = c.getId().toString();

		// This is necessary, because of the 0 path length to itself one
		pbs.add(childId);

		Collections.reverse(pbs);


		// Create the comment_tree
		for (int i = 0; i < pbs.size(); i++) {

			String parentId = pbs.get(i);

			// i is the path length
			COMMENT_TREE.createIt("parent_id", parentId,
					"child_id", childId,
					"path_length", i);
		}

		return "Comment created";

	}



	public static String saveBallot(String userId, String pollId, String candidateId,
			String rank) {

		String message = null;
		// fetch the vote if it exists
		Ballot b = BALLOT.findFirst("poll_id = ? and user_id = ? and candidate_id = ?", 
				pollId, 
				userId,
				candidateId);

		if (b == null) {
			if (rank != null) {
				b = BALLOT.createIt(
						"poll_id", pollId,
						"user_id", userId,
						"candidate_id", candidateId,
						"rank", rank);
				message = "Ballot Created";
			} else {
				message = "Ballot not created";
			}
		} else {
			if (rank != null) {
				b.set("rank", rank).saveIt();
				message = "Ballot updated";
			}
			// If the rank is null, then delete the ballot
			else {
				b.delete();
				message = "Ballot deleted";
			}
		}

		return message;

	}

	public static String saveCommentVote(String userId, String commentId, String rank) {

		String message = null;
		// fetch the vote if it exists
		CommentRank c = COMMENT_RANK.findFirst("user_id = ? and comment_id = ?", 
				userId, commentId);


		if (c == null) {
			if (rank != null) {
				c = COMMENT_RANK.createIt(
						"comment_id", commentId,
						"user_id", userId,
						"rank", rank);
				message = "Comment Vote Created";
			} else {
				message = "Comment Vote not created";
			}
		} else {
			if (rank != null) {
				c.set("rank", rank).saveIt();
				message = "Comment Vote updated";
			}
			// If the rank is null, then delete the ballot
			else {
				c.delete();
				message = "Comment Vote deleted";
			}
		}

		return message;

	}


	public static String setCookiesForLogin(FullUser fu, String auth, Response res) {
		Boolean secure = false;
		res.cookie("auth",null, 0);
		res.cookie("uid",null, 0);
		res.cookie("uaid",null, 0);
		res.cookie("username",null, 0);
		res.cookie("auth", auth, DataSources.EXPIRE_SECONDS, secure);
		res.cookie("uid", fu.getString("user_id"), DataSources.EXPIRE_SECONDS, secure);
		res.cookie("uaid", Tools.ALPHA_ID.encode(new BigInteger(fu.getString("user_id"))), 
				DataSources.EXPIRE_SECONDS, secure);
		res.cookie("username", fu.getString("name"), DataSources.EXPIRE_SECONDS, secure);

		return "Logged in";
	}

	public static String setCookiesForLogin(User user, String auth, Response res) {
		Boolean secure = false;
		res.cookie("auth",null, 0);
		res.cookie("uid",null, 0);
		res.cookie("uaid",null, 0);
		
		res.cookie("auth", auth, DataSources.EXPIRE_SECONDS, secure);
		res.cookie("uid", user.getId().toString(), DataSources.EXPIRE_SECONDS, secure);
		res.cookie("uaid", user.getString("aid"), DataSources.EXPIRE_SECONDS, secure);

		return "Logged in";
	}

	public static UserLoginView getUserFromCookie(Request req, Response res) {
		
		String auth = req.cookie("auth");
		
		UserLoginView uv = null;
		
		uv = USER_LOGIN_VIEW.findFirst("auth = ?" , auth);
		
		return uv;
		
	}
	
	
	public static UserLoginView getOrCreateUserFromCookie(Request req, Response res) {

		String auth = req.cookie("auth");
		
		UserLoginView uv = null;

		// If no cookie, fetch user by ip address
		if (auth == null) {

			User user = USER.findFirst("ip_address = ?", req.ip());

			// It found a user row
			if (user != null) {

				// See if you have a login that hasn't expired yet
				Login login = LOGIN.findFirst("user_id = ? and expire_time > ?", user.getId(),
						Tools.newCurrentTimestamp().toString());
//								log.info(Tools.newCurrentTimestamp().toString());
//								log.info(login.toJson(false));

			
				
				// It found a login item for that user row, so the cookie, like u shoulda
				if (login != null) {
					auth = login.getString("auth");
					Actions.setCookiesForLogin(user, auth, res);
				}
				
				// Need to login for that user and set the cookie
				else {
					
					auth = Tools.generateSecureRandom();
					login = LOGIN.createIt("user_id", user.getId(), 
							"auth", auth,
							"expire_time", Tools.newExpireTimestamp());

					Actions.setCookiesForLogin(user, auth, res);
				}

				// The login is either there or created now, so find it and return out
				uv = USER_LOGIN_VIEW.findFirst("auth = ?" , auth);
				return uv;
			}

		}
		// The auth cookie is there, so find the user from the login
		else {
			uv = USER_LOGIN_VIEW.findFirst("auth = ?" , auth);
		}

		// The user doesn't exist, so you need to create the user and login
		if (uv == null) {
			User user = USER.createIt("ip_address", req.ip());
			user.set("aid", Tools.ALPHA_ID.encode(BigInteger.valueOf(user.getLong("id")))).saveIt();
			auth = Tools.generateSecureRandom();
			LOGIN.createIt("user_id", user.getId(), 
					"auth", auth,
					"expire_time", Tools.newExpireTimestamp());

			uv = USER_LOGIN_VIEW.findFirst("auth = ?" , auth);
			Actions.setCookiesForLogin(user, auth, res);
		}

		return uv;
	}

	public static List<RangeBallot> convertDBBallots(List<Ballot> dbBallots) {
		List<RangeBallot> ballots = new ArrayList<>();


		for (Ballot dbBallot : dbBallots) {
			Integer candidateId = dbBallot.getInteger("candidate_id");
			Double rank = dbBallot.getDouble("rank");
			RangeBallot rb = new RangeBallot(new RangeCandidate(candidateId, rank));
			ballots.add(rb);
		}

		return ballots;
	}


	public static String rangePollResults(String pollId) {

		List<Ballot> dbBallots = BALLOT.find("poll_id = ?", pollId);

		List<RangeBallot> ballots = convertDBBallots(dbBallots);

		Poll p = POLL.findFirst("id = ?", pollId);


		RangeElection re = new RangeElection(Integer.valueOf(pollId),
				sumIdToRangeVotingSystemType().get(p.getInteger("poll_sum_type_id")), 
				ballots);


		return Tools.nodeToJson(Transformations.rangeResultsJson(re));

	}



	public static Map<Integer, RangeVotingSystemType> sumIdToRangeVotingSystemType() {
		Map<Integer, RangeVotingSystemType> map = new HashMap<>();

		map.put(1, RangeVotingSystemType.AVERAGE);
		map.put(2, RangeVotingSystemType.MEDIAN);
		map.put(3, RangeVotingSystemType.NORMALIZED);

		return map;
	}


	public static String fetchDiscussionComments(Integer userId, 
			Integer discussionId, Integer parentId) {
		List<CommentView> cvs = COMMENT_VIEW.findBySQL(COMMENT_VIEW_SQL(
				userId, discussionId, parentId));
		return commentObjectsToJson(cvs);
	}

	public static String fetchDiscussionComments(Integer userId, 
			Integer discussionId, Integer parentId, 
			Integer minPathLength, Integer maxPathLength) {
		List<CommentView> cvs = COMMENT_VIEW.findBySQL(COMMENT_VIEW_SQL(
				userId, discussionId, parentId, minPathLength, maxPathLength));
		return commentObjectsToJson(cvs);
	}

	public static String fetchDiscussionComments(Integer userId, Integer discussionId) {
		List<CommentView> cvs = COMMENT_VIEW.findBySQL(COMMENT_VIEW_SQL(
				userId, discussionId));
		return commentObjectsToJson(cvs);
	}

	public static String fetchComments(Integer userId, Integer commentId) {
		List<CommentView> cvs = COMMENT_VIEW.findBySQL(COMMENT_VIEW_SQL(
				userId, null, commentId));
		return commentObjectsToJson(cvs);
	}

	public static String commentObjectsToJson(List<CommentView> cvs) {
		//		return Tools.GSON.toJson(Transformations.convertCommentsToEmbeddedObjects(cvs));
		try {
			return Tools.MAPPER.writeValueAsString(Transformations.convertCommentsToEmbeddedObjects(cvs));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String login(String userOrEmail, String password, Response res) {

		// Find the user, then create a login for them

		FullUser fu = FULL_USER.findFirst("name = ? or email = ?", userOrEmail, userOrEmail);

		if (fu == null) {
			throw new NoSuchElementException("Incorrect user/email");
		} else {
			String encryptedPassword = fu.getString("password_encrypted");

			Boolean correctPass = Tools.PASS_ENCRYPT.checkPassword(password, encryptedPassword);

			if (correctPass) {

				String auth = Tools.generateSecureRandom();
				LOGIN.createIt("user_id", fu.getInteger("user_id"), 
						"auth", auth,
						"expire_time", Tools.newExpireTimestamp());

				return Actions.setCookiesForLogin(fu, auth, res);

			} else {
				throw new NoSuchElementException("Incorrect Password");
			}
		}

	}

	public static String signup(String userName, String password, String email, Request req, Response res) {

		// Find the user, then create a login for them

		FullUser fu = FULL_USER.findFirst("name = ? or email = ?", userName, userName);


		if (fu == null) {

			// Create the user and full user
			User user = USER.createIt("ip_address", req.ip());
			user.set("aid", Tools.ALPHA_ID.encode(BigInteger.valueOf(user.getLong("id")))).saveIt();

			log.info("encrypting the user password");
			String encryptedPassword = Tools.PASS_ENCRYPT.encryptPassword(password);
			fu = FULL_USER.createIt("user_id", user.getId(),
					"name", userName,
					"email", email,
					"password_encrypted", encryptedPassword);

			// now login that user
			String auth = Tools.generateSecureRandom();
			LOGIN.createIt("user_id", user.getId(), 
					"auth", auth,
					"expire_time", Tools.newExpireTimestamp());

			Actions.setCookiesForLogin(fu, auth, res);

			return "Logged in";


		} else {
			throw new NoSuchElementException("Username/Password already exists");
		}

	}

	public static void addPollVisit(String userId, Integer pollId) {
		POLL_VISIT.createIt("user_id", userId, 
				"poll_id", pollId);
	}

	public static void addTagVisit(String userId, Integer tagId) {
		TAG_VISIT.createIt("user_id", userId, 
				"tag_id", tagId);
	}

	public static String savePollTag(String userId, String pollId, String tagId, String newTagName) {

		String message = null;

		// If the tag isn't there, then add it
		if (tagId == null) {

			// Fetch to see if the tag exists first
			Tag tag = TAG.findFirst("name = ?", newTagName);

			if (tag == null) {
				tag = TAG.createIt("user_id", userId,
						"name", newTagName);
				tag.set("aid", Tools.ALPHA_ID.encode(BigInteger.valueOf(tag.getLong("id")))).saveIt();
				message = "New tag added";
			}
			
			tagId = tag.getId().toString();
			
		} else {
			message = "Tag added";
		}


		try {
			POLL_TAG.createIt("poll_id", pollId,
					"tag_id", tagId);
		} catch (DBException e) {
			throw new NoSuchElementException("Tag already added");
		}

		return message;
	}
	
	public static String clearTags(String userId, String pollId) {
		
		// Make sure its the correct user
		Poll p = POLL.findFirst("id = ? and user_id = ?", pollId, userId);
		
		if (p == null) {
			throw new NoSuchElementException("Wrong user");
		}
		
		
		POLL_TAG.delete("poll_id = ?", pollId);
		
		return "Tags deleted";
	}



}

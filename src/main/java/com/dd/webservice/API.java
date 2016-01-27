package com.dd.webservice;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.before;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Response;

import com.dd.db.Actions;
import com.dd.db.Tables.Poll;
import com.dd.db.Tables.User;
import com.dd.db.Tables.UserView;
import com.dd.tools.SampleData;
import com.dd.tools.Tools;
import com.dd.voting.ballot.RankedBallot;
import com.dd.voting.election.ElectionRound;
import com.dd.voting.election.STVElection;
import com.dd.voting.election.STVElection.Quota;

import static com.dd.db.Tables.*;
import static com.dd.tools.Tools.ALPHA_ID;



public class API {

	static final Logger log = LoggerFactory.getLogger(API.class);

	public static void setup() {

		// Get the user id
		get("get_user", (req, res) -> {

			try {

				Tools.dbInit();
				
				UserView uv = Actions.getUserFromCookie(req, res);

				return uv.getId().toString();

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});


		post("/create_empty_poll", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				
				Tools.dbInit();

				UserView uv = Actions.getUserFromCookie(req, res);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				

				String pollId = Actions.createEmptyPoll(uv.getId().toString());

				String pollAid = ALPHA_ID.encode(new BigInteger(pollId));

				return pollAid;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/save_poll", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				
				Tools.dbInit();
				
				UserView uv = Actions.getUserFromCookie(req, res);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				



				String subject = vars.get("subject");
				String text = vars.get("poll_text");
				String pollId = ALPHA_ID.decode(vars.get("poll_id")).toString();
				Boolean private_ = vars.get("public_radio").equals("private");
				String password = (private_) ? vars.get("private_password") : null;
				String pollSumTypeId = vars.get("sum_type_radio");
				//				log.info(text);

				String message = Actions.savePoll(uv.getId().toString(), pollId, 
						subject, text, password, pollSumTypeId);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/delete_poll/:pollId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				
				Tools.dbInit();

				UserView uv = Actions.getUserFromCookie(req, res);

				

				String pollId = ALPHA_ID.decode(req.params(":pollId")).toString();

				String message = Actions.deletePoll(uv.getId().toString(), pollId);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});




		post("/save_candidate", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Tools.dbInit();
				
				UserView uv = Actions.getUserFromCookie(req, res);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String subject = vars.get("subject");
				String text = vars.get("text");
				String pollId = vars.get("poll_id");
				String candidateId = vars.get("candidate_id");

				

				String message;
				if (candidateId == null) {
					message = Actions.createCandidate(uv.getId().toString(), pollId, subject, text);
				} else {
					message = Actions.saveCandidate(uv.getId().toString(), candidateId, subject, text);
				}



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_poll_candidates/:pollId", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);

				String pollId = ALPHA_ID.decode(req.params(":pollId")).toString();

				Tools.dbInit();

				String json = Tools.replaceNewlines(
						CANDIDATE_VIEW.find("poll_id = ?", 
								pollId).toJson(false));


				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});		

		post("/delete_candidate/:candidateId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				
				Tools.dbInit();

				UserView uv = Actions.getUserFromCookie(req, res);

			

				String candidateId = req.params(":candidateId");

				String message = Actions.deleteCandidate(uv.getId().toString(), candidateId);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/edit_comment", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				
				Tools.dbInit();

				UserView uv = Actions.getUserFromCookie(req, res);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String text = vars.get("text");
				String commentId = vars.get("comment_id");

				

				String message;

				message = Actions.editComment(uv.getId().toString(), commentId, text);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/delete_comment/:commentId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				
				Tools.dbInit();

				UserView uv = Actions.getUserFromCookie(req, res);

				String commentId = req.params(":commentId");

				String message;

				message = Actions.deleteComment(uv.getId().toString(), commentId);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		post("/create_comment", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				
				Tools.dbInit();

				UserView uv = Actions.getUserFromCookie(req, res);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String text = vars.get("text");
				String discussionId = vars.get("discussion_id");
				List<String> parentBreadCrumbs = Arrays.asList(vars.get("parent_bread_crumbs").split(","));
				
				

				String message;

				message = Actions.createComment(uv.getId().toString(), discussionId, 
						parentBreadCrumbs, text);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});




		get("/get_poll/:pollId", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);


				String pollId = ALPHA_ID.decode(req.params(":pollId")).toString();

				Tools.dbInit();

				String json = Tools.replaceNewlines(POLL_VIEW.findFirst("id = ?", pollId).toJson(false));

				log.info(json);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});



		get("/get_user_poll_votes/:pollId", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);
				
				Tools.dbInit();

				UserView uv = Actions.getUserFromCookie(req, res);

				String pollId = ALPHA_ID.decode(req.params(":pollId")).toString();

				

				String json = BALLOT_VIEW.find("poll_id = ? and user_id = ?",
						pollId, uv.getId().toString()).toJson(false);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		get("/get_poll_results/:pollId", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);

				String pollId = ALPHA_ID.decode(req.params(":pollId")).toString();

				Tools.dbInit();

				String json = Actions.rangePollResults(pollId);

				log.info(json);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		get("/get_comments/:pollId", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);
				
				Tools.dbInit();

				UserView uv = Actions.getUserFromCookie(req, res);

				String pollId = ALPHA_ID.decode(req.params(":pollId")).toString();

				

				Integer discussionId = POLL.findFirst("id = ?", pollId).getInteger("discussion_id");

				String json = Actions.fetchDiscussionComments(uv.getInteger("id"), discussionId);

				log.info(json);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		post("/save_ballot/:pollId/:candidateId/:rank", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				
				Tools.dbInit();
				
				UserView uv = Actions.getUserFromCookie(req, res);

				String pollId = ALPHA_ID.decode(req.params(":pollId")).toString();
				String candidateId = req.params(":candidateId");
				String rank = req.params(":rank");
				if (rank.equals("null")) {
					rank = null;
				}

				

				String message = Actions.saveBallot(uv.getId().toString(), pollId, candidateId, rank);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});


		post("/save_comment_vote/:commentId/:rank", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				
				Tools.dbInit();
				
				UserView uv = Actions.getUserFromCookie(req, res);

				String commentId = req.params(":commentId");
				String rank = req.params(":rank");
				if (rank.equals("null")) {
					rank = null;
				}

				

				String message = Actions.saveCommentVote(uv.getId().toString(), commentId, rank);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});










	}

}
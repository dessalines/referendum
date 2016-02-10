package com.dd.webservice;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.before;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.Paginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Response;

import com.dd.db.Actions;
import com.dd.db.Tables.Poll;
import com.dd.db.Tables.User;
import com.dd.db.Tables.UserLoginView;
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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

				return uv.toJson(false);

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});
		
		get("get_user_info/:userAid", (req, res) -> {

			try {

				Tools.dbInit();

				String userId = ALPHA_ID.decode(req.params(":userAid")).toString();
				
				String json = USER_VIEW.findFirst("id = ?", userId).toJson(false);
				
				return json;

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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());





				String subject = vars.get("subject");
				String text = vars.get("poll_text");
				String pollId = ALPHA_ID.decode(vars.get("poll_id")).toString();
				Boolean private_ = vars.get("public_radio").equals("private");
				String password = (private_) ? vars.get("private_password") : null;
				String pollSumTypeId = vars.get("sum_type_radio");
				Boolean fullUsersOnly = (vars.get("full_users_only") != null) ? true : false;

				String message = Actions.savePoll(uv.getId().toString(), pollId, 
						subject, text, password, pollSumTypeId, fullUsersOnly, res);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/unlock_poll", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);


				Tools.dbInit();

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String password = vars.get("password");
				String pollId = ALPHA_ID.decode(vars.get("poll_id")).toString();


				String message = Actions.unlockPoll(pollId, password, res);

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

				UserLoginView uv = Actions.getUserFromCookie(req, res);



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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String subject = vars.get("subject");
				String text = vars.get("text");
				String pollId = ALPHA_ID.decode(vars.get("poll_id")).toString();
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
								pollId).orderBy("avg_rank desc").toJson(false));


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

				UserLoginView uv = Actions.getUserFromCookie(req, res);



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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String text = vars.get("text");
				String discussionId = vars.get("discussion_id");
				String parentCommentId = vars.get("parent_comment_id");

				List<String> parentBreadCrumbs = null;
				if (parentCommentId != null) {
					// Fetch the parent breadCrumbs from scratch(required for comment pages)
					parentBreadCrumbs = Arrays.asList(COMMENT_VIEW.findFirst("id = ?", parentCommentId)
							.getString("breadcrumbs").split(","));
				}

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




		get("/get_poll/:pollAid", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);



				Integer pollId = ALPHA_ID.decode(req.params(":pollAid")).intValue();

				Tools.dbInit();

				UserLoginView uv = Actions.getUserFromCookie(req, res);

				String json = Tools.replaceNewlines(POLL_VIEW.findFirst("id = ?", pollId).toJson(false));

				Actions.addPollVisit(uv.getId().toString(), pollId);

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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

				String pollId = ALPHA_ID.decode(req.params(":pollId")).toString();



				Integer discussionId = POLL.findFirst("id = ?", pollId).getInteger("discussion_id");

				String json = Actions.fetchDiscussionComments(uv.getInteger("id"), discussionId);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		get("/get_comment/:commentId", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();

				UserLoginView uv = Actions.getUserFromCookie(req, res);

				Integer commentId = ALPHA_ID.decode(req.params(":commentId")).intValue();

				String json = Actions.fetchComments(uv.getInteger("id"), commentId);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		get("/get_comment_parent/:commentId", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();

				Integer commentId = ALPHA_ID.decode(req.params(":commentId")).intValue();

				// Fetch the parent breadCrumbs from scratch(required for comment pages)
				List<String> parentBreadCrumbs = Arrays.asList(COMMENT_VIEW.findFirst("id = ?", commentId)
						.getString("breadcrumbs").split(","));


				String parentAid = "-1";
				if (parentBreadCrumbs.size() > 1) {
					String parentId = parentBreadCrumbs.get(parentBreadCrumbs.size() - 2);
					parentAid = ALPHA_ID.encode(new BigInteger(parentId));
				}

				log.info(parentAid);

				return parentAid;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		get("/get_trending_polls/:tagAid/:userAid/:order/:pageSize/:startIndex", (req, res) -> {

			try {
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();

				String tagAid = req.params(":tagAid");
				String tagId = (tagAid.equals("all")) ? "all" : ALPHA_ID.decode(tagAid).toString();
				String userAid = req.params(":userAid");
				String userId = (userAid.equals("all")) ? "all" : ALPHA_ID.decode(userAid).toString();
				String order = req.params(":order");
				Integer pageSize = Integer.valueOf(req.params(":pageSize"));
				Integer startIndex = Integer.valueOf(req.params(":startIndex"));
				

				Paginator polls = null;

				if (tagId.equals("all") && userId.equals("all")) {
					polls = new Paginator<PollView>(PollView.class,
							pageSize, 
							"subject is not null").
							orderBy(order + " desc");
				} else if (tagId.equals("all") && !userId.equals("all")){
					polls = new Paginator<PollView>(PollView.class, 
							pageSize, 
							"subject is not null and user_id = ?", userId).
							orderBy(order + " desc");
				} else if (!tagId.equals("all") && userId.equals("all")){
					polls = new Paginator<PollUngroupedView>(PollUngroupedView.class, 
							pageSize, 
							"subject is not null and tag_id = ?", tagId).
							orderBy(order + " desc");
				} else {
					polls = new Paginator<PollView>(PollView.class, 
							pageSize, 
							"subject is not null and user_id = ? and tag_id = ?", userId, tagId).
							orderBy(order + " desc");
				}

				Integer pageNum = (startIndex/pageSize)+1;

				String json = Tools.wrapPaginatorArray(
						Tools.replaceNewlines(polls.getPage(pageNum).toJson(false)),
								polls.getCount());

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_trending_tags/:order/:pageSize/:startIndex", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();

				String order = req.params(":order");
				Integer pageSize = Integer.valueOf(req.params(":pageSize"));
				Integer startIndex = Integer.valueOf(req.params(":startIndex"));

				Paginator tags = null;
				
				tags = new Paginator<TagView>(TagView.class,
						pageSize, 
						"1=?", "1").
						orderBy(order + " desc");

				Integer pageNum = (startIndex/pageSize)+1;

				String json = tags.getPage(pageNum).toJson(false);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_poll_tags/:pollAid", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();

				String pollId = ALPHA_ID.decode(req.params(":pollAid")).toString();

				String json = POLL_TAG_VIEW.find("poll_id = ?", pollId).toJson(false);

				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});
		
		get("/get_tag/:tagAid", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);

				Tools.dbInit();
				
				UserLoginView uv = Actions.getUserFromCookie(req, res);

				String tagAid = req.params(":tagAid");
				Integer tagId = Tools.ALPHA_ID.decode(tagAid).intValue();
				
				Actions.addTagVisit(uv.getId().toString(), tagId);

				String json = TAG_VIEW.findFirst("id = ?", tagId).toJson(false);

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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

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

				UserLoginView uv = Actions.getUserFromCookie(req, res);

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

		get("/tag_search/:query", (req, res) -> {

			try {

				Tools.allowAllHeaders(req, res);
				Tools.dbInit();

				String query = req.params(":query");

				String json = null;

				String queryStr = constructQueryString(query, "name");


				json = TAG_VIEW.find(queryStr.toString()).limit(5).orderBy("day_score desc").toJson(false);

				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});
		
		get("/poll_search/:query", (req, res) -> {

			try {

				Tools.allowAllHeaders(req, res);
				Tools.dbInit();

				String query = req.params(":query");

				String json = null;

				String queryStr = constructQueryString(query, "subject");

				json = POLL_VIEW.find(queryStr.toString()).limit(5).orderBy("day_score desc").toJson(false, 
						"aid", "subject", "day_hits");

				return json;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		post("/save_poll_tag", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Tools.dbInit();

				UserLoginView uv = Actions.getUserFromCookie(req, res);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String pollId = ALPHA_ID.decode(vars.get("poll_id")).toString();
				String tagId = vars.get("tag_id");
				String tagName = vars.get("tag_name");

				String message = Actions.savePollTag(uv.getId().toString(), pollId, tagId, tagName);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/clear_tags/:pollId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Tools.dbInit();

				UserLoginView uv = Actions.getUserFromCookie(req, res);
				String pollId = ALPHA_ID.decode(req.params(":pollId")).toString();

				String message = Actions.clearTags(uv.getId().toString(), pollId);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});



		post("/login", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Tools.dbInit();

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String userOrEmail = vars.get("user_or_email");
				String password = vars.get("password");

				String message = Actions.login(userOrEmail, password, res);

				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/signup", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Tools.dbInit();

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String userName = vars.get("username");
				String password = vars.get("password");
				String email = vars.get("email");

				String message = Actions.signup(userName, password, email, req, res);

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

	public static String constructQueryString(String query, String columnName) {

		try {
			query = java.net.URLDecoder.decode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] splitWords = query.split(" ");
		StringBuilder queryStr = new StringBuilder();

		for(int i = 0;;) {
			String word = splitWords[i++].replaceAll("'", "_");

			String likeQuery = columnName + " like '%" + word + "%'";

			queryStr.append(likeQuery);

			if (i < splitWords.length) {
				queryStr.append(" and ");
			} else {
				break;
			}
		}

		return queryStr.toString();

	}

}
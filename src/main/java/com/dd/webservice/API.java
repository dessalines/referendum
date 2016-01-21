package com.dd.webservice;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.before;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.db.Actions;
import com.dd.db.Tables.Poll;
import com.dd.db.Tables.User;
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
		before((req, res) -> {
            String uid = req.cookie("uid");
            BigInteger id = ALPHA_ID.decode(uid);
            
            UserView uv = USER_VIEW.findFirst("id = ?" , id);
           
            if (uv == null) {
            	// TODO use an IP hash instead
            	User user = USER.createIt("ip_address", req.ip());
            	uv = USER_VIEW.findFirst("id = ?", user.getId());
            }
            
            // Set the user attribute
            req.attribute("user", uv);

        });
		
		
		post("/create_poll", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				
				UserView uv = req.attribute("user");

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				
				String subject = vars.get("subject");
				String text = vars.get("text");
				String password = vars.get("password");

				Tools.dbInit();

				String message = Actions.createPoll(uv.getId().toString(), subject, text, password);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		post("/create_candidate/:pollId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				UserView uv = req.attribute("user");

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String subject = vars.get("subject");
				String text = vars.get("text");
				String pollId = req.params(":pollId");

				Tools.dbInit();

				String message = Actions.createCandidate(uv.getId().toString(), pollId, subject, text);



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


				String pollId = req.params(":pollId");

				Tools.dbInit();

				String json = CANDIDATE_VIEW.find("poll_id = ?", pollId).toJson(false);


				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});

		post("/create_ballot/:pollId", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);
				
				UserView uv = req.attribute("user");

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());
				
				String pollId = req.params(":pollId");

				Tools.dbInit();

				String message = Actions.createBallot(uv.getId().toString(), pollId, vars);



				return message;

			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}

		});

		get("/get_stv_election/:pollId", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);


				String pollId = req.params(":pollId");

				Tools.dbInit();

				String json = Actions.runSTVElection(pollId);


				return json;
			} catch (Exception e) {
				res.status(666);
				e.printStackTrace();
				return e.getMessage();
			} finally {
				Tools.dbClose();
			}


		});
		
		
		get("/get_sample_stv_election", (req, res) -> {

			try {	
				Tools.allowAllHeaders(req, res);



				Tools.dbInit();
				
				List<RankedBallot> ballots = SampleData.setupBallots();

				STVElection stv = new STVElection(Quota.DROOP, ballots, 3);

				List<ElectionRound> rounds = stv.getRounds();
				
				
				String json = Tools.GSON.toJson(rounds);
				
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








	}

}
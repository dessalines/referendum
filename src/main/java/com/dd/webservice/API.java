package com.dd.webservice;

import static spark.Spark.get;
import static spark.Spark.post;


import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.dd.db.Actions;
import com.dd.db.Tables.Poll;
import com.dd.tools.Tools;

import static com.dd.db.Tables.*;



public class API {

	static final Logger log = LoggerFactory.getLogger(API.class);


	public static void setup() {

		post("/create_poll", (req, res) -> {
			try {
				Tools.allowAllHeaders(req, res);
				Tools.logRequestInfo(req);

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String subject = vars.get("subject");
				String text = vars.get("text");

				Tools.dbInit();

				String message = Actions.createPoll(subject, text);



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

				Map<String, String> vars = Tools.createMapFromAjaxPost(req.body());

				String subject = vars.get("subject");
				String text = vars.get("text");
				String pollId = req.params(":pollId");

				Tools.dbInit();

				String message = Actions.createCandidate(pollId, subject, text);



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
		
		
		
		


	}

}
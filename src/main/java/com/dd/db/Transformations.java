package com.dd.db;

import static com.dd.db.Tables.BALLOT;
import static com.dd.db.Tables.CANDIDATE_VIEW;
import static com.dd.db.Tables.POLL;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.dd.db.Tables.Ballot;
import com.dd.db.Tables.CandidateView;
import com.dd.db.Tables.CommentView;
import com.dd.db.Tables.Poll;
import com.dd.tools.Tools;
import com.dd.voting.ballot.RangeBallot;
import com.dd.voting.candidate.RangeCandidateResult;
import com.dd.voting.election.RangeElection;

public class Transformations {


	public static ObjectNode rangeResultsJson(RangeElection re) {

		
		List<CandidateView> allCandidates = CANDIDATE_VIEW.find("poll_id = ?", re.getId());
		
		Map<Integer, CandidateView> cMap = new HashMap<>();
		for (CandidateView c : allCandidates) {
			cMap.put(c.getInteger("id"), c);
		}
		

		ObjectNode a = Tools.MAPPER.createObjectNode();

		ArrayNode an = a.putArray("results");

		for (RangeCandidateResult rank : re.getRankings()) {

			ObjectNode on = Tools.MAPPER.valueToTree(rank);
			JsonNode cm = Tools.jsonToNode(Tools.replaceNewlines(cMap.get(rank.getId()).toJson(false)));
			on.put("candidate_obj", cm);
			
			an.add(on);

		}


		return a;

	}
	
	public static List<CommentObj> convertCommentsToEmbeddedObjects(List<CommentView> cvs) {
		List<CommentObj> cos = new ArrayList<>();
		for (CommentView cv : cvs) {

			Integer id = cv.getInteger("id");

			// Create the comment object
			CommentObj co = new CommentObj(id, 
					cv.getInteger("discussion_id"), 
					cv.getInteger("user_id"), 
					cv.getDouble("avg_rank"),
					cv.getInteger("user_rank"), 
					cv.getString("text"), 
					cv.getString("breadcrumbs"), 
					cv.getTimestamp("created"), 
					cv.getTimestamp("modified"));

			// If there's no parent, add it to the top level
			if (co.getParentId() == null) {
				cos.add(co);
			} 
			else {

				// Get the correct level
				CommentObj topParent = CommentObj.findInEmbeddedById(cos, co.getTopParentId());

				CommentObj parentObj = topParent;
				
				// If its down more than a single level, you have to do a recursive loop to 
				// fetch the correct parent object
				if (co.getBreadCrumbsList().size() > 2) {
					for (int i = 1; i < co.getBreadCrumbsList().size() - 1; i++) {
						Integer parent = co.getBreadCrumbsList().get(i);
						parentObj = parentObj.findInEmbeddedById(parent);
					}
				}
				
				// Add this commentobj to the embedded comments of its parent
				parentObj.getEmbedded().add(co);
			}

		}

		return cos;
	}

	public static class CommentObj {
		private Integer id, discussionId, userId, userRank, parentId, topParentId;
		private Double avgRank;
		private String text;
		private Timestamp created, modified;
		private List<CommentObj> embedded;

		private List<Integer> breadCrumbsList;

		public CommentObj(Integer id, Integer discussionId, Integer userId,
				Double avgRank, Integer userRank, String text, String breadCrumbs,
				Timestamp created, Timestamp modified) {
			this.id = id;
			this.discussionId = discussionId;
			this.userId = userId;
			this.avgRank = avgRank;
			this.userRank = userRank;
			if (this.userRank == null) {
				this.userRank = -1;
			}
			if (this.avgRank == null) {
				this.avgRank = -1.0D;
			}
			
			this.text = text;
			this.created = created;
			this.modified = modified;
			embedded = new ArrayList<>();
			setBreadCrumbsArr(breadCrumbs);
			setParentId();

		}

		public CommentObj findInEmbeddedById(Integer id) {
			return findInEmbeddedById(embedded, id);
		}

		public static CommentObj findInEmbeddedById(List<CommentObj> cos, Integer id) {
			for (CommentObj c : cos) {
				if (c.getId() == id) {
					return c;
				}
			}
			return null;
		}

		private void setBreadCrumbsArr(String breadCrumbs) {
			breadCrumbsList = new ArrayList<>();
			for (String br : breadCrumbs.split(",")) {
				breadCrumbsList.add(Integer.valueOf(br));
			}
		}

		private void setParentId() {
			Integer cIndex = breadCrumbsList.indexOf(id);

			if (cIndex > 0) {
				parentId = breadCrumbsList.get(cIndex - 1);
			}

			topParentId = breadCrumbsList.get(0);

		}

		public List<CommentObj> getEmbedded() {
			return embedded;
		}

		public Integer getId() {
			return id;
		}

		public Integer getDiscussionId() {
			return discussionId;
		}

		public Integer getUserId() {
			return userId;
		}

		public Integer getParentId() {
			return parentId;
		}

		public Integer getTopParentId() {
			return topParentId;
		}

		public Double getAvgRank() {
			return avgRank;
		}

		public String getText() {
			return text;
		}

		public Timestamp getCreated() {
			return created;
		}

		public Timestamp getModified() {
			return modified;
		}

		public List<Integer> getBreadCrumbsList() {
			return breadCrumbsList;
		}

		public Integer getUserRank() {
			return userRank;
		}



	}
}

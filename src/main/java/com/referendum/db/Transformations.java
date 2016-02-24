package com.referendum.db;

import static com.referendum.db.Tables.BALLOT;
import static com.referendum.db.Tables.CANDIDATE_VIEW;
import static com.referendum.db.Tables.POLL;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.referendum.db.Tables.Ballot;
import com.referendum.db.Tables.CandidateView;
import com.referendum.db.Tables.CommentView;
import com.referendum.db.Tables.Poll;
import com.referendum.tools.Tools;
import com.referendum.voting.ballot.RangeBallot;
import com.referendum.voting.candidate.RangeCandidateResult;
import com.referendum.voting.candidate.RankedCandidate;
import com.referendum.voting.election.RangeElection;

public class Transformations {

	
	static final Logger log = LoggerFactory.getLogger(Transformations.class);

	
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

			// Create the comment object
			CommentObj co = new CommentObj(cv.getInteger("id"), 
					cv.getString("aid"),
					cv.getInteger("discussion_id"), 
					cv.getInteger("poll_id"), 
					cv.getString("poll_aid"),
					cv.getInteger("user_id"), 
					cv.getString("user_aid"),
					cv.getString("user_name"),
					cv.getBoolean("deleted"),
					cv.getBoolean("read"), 
					cv.getDouble("avg_rank"),
					cv.getInteger("user_rank"), 
					cv.getString("text"), 
					cv.getString("breadcrumbs"), 
					cv.getTimestamp("created"), 
					cv.getTimestamp("modified"));
			
//			log.info(co.getModified());
			
			// If there's no parent, add it to the top level
			if (co.getParentId() == null) {
				cos.add(co);
			}
			else {

				// Get the correct level
				CommentObj topParent = CommentObj.findInEmbeddedById(cos, co);

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
				
				Collections.sort(parentObj.getEmbedded(), new CommentObj.CommentObjComparator());
			}
			
			

		}
		
		Collections.sort(cos, new CommentObj.CommentObjComparator());

		return cos;
	}

	public static class CommentObj {
		private Integer id, discussionId, pollId, userId, userRank, parentId, topParentId;
		private Double avgRank;
		private String aid, pollAid, userAid, text, userName;
		private Timestamp created, modified;
		private List<CommentObj> embedded;
		private Boolean deleted, read;

		private List<Integer> breadCrumbsList;

		public CommentObj(Integer id, String aid, Integer discussionId, 
				Integer pollId, String pollAid, Integer userId, String userAid, 
				String userName, Boolean deleted, Boolean read,
				Double avgRank, Integer userRank, String text, String breadCrumbs,
				Timestamp created, Timestamp modified) {
			this.id = id;
			this.aid = aid;
			this.discussionId = discussionId;
			this.pollId = pollId;
			this.pollAid = pollAid;
			this.userId = userId;
			this.userAid = userAid;
			this.userName = userName;
			this.deleted = deleted;
			this.read = read;
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
		
		@Override
		public String toString() {
			return "id : " + id;
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
		
		public static CommentObj findInEmbeddedById(List<CommentObj> cos, CommentObj co) {
			Integer id = co.getTopParentId();
			
			for (CommentObj c : cos) {
				if (c.getId() == id) {
					return c;
				}
			}
			
			return co;

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
		
		public static class CommentObjComparator implements Comparator<CommentObj> {

			@Override
			public int compare(CommentObj o1, CommentObj o2) {
				return o2.getAvgRank().compareTo(o1.getAvgRank());
			}

		}

		public Integer getDiscussionId() {
			return discussionId;
		}
		
		public Integer getPollId() {
			return pollId;
		}

		public Integer getUserId() {
			return userId;
		}
		
		public String getUserName() {
			return userName;
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
		
		public Boolean getDeleted() {
			return deleted;
		}
		
		public Boolean getRead() {
			return read;
		}
		
		public String getAid() {
			return aid;
		}

		public String getPollAid() {
			return pollAid;
		}

		public String getUserAid() {
			return userAid;
		}



	}
}

package com.dd.db;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

public class Tables {



	@Table("poll")
	public static class Poll extends Model {}
	public static final Poll POLL = new Poll();

	@Table("poll_view")
	public static class PollView extends Model {}
	public static final PollView POLL_VIEW = new PollView();

	@Table("poll_type")
	public static class PollType extends Model {}
	public static final PollType POLL_TYPE = new PollType();

	@Table("poll_tag")
	public static class PollTag extends Model {}
	public static final PollTag POLL_TAG = new PollTag();

	@Table("tag")
	public static class Tag extends Model {}
	public static final Tag TAG = new Tag();

	@Table("candidate")
	public static class Candidate extends Model {}
	public static final Candidate CANDIDATE = new Candidate();

	@Table("candidate_view")
	public static class CandidateView extends Model {}
	public static final CandidateView CANDIDATE_VIEW = new CandidateView();

	@Table("ballot")
	public static class Ballot extends Model {}
	public static final Ballot BALLOT = new Ballot();

	@Table("ballot_view")
	public static class BallotView extends Model {}
	public static final BallotView BALLOT_VIEW = new BallotView();

	@Table("discussion")
	public static class Discussion extends Model {}
	public static final Discussion DISCUSSION = new Discussion();

	@Table("comment")
	public static class Comment extends Model {}
	public static final Comment COMMENT = new Comment();

	@Table("comment_view")
	public static class CommentView extends Model {}
	public static final CommentView COMMENT_VIEW = new CommentView();

	@Table("comment_tree")
	public static class CommentTree extends Model {}
	public static final CommentTree COMMENT_TREE = new CommentTree();

	@Table("user")
	public static class User extends Model {}
	public static final User USER = new User();

	@Table("user_view")
	public static class UserView extends Model {}
	public static final UserView USER_VIEW = new UserView();

	public static final String COMMENT_VIEW_SQL(Integer userId, Integer discussionId, 
			Integer parentId, Integer minPathLength, Integer maxPathLength) {
		String s = "select \n"+
				"comment.id,\n"+
				"comment.discussion_id,\n"+
				"text,\n"+
				"comment.user_id,\n"+
				"-- min(a.path_length,b.path_length),\n"+
				"AVG(c.rank) as avg_rank,\n"+
				"d.rank as user_rank,\n"+
				"a.parent_id as parent_id,\n"+
				"GROUP_CONCAT(distinct b.parent_id order by b.path_length desc) AS breadcrumbs,\n"+
				"max(a.parent_id) as derp_id,\n"+
				"comment.created,\n"+
				"comment.modified\n"+
				"from comment\n"+
				"JOIN comment_tree a ON (comment.id = a.child_id) \n"+
				"JOIN comment_tree b ON (b.child_id = a.child_id) \n"+
				"left join comment_rank c \n"+
				"on comment.id = c.comment_id\n"+
				"left join comment_rank d \n"+
				"on comment.id = d.comment_id\n" + 
				"and d.user_id = " + userId + "\n"+
				"WHERE comment.discussion_id = " + discussionId + "\n";
		if (parentId != null) {
			s += "and a.parent_id = " + parentId + " \n";
		}


		if (minPathLength != null) {
			s += "and a.path_length >= " + minPathLength + " \n";
		}
		if (maxPathLength != null) {
			s += "and a.path_length <= " + maxPathLength + " \n";
		}


		s+="GROUP BY a.child_id;";

		return s;
	}
	
	public static final String COMMENT_VIEW_SQL(Integer userId, Integer discussionId) {
		return COMMENT_VIEW_SQL(userId, discussionId, null, null, null);
	}

	public static final String COMMENT_VIEW_SQL(Integer userId, Integer discussionId, Integer parentId) {
		return COMMENT_VIEW_SQL(userId, discussionId, parentId, null, null);
	}


}

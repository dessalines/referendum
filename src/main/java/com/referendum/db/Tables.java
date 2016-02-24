package com.referendum.db;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

public class Tables {



	@Table("poll")
	public static class Poll extends Model {}
	public static final Poll POLL = new Poll();

	@Table("poll_view")
	public static class PollView extends Model {}
	public static final PollView POLL_VIEW = new PollView();
	
	@Table("poll_ungrouped_view")
	public static class PollUngroupedView extends Model {}
	public static final PollUngroupedView POLL_UNGROUPED_VIEW = new PollUngroupedView();

	@Table("poll_type")
	public static class PollType extends Model {}
	public static final PollType POLL_TYPE = new PollType();

	@Table("poll_tag")
	public static class PollTag extends Model {}
	public static final PollTag POLL_TAG = new PollTag();
	
	@Table("poll_tag_view")
	public static class PollTagView extends Model {}
	public static final PollTagView POLL_TAG_VIEW = new PollTagView();
	
	@Table("poll_visit")
	public static class PollVisit extends Model {}
	public static final PollVisit POLL_VISIT = new PollVisit();

	@Table("tag")
	public static class Tag extends Model {}
	public static final Tag TAG = new Tag();
	
	@Table("tag_view")
	public static class TagView extends Model {}
	public static final TagView TAG_VIEW = new TagView();
	
	@Table("tag_visit")
	public static class TagVisit extends Model {}
	public static final TagVisit TAG_VISIT = new TagVisit();

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

	@Table("comment_rank")
	public static class CommentRank extends Model {}
	public static final CommentRank COMMENT_RANK = new CommentRank();

	@Table("user")
	public static class User extends Model {}
	public static final User USER = new User();
	
	@Table("user_view")
	public static class UserView extends Model {}
	public static final UserView USER_VIEW = new UserView();
	
	@Table("full_user")
	public static class FullUser extends Model {}
	public static final FullUser FULL_USER = new FullUser();

	@Table("user_login_view")
	public static class UserLoginView extends Model {}
	public static final UserLoginView USER_LOGIN_VIEW = new UserLoginView();
	
	@Table("login")
	public static class Login extends Model {}
	public static final Login LOGIN = new Login();

	public static final String COMMENT_VIEW_SQL(Integer userId, Integer discussionId, 
			Integer parentId, Integer minPathLength, Integer maxPathLength, 
			String orderBy, Integer commentUserId, Integer parentUserId, 
			Integer pageNum, Integer pageSize, Boolean read) {
		StringBuilder s = new StringBuilder("select \n"+
				"comment.id,\n"+
				"comment.aid,\n"+
				"b.parent_id as parent_comment_id, \n"+ 
				"parent_comment.aid as parent_comment_aid, \n"+
				"comment.discussion_id,\n"+
				"poll.id as poll_id,\n"+
				"poll.aid as poll_aid,\n"+
				"comment.text,\n"+
				"comment.user_id,\n"+
				"user.aid as user_aid,\n"+
				"parent_user.id as parent_user_id,\n"+
				"parent_user.aid as parent_user_aid,\n"+
				"coalesce(full_user.name, concat('user_',user.aid)) as user_name,\n"+
				"comment.deleted,\n"+
				"comment.read,\n"+
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
				"left join poll on \n"+
				"comment.discussion_id = poll.discussion_id \n"+
				"left join full_user \n"+
				"on comment.user_id = full_user.user_id \n"+
				"left join user \n"+
				"on comment.user_id = user.id \n"+
				"left join comment parent_comment \n"+
				"on b.parent_id = parent_comment.id \n"+
				"left join user parent_user \n"+
				"on parent_comment.user_id = parent_user.id \n"
				);

		if (discussionId != null) {
			s.append("WHERE comment.discussion_id = " + discussionId + "\n");
		} else {
			s.append("WHERE comment.discussion_id >= 0 \n");
		}

		if (parentId != null) {
			s.append("and a.parent_id = " + parentId + " \n");
			s.append("and b.parent_id >= " + parentId + "\n"); //HRM?
		}

		if (minPathLength != null) {
			s.append("and b.path_length >= " + minPathLength + " \n");
		}
		
		if (maxPathLength != null) {
			s.append("and b.path_length <= " + maxPathLength + " \n");
		}
		
		if (commentUserId != null) {
			s.append("and comment.user_id = " + commentUserId + " \n");
		}
		
		if (parentUserId != null) {
			s.append("and parent_user.id = " + parentUserId + " \n");
		}
		
		if (read != null) {
			s.append("and comment.read = " + read + " \n");
		}

		s.append("GROUP BY a.child_id \n");
		
		if (orderBy != null) {
			s.append("order by " + orderBy + " \n");
		}
		
		if (pageSize != null) {
			s.append("limit " + pageSize + " \n");
		}
		
		if (pageNum != null) {
			s.append("offset " + ((pageNum - 1) * pageSize) + " \n");
		}
		

		
		s.append(";");
		
		System.out.println(s.toString());

		return s.toString();
	}


}

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
	
	@Table("comment_tree")
	public static class CommentTree extends Model {}
	public static final CommentTree COMMENT_TREE = new CommentTree();
	
	@Table("user")
	public static class User extends Model {}
	public static final User USER = new User();
	
	@Table("user_view")
	public static class UserView extends Model {}
	public static final UserView USER_VIEW = new UserView();
	
	
	
}

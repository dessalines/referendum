package com.dd.db;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

public class Tables {
	
	
	
	@Table("poll")
	public static class Poll extends Model {}
	public static final Poll POLL = new Poll();
	
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
	
	@Table("ballot_item")
	public static class BallotItem extends Model {}
	public static final BallotItem BALLOT_ITEM = new BallotItem();
	
	@Table("ballot_item_view")
	public static class BallotItemView extends Model {}
	public static final BallotItemView BALLOT_ITEM_VIEW = new BallotItemView();
	
	@Table("discussion")
	public static class Discussion extends Model {}
	public static final Discussion DISCUSSION = new Discussion();
	
	@Table("comment")
	public static class Comment extends Model {}
	public static final Comment COMMENT = new Comment();
	
	@Table("comment_tree")
	public static class CommentTree extends Model {}
	public static final CommentTree COMMENT_TREE = new CommentTree();
	
	
	
}

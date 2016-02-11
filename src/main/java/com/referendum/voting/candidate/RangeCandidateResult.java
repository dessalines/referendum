package com.referendum.voting.candidate;

import java.util.Comparator;

public class RangeCandidateResult implements Candidate {
	private Integer id, count;
	private Double score;
	
	public RangeCandidateResult(Integer id, Double score, Integer count) {
		this.id = id;
		this.score = score;
		this.count = count;
	}
	
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override public String toString() {
		return id.toString();
	}


	public Double getScore() {
		return score;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public static class RangeCandidateResultComparator implements Comparator<RangeCandidateResult> {

		@Override
		public int compare(RangeCandidateResult o1, RangeCandidateResult o2) {
			return o1.getScore().compareTo(o2.getScore());
		}

	}
}

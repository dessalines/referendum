package com.dd.voting.candidate;

import java.util.Comparator;

public class RangeCandidate implements Candidate {

	private Integer id;
	private Double rank;
	
	public RangeCandidate(Integer id, Double rank) {
		this.id = id;
		this.rank = rank;
	}
	
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override public String toString() {
		return id.toString();
	}


	public Double getRank() {
		return rank;
	}
	
	public static class RangeCandidateComparator implements Comparator<RangeCandidate> {

		@Override
		public int compare(RangeCandidate o1, RangeCandidate o2) {
			return o1.getRank().compareTo(o2.getRank());
		}

	}

}

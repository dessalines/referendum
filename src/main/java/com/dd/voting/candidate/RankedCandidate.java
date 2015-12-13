package com.dd.voting.candidate;

import java.util.Comparator;

public class RankedCandidate implements Candidate {

	private Integer id, rank;

	public RankedCandidate(Integer id, Integer rank) {
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


	public Integer getRank() {
		return rank;
	}
	
	public static class RankedCandidateComparator implements Comparator<RankedCandidate> {

		@Override
		public int compare(RankedCandidate o1, RankedCandidate o2) {
			return o1.getRank().compareTo(o2.getRank());
		}

	}

	
}

package com.dd.voting.candidate;

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

	
}

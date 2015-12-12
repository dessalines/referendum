package com.dd.voting.election;

import java.util.HashMap;
import java.util.Map;

import com.dd.voting.candidate.RankedCandidate;

public class ElectionRoundItem {

	enum Status {
		STAYS, DEFEATED, ELECTED;
	}
	
	public Status status = Status.STAYS;
	
	private RankedCandidate candidate;
	private Integer votes;
	
	public Map<Integer, DistributedVote> distributedVotes;
	
	public static class DistributedVote {
		private Integer votes, candidateId;
		
		public DistributedVote(Integer candidateId) {
			votes = 1;
			this.candidateId = candidateId;
		}
		
		public void addVote() {
			votes++;
		}
	}
	
	
	public ElectionRoundItem(RankedCandidate candidate) {
		this.candidate = candidate;
		votes = 1;
		distributedVotes = new HashMap<>();
	}
	
	public void addVote() {
		votes++;
	}
	
	public Integer getVotes() {
		return votes;
	}
	
	public RankedCandidate getCandidate() {
		return candidate;
	}

	public void addVotes(Integer votes) {
		this.votes += votes - 1;
		
	}
	
	
	
	
	
}

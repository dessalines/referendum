package com.dd.voting.election;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.dd.voting.candidate.RankedCandidate;

public class ElectionRoundItem {

	public enum Status {
		STAYS, DEFEATED, ELECTED;
	}
	
	private Status status = Status.STAYS;
	
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
		votes = 0;
		distributedVotes = new HashMap<>();
	}
	
	public void addVote() {
		votes++;
	}
	
	public Integer getVotes() {
		return votes;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public RankedCandidate getCandidate() {
		return candidate;
	}

	public void addVotes(Integer votes) {
		this.votes += votes;
		
	}
	
	public static class ElectionRoundItemComparator implements Comparator<ElectionRoundItem> {

		@Override
		public int compare(ElectionRoundItem o1, ElectionRoundItem o2) {
			return o1.getCandidate().getId().compareTo(o2.getCandidate().getId());
		}

	}
	
	
	
	
	
}

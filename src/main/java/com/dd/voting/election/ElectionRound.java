package com.dd.voting.election;

import java.util.List;

public class ElectionRound {
	
	private List<ElectionRoundItem> electionRoundItems;
	private Integer threshold;

	public ElectionRound(List<ElectionRoundItem> electionRoundItems, Integer threshold) {
		this.electionRoundItems = electionRoundItems;
		this.threshold = threshold;
	}
	
	
	
	public List<ElectionRoundItem> getElectionRoundItems() {
		return electionRoundItems;
	}
	
	public Integer getThreshold() {
		return threshold;
	}
	
}

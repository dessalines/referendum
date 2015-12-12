package com.dd.voting.election;

import java.util.List;

public class ElectionRound {
	
	private List<ElectionRoundItem> electionRoundItems;

	public ElectionRound(List<ElectionRoundItem> electionRoundItems) {
		this.electionRoundItems = electionRoundItems;
	}
	
	
	
	public List<ElectionRoundItem> getElectionRoundItems() {
		return electionRoundItems;
	}
	
}

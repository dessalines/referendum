package com.dd.voting.election;

import java.util.Collections;
import java.util.List;

import com.dd.voting.election.ElectionRoundItem.ElectionRoundItemComparator;

public class ElectionRound {
	
	private List<ElectionRoundItem> electionRoundItems;
	private Integer threshold;

	public ElectionRound(List<ElectionRoundItem> electionRoundItems, Integer threshold) {
		this.electionRoundItems = electionRoundItems;
		this.threshold = threshold;
		
		Collections.sort(this.electionRoundItems, new ElectionRoundItemComparator());
		
		
		
	}
	
	
	
	public List<ElectionRoundItem> getRoundItems() {
		return electionRoundItems;
	}
	
	public Integer getThreshold() {
		return threshold;
	}
	
}

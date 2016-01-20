package com.dd.voting.results;

import java.util.List;

import com.dd.voting.candidate.RangeCandidate;
import com.dd.voting.results.ElectionResults.ElectionResultsType;

public interface RangeElectionResults extends ElectionResults {

	List<RangeCandidate> getRankings();
	
	default ElectionResultsType getElectionResultsType() {
		return ElectionResultsType.MULTIPLE_WINNER;
	}
}

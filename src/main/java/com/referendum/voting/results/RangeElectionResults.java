package com.referendum.voting.results;

import java.util.List;

import com.referendum.voting.candidate.RangeCandidateResult;

public interface RangeElectionResults extends ElectionResults {

	List<RangeCandidateResult> getRankings();
	
	default ElectionResultsType getElectionResultsType() {
		return ElectionResultsType.MULTIPLE_WINNER;
	}
}

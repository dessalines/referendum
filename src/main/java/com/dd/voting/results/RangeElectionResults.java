package com.dd.voting.results;

import java.util.List;

import com.dd.voting.candidate.RangeCandidateResult;

public interface RangeElectionResults extends ElectionResults {

	List<RangeCandidateResult> getRankings();
	
	default ElectionResultsType getElectionResultsType() {
		return ElectionResultsType.MULTIPLE_WINNER;
	}
}

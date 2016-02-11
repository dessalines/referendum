package com.referendum.voting.results;

import java.util.List;

import com.referendum.voting.ballot.RankedBallot;
import com.referendum.voting.election.ElectionRound;

public interface MultipleWinnerElectionResults extends ElectionResults {

	List<ElectionRound> getRounds();
	
	List<RankedBallot> getBallots();
	
	Integer getThreshold(Integer votes, Integer seats);
	
	default ElectionResultsType getElectionResultsType() {
		return ElectionResultsType.MULTIPLE_WINNER;
	}
	
	
}

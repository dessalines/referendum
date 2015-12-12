package com.dd.voting.results;

import java.util.List;

import com.dd.voting.ballot.RankedBallot;
import com.dd.voting.election.ElectionRound;

public interface MultipleWinnerElectionResults extends ElectionResults {

	List<ElectionRound> getElectionRounds();
	
	List<RankedBallot> getBallots();
	
	Integer getThreshold(Integer votes, Integer seats);
	
	default ElectionResultsType getElectionResultsType() {
		return ElectionResultsType.MULTIPLE_WINNER;
	}
	
	
}

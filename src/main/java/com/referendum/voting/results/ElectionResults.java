package com.referendum.voting.results;

import com.referendum.voting.election.Election;


public interface ElectionResults extends Election {
	
	enum ElectionResultsType {
		MULTIPLE_WINNER, SINGLE_WINNER;
	}
	
	ElectionResultsType getElectionResultsType();
	
	
	

}

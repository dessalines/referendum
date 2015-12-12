package com.dd.voting.results;

import com.dd.voting.election.Election;


public interface ElectionResults extends Election {
	
	enum ElectionResultsType {
		MULTIPLE_WINNER, SINGLE_WINNER;
	}
	
	ElectionResultsType getElectionResultsType();
	
	
	

}

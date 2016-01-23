package com.dd.voting.election;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.voting.ballot.RangeBallot;
import com.dd.voting.candidate.RangeCandidate;
import com.dd.voting.candidate.RangeCandidateResult;
import com.dd.voting.results.RangeElectionResults;
import com.dd.voting.voting_system.choice_type.RangeVotingSystem;

import static com.dd.voting.candidate.RangeCandidateResult.RangeCandidateResultComparator;

public class RangeElection implements RangeVotingSystem, RangeElectionResults {

	static final Logger log = LoggerFactory.getLogger(RangeElection.class);

	public RangeVotingSystemType type;
	public List<RangeBallot> ballots;
	public List<RangeCandidateResult> rankings;

	// A helpful map of candidates ids to ballots
	Map<Integer, List<RangeCandidate>> candidateBallots;

	public RangeElection(RangeVotingSystemType type, List<RangeBallot> ballots) {
		this.type = type;
		this.ballots = ballots;

		runElection();
	}




	/**
	 * This ones simple, just organize by each candidate vote, then create a new RangeCandidate
	 * to do the rankings
	 */
	@Override
	public void runElection() {

		fillCandidateBallotsMap();

		calculateScoreForEachCandidate();

	}

	public void fillCandidateBallotsMap() {

		candidateBallots = new HashMap<>();

		for (RangeBallot ballot : ballots) {

			RangeCandidate candidate = ballot.getCandidate();

			if (candidateBallots.get(candidate.getId()) == null) {
				List<RangeCandidate> candidateList = new ArrayList<>();
				candidateList.add(candidate);
				candidateBallots.put(candidate.getId(), candidateList);
			} else {
				candidateBallots.get(candidate.getId()).add(candidate);
			}

		}
	}

	public void calculateScoreForEachCandidate() {

		// Loop over all the individual grouped candidates, and find the score
		rankings = new ArrayList<>();
		
		for (Entry<Integer, List<RangeCandidate>> candidateGrouped : candidateBallots.entrySet()) {

			Double score = null;

			// Add the integers to a collection
			List<Double> ints = new ArrayList<>();
			for (RangeCandidate rc : candidateGrouped.getValue()) {
				ints.add(rc.getRank());
			}

			if (type == RangeVotingSystemType.AVERAGE) {
				score = average(ints);
			} else if (type == RangeVotingSystemType.MEDIAN) {
				score = median(ints);
			}

			RangeCandidateResult result = new RangeCandidateResult(candidateGrouped.getKey(),
					score, candidateGrouped.getValue().size());

			rankings.add(result);

		}

		Collections.sort(rankings, new RangeCandidateResultComparator().reversed());
		

	}

	public Double median(List<Double> ints) {

		Collections.sort(ints);

		int middle = ints.size() / 2;
		
		Double median;
		if (ints.size() % 2 == 1) {
			median = (double) ints.get(middle);
		} else {
			median = (double) (ints.get(middle - 1) + ints.get(middle)) / 2;
		}
		
		return median;
	}

	public Double average(List<Double> ints) {

		Double sum = 0.0, count = 0.0;

		for (Double i : ints) {
			sum += i;
			count++;
		}
		
		Double avg = (double) (sum / count);

		return avg;
	}

	@Override
	public List<RangeCandidateResult> getRankings() {
		return rankings;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RangeVotingSystemType getRangeVotingSystemType() {
		return type;
	}

	@Override
	public ElectionResultsType getElectionResultsType() {
		return ElectionResultsType.MULTIPLE_WINNER;
	}


}

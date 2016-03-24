package com.referendum.voting.election;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.referendum.voting.ballot.RangeBallot;
import com.referendum.voting.candidate.RangeCandidate;
import com.referendum.voting.candidate.RangeCandidateResult;
import com.referendum.voting.results.RangeElectionResults;
import com.referendum.voting.voting_system.choice_type.RangeVotingSystem;

import static com.referendum.voting.candidate.RangeCandidateResult.RangeCandidateResultComparator;

public class RangeElection implements RangeVotingSystem, RangeElectionResults {

	static final Logger log = LoggerFactory.getLogger(RangeElection.class);

	public RangeVotingSystemType type;
	public List<RangeBallot> ballots;
	public List<RangeCandidateResult> rankings;
	public Integer id;
	public Integer minimumPctThreshold;

	// A helpful map of candidates ids to ballots
	Map<Integer, List<RangeCandidate>> candidateBallots;

	public RangeElection(Integer id, RangeVotingSystemType type, List<RangeBallot> ballots, 
			Integer minimumPctThreshold) {
		this.id = id;
		this.type = type;
		this.ballots = ballots;
		this.minimumPctThreshold = minimumPctThreshold;

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
		
		// The highest number of ballots for any candidate
		Integer maxVotes = 0;
		for (Entry<Integer, List<RangeCandidate>> candidateGrouped : candidateBallots.entrySet()) {
			Integer count = candidateGrouped.getValue().size();
			if (count >= maxVotes) {
				maxVotes = count;
			}
		}
		
		Integer threshold = (maxVotes * minimumPctThreshold / 100);
		
		log.info("vote count threshold = " + threshold);
		

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

			Integer count = candidateGrouped.getValue().size();
			RangeCandidateResult result = new RangeCandidateResult(candidateGrouped.getKey(),
					score, count);
			
			log.info("candidate = " + candidateGrouped.getKey() + " , vote count = " + count);

			if (count >= threshold) {
				rankings.add(result);
			}

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
		return id;
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

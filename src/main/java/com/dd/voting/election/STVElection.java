package com.dd.voting.election;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.tools.Tools;
import com.dd.voting.ballot.RankedBallot;
import com.dd.voting.candidate.Candidate;
import com.dd.voting.candidate.RankedCandidate;
import com.dd.voting.election.ElectionRoundItem.DistributedVote;
import com.dd.voting.election.ElectionRoundItem.Status;
import com.dd.voting.results.MultipleWinnerElectionResults;
import com.dd.voting.voting_system.rank_type.RankedVotingSystem;


public class STVElection implements MultipleWinnerElectionResults, RankedVotingSystem {


	static final Logger log = LoggerFactory.getLogger(STVElection.class);


	public enum Quota {
		HARE, DROOP;
	}

	public Quota quota;
	public List<RankedBallot> ballots;
	public List<ElectionRound> electionRounds;
	public Integer seats, votePool, roundNumber = 0;

	// A helpful map of candidates ids to ballots
	Map<Integer, List<RankedBallot>> candidateBallots;

	// A helpful map from rounds to candidates to round items
	// IE [round 0, candidate 1, [status, votes, etc]]
	Map<Integer, Map<Integer, ElectionRoundItem>> roundToCandidateToItem;

	Map<Integer, RankedCandidate> candidateIdToCandidate = new HashMap<>();

	public STVElection(Quota quota, List<RankedBallot> ballots, Integer seats) {
		this.quota = quota;
		this.ballots = ballots;
		this.seats = seats;

		runElection();
	}

	@Override
	public void runElection() {

		Integer winners = 0;

		electionRounds = new ArrayList<>();
		votePool = ballots.size();

		fillCandidateMaps();

		while (winners < seats) {

			Integer threshold = getThreshold(votePool, seats);



			//			log.info("candidate ballots total: " + Tools.GSON2.toJson(candidateBallots));
			log.info("round to candidate items: " + Tools.GSON2.toJson(roundToCandidateToItem));

			List<ElectionRoundItem> roundItems = new ArrayList<>();

			ElectionRound er = new ElectionRound(roundItems);

			electionRounds.add(er);

			Boolean areWinners = false;

			log.info("treshold = " + threshold);

			// Now you have all the ballots round counted, now do the rules
			for (Entry<Integer, ElectionRoundItem> e : roundToCandidateToItem.get(roundNumber).entrySet()) {


				Integer cCandidateId = e.getKey();

				RankedCandidate cCandidate = candidateIdToCandidate.get(cCandidateId);



				ElectionRoundItem eri = e.getValue();

				Integer votes = eri.getVotes();



				// Check to see if they win
				if (votes >= threshold) {

					log.info("Candidate: " + cCandidate.getId() + " won with " + votes + "/" + threshold);

					eri.status = Status.ELECTED;

					Integer surplusVotes = votes - threshold;

					// Loop over the ballots in order, if they have that for the current round,
					// then add that surplus to their next hopeful


					for (int i = 0; i < surplusVotes; i++) {

						RankedBallot ballot = candidateBallots.get(cCandidate.getId()).get(i);


						distributeVote(roundNumber, cCandidate, ballot);

					}


					// If next preference doesn't exist, then eliminate those votes from the pool

					winners++;
					areWinners = true;

				}

				roundItems.add(eri);

			}

			// If there were no winners after those were counted, then eliminate the lowest, and distribute them
			if (!areWinners) {
				log.info("No Winners");

				RankedCandidate eliminatedCandidate = null;

				Integer maxVotes = Integer.MAX_VALUE;


				// Find the candidate with the lowest votes
				for (Entry<Integer, ElectionRoundItem> e : roundToCandidateToItem.get(roundNumber).entrySet()) {
					Integer cVotes = e.getValue().getVotes();

					log.info("cVotes = " + cVotes);
					if (cVotes < maxVotes) {
						Integer eliminatedCandidateId = e.getKey();
						eliminatedCandidate = candidateIdToCandidate.get(eliminatedCandidateId);
						maxVotes = e.getValue().getVotes();
						log.info("new max votes = " + maxVotes);
						log.info("ec = " + eliminatedCandidate.getId());
					}

				}

				// Set them as defeated
				log.info("round # " + roundNumber + " eliminatedCandidate = " + eliminatedCandidate.getId());
				log.info(roundToCandidateToItem.get(0).get(eliminatedCandidate.getId()).status.toString());
				roundToCandidateToItem.get(roundNumber).get(eliminatedCandidate.getId()).status = Status.DEFEATED;

				log.info("Candidate " + eliminatedCandidate.getId() + " defeated");
				log.info(roundToCandidateToItem.get(0).get(eliminatedCandidate.getId()).status.toString());

				// Transfer their votes to their next choice
				for (RankedBallot ballot : candidateBallots.get(eliminatedCandidate.getId())) {
					distributeVote(roundNumber, eliminatedCandidate, ballot);
				}

			}

			transferVotesToNextRound();
			roundNumber++;


		}




	}

	private void distributeVote(
			int roundNumber,
			RankedCandidate candidate,
			RankedBallot ballot) {


		ElectionRoundItem eri = roundToCandidateToItem.get(roundNumber).get(candidate.getId());



		log.info("round number = " + roundNumber + " ballot rankings size = " + ballot.getRankings().size());


		log.info(Tools.GSON2.toJson(ballot));


		// TODO what if ToCandidate is out of race? Don't do any distribution
		if (ballot.getRankings().size() <= roundNumber) {
			votePool--;

			log.info("Couldn't distributed because no next candidate, vote pool now = " + votePool);
		}

		// distributed candidate is still in race
		else {

			log.info("Distributing vote");

			// add those excess votes to the first alive choice on their ballot
			RankedCandidate distributedToCandidate = null;
			for (RankedCandidate candidateOption : ballot.getRankings()) {
				if (roundToCandidateToItem.get(roundNumber).get(candidateOption.getId()).status == Status.STAYS) {
					distributedToCandidate = candidateOption;
					break;
				}
			}

			// Fill it if its empty
			if (roundToCandidateToItem.get(roundNumber+1) == null) {
				ElectionRoundItem newEri = new ElectionRoundItem(distributedToCandidate);
				HashMap<Integer, ElectionRoundItem> eriMap = new HashMap<Integer, ElectionRoundItem>();
				eriMap.put(distributedToCandidate.getId(), newEri);

				roundToCandidateToItem.put(roundNumber+1, eriMap);

			}

			if (roundToCandidateToItem.get(roundNumber+1).get(distributedToCandidate.getId()) == null) {
				ElectionRoundItem newEri = new ElectionRoundItem(distributedToCandidate);

				roundToCandidateToItem.get(roundNumber+1).put(distributedToCandidate.getId(), newEri);
			}


			if (eri.distributedVotes.get(distributedToCandidate.getId()) == null) {

				log.info("First distributed vote from " + eri.getCandidate().getId() + 
						" to " + distributedToCandidate.getId());
				DistributedVote dv = new DistributedVote(distributedToCandidate.getId());
				eri.distributedVotes.put(distributedToCandidate.getId(), dv);

			} else {
				log.info("distributed vote from " + eri.getCandidate().getId() + 
						" to " + distributedToCandidate.getId());

				eri.distributedVotes.get(distributedToCandidate.getId()).addVote();



			}

			roundToCandidateToItem.get(roundNumber + 1).get(distributedToCandidate.getId()).addVote();
			log.info("Distributed next eri = " 
					+ Tools.GSON2.toJson(roundToCandidateToItem.get(roundNumber + 1).
							get(distributedToCandidate.getId())));


		}

		log.info(Tools.GSON2.toJson(eri));

	}

	private void transferVotesToNextRound() {

		// Create the next round
		if (roundToCandidateToItem.get(roundNumber + 1) == null) {
			roundToCandidateToItem.put(roundNumber + 1, new HashMap<>());
		}



		for (Entry<Integer, ElectionRoundItem> e : roundToCandidateToItem.get(roundNumber).entrySet()) {
			Integer candidateId = e.getKey();
			ElectionRoundItem eri = e.getValue();

			


			// Only transfer the candidates still in the race, 
			// and if they haven't been distributed already
			if (eri.status == Status.STAYS) {
				
				ElectionRoundItem nextERI = roundToCandidateToItem.get(roundNumber + 1).get(candidateId);
				
				log.info("transferred eri to next round = " + Tools.GSON2.toJson(eri));
				if (nextERI == null) {
					roundToCandidateToItem.get(roundNumber + 1).put(candidateId, eri);
				} else {
					roundToCandidateToItem.get(roundNumber + 1).get(candidateId).addVotes(eri.getVotes());
				}
			}



		}


	}

	private void fillCandidateMaps() {


		// Loop over the ballots
		for (RankedBallot cBallot : ballots) {


			// Get the round votes, if they exist
			if (cBallot.getRankings().size() > roundNumber) {

				RankedCandidate cCandidate = cBallot.getRankings().get(roundNumber);

				candidateIdToCandidate.put(cCandidate.getId(), cCandidate);

				if (roundToCandidateToItem == null) {
					roundToCandidateToItem = new HashMap<>();

					Map<Integer, ElectionRoundItem> eriMap = new HashMap<>();
					eriMap.put(cCandidate.getId(), new ElectionRoundItem(cCandidate));

					roundToCandidateToItem.put(roundNumber, eriMap);

				} else {


					Map<Integer, ElectionRoundItem> map = roundToCandidateToItem.get(roundNumber);

					if (map == null) {
						Map<Integer, ElectionRoundItem> eriMap = new HashMap<>();
						eriMap.put(cCandidate.getId(), new ElectionRoundItem(cCandidate));

						roundToCandidateToItem.put(roundNumber, eriMap);
					} else {


						if (map.get(cCandidate.getId()) == null) {

							log.info("Creating an election round item for candidate " + cCandidate.getId());
							roundToCandidateToItem.get(roundNumber).put(cCandidate.getId(), 
									new ElectionRoundItem(cCandidate));
						} else {
							log.info("adding a vote for candidate " + cCandidate.getId());
							roundToCandidateToItem.get(roundNumber).get(cCandidate.getId()).addVote();
						}
					}

				}



				if (candidateBallots == null) {
					candidateBallots = new HashMap<>();
					candidateBallots.put(cCandidate.getId(), new ArrayList<>(Arrays.asList(cBallot)));	
				} else {
					if (candidateBallots.get(cCandidate.getId()) == null) {
						log.info("new candidate = " + cCandidate.getId());
						List<RankedBallot> ballotList = new ArrayList<>();
						ballotList.add(cBallot);
						candidateBallots.put(cCandidate.getId(), ballotList);
					} else {
						candidateBallots.get(cCandidate.getId()).add(cBallot);
					}
				}

			}
		}
	}



	@Override
	public Integer getThreshold(Integer votes, Integer seats) {	

		Integer threshold = null;
		switch (quota) {
		case DROOP : 
			threshold = (votes/(seats + 1) + 1);
			break;
		case HARE :
			threshold = (votes/seats);
			break;
		}

		return threshold;
	}


	@Override
	public RankedVotingSystemType getRankedVotingSystemType() {
		return RankedVotingSystemType.STV;
	}



	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<ElectionRound> getElectionRounds() {
		return electionRounds;
	}

	@Override
	public List<RankedBallot> getBallots() {
		return ballots;
	}










}

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

		Integer aliveCandidates = roundToCandidateToItem.get(roundNumber).size();

		while (winners < seats) {
			log.info("alive = " + aliveCandidates + " seats = " + seats);

			Integer threshold = getThreshold(votePool, seats);

			aliveCandidates = roundToCandidateToItem.get(roundNumber).size();



			//			log.info("candidate ballots total: " + Tools.GSON2.toJson(candidateBallots));


			List<ElectionRoundItem> roundItems = new ArrayList<>();

			ElectionRound er = new ElectionRound(roundItems, threshold);

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

					eri.setStatus(Status.ELECTED);

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
			log.info("winners2 = " + winners + " seats = " + seats);
			if (!areWinners && winners < seats) {
				log.info("No Winners");

				RankedCandidate eliminatedCandidate = null;

				Integer maxVotes = Integer.MAX_VALUE;


				// Find the candidate with the lowest votes
				for (Entry<Integer, ElectionRoundItem> e : roundToCandidateToItem.get(roundNumber).entrySet()) {
					Integer cVotes = e.getValue().getVotes();

					if (cVotes < maxVotes) {

						Integer eliminatedCandidateId = e.getKey();
						eliminatedCandidate = candidateIdToCandidate.get(eliminatedCandidateId);
						maxVotes = e.getValue().getVotes();
						log.info("maxVotes = " + maxVotes + "elim = " + eliminatedCandidate.getId());
					}

				}


				// Set them as defeated
				log.info("round # " + roundNumber + " eliminatedCandidate = " + eliminatedCandidate.getId());
				roundToCandidateToItem.get(roundNumber).get(eliminatedCandidate.getId()).setStatus(Status.DEFEATED);

				log.info("Candidate " + eliminatedCandidate.getId() + " defeated");

				// Transfer their votes to their next choice
				// should've transferred ballots previously

				for (RankedBallot ballot : candidateBallots.get(eliminatedCandidate.getId())) {
					distributeVote(roundNumber, eliminatedCandidate, ballot);
				}

			}



			transferVotesToNextRound();
			roundNumber++;


		}



		// Set the remaining candidates as a winners or losers(not stays)
		for (Entry<Integer, ElectionRoundItem> e : roundToCandidateToItem.get(roundNumber-1).entrySet()) {

			log.info("winners = " + winners + " alive = " + aliveCandidates + " seats " + 
					seats + " status = " + e.getValue().getStatus());

			if (e.getValue().getStatus() == Status.STAYS) {
				if (winners < seats) {
					e.getValue().setStatus(Status.ELECTED);
				} else {
					e.getValue().setStatus(Status.DEFEATED);
				}
			}


			//			log.info("winners = " + winners + " alive = " + aliveCandidates + " seats " + 
			//					seats + " status = " + e.getValue().getStatus());
			//			if (winners <= seats && e.getValue().getStatus() == Status.STAYS) {
			//				e.getValue().setStatus(Status.ELECTED);
			//			} else {
			//				e.getValue().setStatus(Status.DEFEATED);
			//			}


		}




	}

	private void distributeVote(
			int roundNumber,
			RankedCandidate candidate,
			RankedBallot ballot) {


		ElectionRoundItem eri = roundToCandidateToItem.get(roundNumber).get(candidate.getId());



		//		log.info("round number = " + roundNumber + " ballot rankings size = " + ballot.getRankings().size());
		//		log.info(Tools.GSON2.toJson(ballot));


		// TODO what if ToCandidate is out of race? Don't do any distribution
		if (ballot.getRankings().size() <= roundNumber) {
			//			votePool -= eri.getVotes();
			votePool -= 1;
			log.info("Couldn't distributed because no next candidate, vote pool now = " + votePool);
		}

		// distributed candidate is still in race
		else {

			// add those excess votes to the first alive choice on their ballot
			RankedCandidate distributedToCandidate = null;
			for (RankedCandidate candidateOption : ballot.getRankings()) {

				//			log.info(Tools.GSON2.toJson(candidateOption));
				//												log.info("round = " + roundNumber + Tools.GSON2.toJson(roundToCandidateToItem.get(roundNumber)));

				ElectionRoundItem optionERI = roundToCandidateToItem.get(roundNumber).get(candidateOption.getId());

				if (optionERI != null && optionERI.getStatus() == Status.STAYS) {
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

			// Add the vote
			roundToCandidateToItem.get(roundNumber + 1).get(distributedToCandidate.getId()).addVote();

			// Also distribute the ballot over
			addToCandidateBallots(ballot, distributedToCandidate);

			//			log.info("Distributed next eri = " 
			//					+ Tools.GSON2.toJson(roundToCandidateToItem.get(roundNumber + 1).
			//							get(distributedToCandidate.getId())));


		}

		//		log.info(Tools.GSON2.toJson(eri));

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
			if (eri.getStatus() == Status.STAYS) {

				ElectionRoundItem nextERI = roundToCandidateToItem.get(roundNumber + 1).get(candidateId);

				//				log.info("transferred eri to next round = " + Tools.GSON2.toJson(eri));
				if (nextERI == null) {
					ElectionRoundItem newERI = new ElectionRoundItem(candidateIdToCandidate.get(candidateId));

					newERI.addVotes(eri.getVotes());
					roundToCandidateToItem.get(roundNumber + 1).put(candidateId, newERI);
				} else {
					roundToCandidateToItem.get(roundNumber + 1).get(candidateId).addVotes(eri.getVotes());
				}
			}

		}

		log.info("round to candidate items: " + Tools.GSON2.toJson(roundToCandidateToItem.get(roundNumber)));


	}

	private void fillCandidateMaps() {


		// Loop over the ballots
		for (RankedBallot cBallot : ballots) {


			// Find all the candidates, and add them to a map from candidateId to candidate
			for (int i = 0; i < cBallot.getRankings().size(); i++) {

				RankedCandidate cCandidate = cBallot.getRankings().get(i);

				candidateIdToCandidate.put(cCandidate.getId(), cCandidate);


				// Fill the first round maps
				if (i == 0) {
					addToRoundMap(cCandidate, true);
					addToCandidateBallots(cBallot, cCandidate);
				} else {
					addToRoundMap(cCandidate, false);
				}

			}






		}

	}

	private void addToRoundMap(RankedCandidate cCandidate, Boolean initialVote) {

		ElectionRoundItem eri = new ElectionRoundItem(cCandidate);
		if (initialVote) {
			eri.addVote();
		}



		if (roundToCandidateToItem == null) {
			roundToCandidateToItem = new HashMap<>();

			Map<Integer, ElectionRoundItem> eriMap = new HashMap<>();
			eriMap.put(cCandidate.getId(), eri);

			roundToCandidateToItem.put(roundNumber, eriMap);

		} else {



			if (roundToCandidateToItem.get(roundNumber) == null) {
				Map<Integer, ElectionRoundItem> eriMap = new HashMap<>();
				eriMap.put(cCandidate.getId(), eri);

				roundToCandidateToItem.put(roundNumber, eriMap);
			} else {


				if (roundToCandidateToItem.get(roundNumber).get(cCandidate.getId()) == null) {

					log.info("Creating an election round item for candidate " + cCandidate.getId());
					roundToCandidateToItem.get(roundNumber).put(cCandidate.getId(), 
							eri);
				} else {

					if (initialVote) {
						log.info("adding a vote for candidate " + cCandidate.getId());
						roundToCandidateToItem.get(roundNumber).get(cCandidate.getId()).addVote();
					}
				}
			}

		}
	}

	private void addToCandidateBallots(RankedBallot cBallot,
			RankedCandidate cCandidate) {
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
	public List<ElectionRound> getRounds() {
		return electionRounds;
	}

	@Override
	public List<RankedBallot> getBallots() {
		return ballots;
	}










}

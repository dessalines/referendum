package com.dd.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dd.voting.ballot.RankedBallot;
import com.dd.voting.candidate.RankedCandidate;

public class SampleData {

	public static Integer orange = 1, pear = 2, chocolate = 3, strawberry = 4, mixed = 5;


	public static  List<RankedBallot> setupBallots() {

		List<RankedBallot> ballots = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			RankedCandidate rc = new RankedCandidate(orange, 1);
			RankedBallot rb = new RankedBallot(Arrays.asList(rc));
			ballots.add(rb);
		}

		for (int i = 0; i < 2; i++) {
			RankedCandidate rc = new RankedCandidate(pear, 1);
			RankedCandidate rc2 = new RankedCandidate(orange, 2);
			List<RankedCandidate> candidates = new ArrayList<>();

			candidates.add(rc);
			candidates.add(rc2);
			RankedBallot rb = new RankedBallot(candidates);
			ballots.add(rb);
		}

		// Weird thing with the test, do 4 with second choice strawberries,
		// then 4 with second choice sweets, then another 4 with second choice strawberries
		for (int i = 0; i < 4; i++) {
			RankedCandidate rc = new RankedCandidate(chocolate, 1);
			RankedCandidate rc2 = new RankedCandidate(strawberry, 2);
			List<RankedCandidate> candidates = new ArrayList<>();

			candidates.add(rc);
			candidates.add(rc2);
			RankedBallot rb = new RankedBallot(candidates);
			ballots.add(rb);
		}

		for (int i = 0; i < 4; i++) {
			RankedCandidate rc = new RankedCandidate(chocolate, 1);
			RankedCandidate rc2 = new RankedCandidate(mixed, 2);
			List<RankedCandidate> candidates = new ArrayList<>();

			candidates.add(rc);
			candidates.add(rc2);
			RankedBallot rb = new RankedBallot(candidates);
			ballots.add(rb);
		}

		for (int i = 0; i < 4; i++) {
			RankedCandidate rc = new RankedCandidate(chocolate, 1);
			RankedCandidate rc2 = new RankedCandidate(strawberry, 2);
			List<RankedCandidate> candidates = new ArrayList<>();

			candidates.add(rc);
			candidates.add(rc2);
			RankedBallot rb = new RankedBallot(candidates);
			ballots.add(rb);
		}

		for (int i = 0; i < 1; i++) {
			RankedCandidate rc = new RankedCandidate(strawberry, 1);
			RankedBallot rb = new RankedBallot(Arrays.asList(rc));
			ballots.add(rb);
		}

		for (int i = 0; i < 1; i++) {
			RankedCandidate rc = new RankedCandidate(mixed, 1);
			RankedBallot rb = new RankedBallot(Arrays.asList(rc));
			ballots.add(rb);
		}


		return ballots;
	}


}

package com.dd.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.db.Actions;
import com.dd.db.Transformations;
import com.dd.tools.Tools;
import com.dd.voting.ballot.RangeBallot;
import com.dd.voting.candidate.RangeCandidate;
import com.dd.voting.election.RangeElection;
import com.dd.voting.voting_system.choice_type.RangeVotingSystem.RangeVotingSystemType;

import static com.dd.db.Tables.*;

public class RangeElectionTest2 extends TestCase {

	static final Logger log = LoggerFactory.getLogger(RangeElectionTest2.class);	
	
	public void testRangeElection() {
		
		Tools.dbInit();
	
//		String json = Actions.rangePollResults("1");
//		log.info(json);
		
		
		String json = Actions.rangePollResults("1");
		
		System.out.println(json);
		
		Tools.dbClose();
		
	}
}

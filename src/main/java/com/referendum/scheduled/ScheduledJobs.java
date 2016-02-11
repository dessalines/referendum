package com.referendum.scheduled;


import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduledJobs {
	public static void start() {
		// Another
		try {
			// Grab the Scheduler instance from the Factory 
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

			// and start it off
			scheduler.start();

			JobDetail job = newJob(BuildFastTables.class)
					.build();

			// Trigger the job to run now, and then repeat every 20 minutes
			Trigger trigger = newTrigger()
					.startNow()
					.withSchedule(simpleSchedule()
							.withIntervalInMinutes(15)
							.repeatForever())            
							.build();

			// Tell quartz to schedule the job using our trigger
			scheduler.scheduleJob(job, trigger);

		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}
}

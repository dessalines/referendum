package com.referendum;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.referendum.scheduled.ScheduledJobs;
import com.referendum.tools.Tools;
import com.referendum.webservice.WebService;

public class Main {

	static Logger log = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	@Option(name="-uninstall",usage="Uninstall torrenttunes-client.(WARNING, this deletes your library)")
	private boolean uninstall;

	@Option(name="-loglevel", usage="Sets the log level [INFO, DEBUG, etc.]")
	private String loglevel = "INFO";
	
	@Option(name="-maintenance", usage="Redirects to the maintenance page")
	private boolean maintenanceRedirect;

	@Option(name="-local", usage="Use the local webservice")
	private boolean local;

	public void doMain(String[] args) {

		parseArguments(args);

		// See if the user wants to uninstall it
		if (uninstall) {
			Tools.uninstall();
		}
		
		if (maintenanceRedirect) {
			DataSources.BASE_ENDPOINT = DataSources.MAINTENANCE_PAGE_URL();
		}


		log.setLevel(Level.toLevel(loglevel));
//		log.getLoggerContext().getLogger("org.eclipse.jetty").setLevel(Level.OFF);
//		log.getLoggerContext().getLogger("spark.webserver").setLevel(Level.OFF);

		
		Tools.setupDirectories();

		Tools.copyResourcesToHomeDir(true);

		Tools.addExternalWebServiceVarToTools(local);
		
		ScheduledJobs.start();

		// Startup the web service
		WebService.start();

	}



	private void parseArguments(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);

		try {

			parser.parseArgument(args);

		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			System.err.println("java -jar referendum.jar [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			System.exit(0);


			return;
		}
	}


	public static void main(String[] args) {
		new Main().doMain(args);

	}


}

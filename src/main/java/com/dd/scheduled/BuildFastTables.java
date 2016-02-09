package com.dd.scheduled;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dd.DataSources;
import com.dd.tools.ScriptRunner;



public class BuildFastTables implements Job {

	static final Logger log = LoggerFactory.getLogger(BuildFastTables.class);


	private static void create() {


	

			Properties prop = DataSources.DB_PROP;

		
			Connection mConnection;
			try {
			    Class.forName("com.mysql.jdbc.Driver");
			    mConnection = DriverManager.getConnection(prop.getProperty("dburl"),
			    		prop.getProperty("dbuser"),
			    		prop.getProperty("dbpassword"));
			    
			    ScriptRunner runner = new ScriptRunner(mConnection, false, false);
			    runner.setErrorLogWriter(null);
			    runner.setLogWriter(null);
			    log.info("Rebuilding fast tables....");
				runner.runScript(new BufferedReader(new FileReader(DataSources.SQL_FAST_TABLES_FILE())));
				
				
			} catch (ClassNotFoundException e) {
			    log.error("Unable to get mysql driver: " + e);
			} catch (SQLException e) {
			    log.error("Unable to connect to server: " + e);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
//			try {
//			ArrayList<String> cmd = new ArrayList<String>();
//			cmd.add("mysql");
//			cmd.add("-u" + prop.getProperty("dbuser"));
//			cmd.add("-p" + prop.getProperty("dbpassword"));
//			cmd.add(prop.getProperty("dburl"));
//			cmd.add("<");
//			cmd.add(DataSources.SQL_FAST_TABLES_FILE());
//			
//			for (String uz : cmd) {System.out.println(uz);}
//
//			ProcessBuilder b = new ProcessBuilder(cmd);
//			b.inheritIO();
//			Process p;
//
//			p = b.start();
//
//
//			p.waitFor();
//
//			log.info("Fast Tables created succesfully.");
//			
//		} catch (IOException | InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		


	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		create();
	}
}

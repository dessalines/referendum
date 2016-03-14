package com.referendum.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.referendum.DataSources;

public class WriteHTMLFiles {
	
	private Map<String, String> templates;

	static final Logger log = LoggerFactory.getLogger(WriteHTMLFiles.class);

	public static void write() {
		new WriteHTMLFiles();
	}
	
	private WriteHTMLFiles() {
		
		setupTemplates();

		for (String page : DataSources.HEAD_TEMPLATES) {
			writeFile(DataSources.TEMPLATES(page));
		}

	}
	
	private void setupTemplates() {
		
		templates = new HashMap<>();
		
		for (String name : DataSources.SUB_TEMPLATES) {
			templates.put(name, Tools.readFile(DataSources.TEMPLATES(name)));
		}

	}

	private void writeFile(String headTemplateFile) {
		try {

			Reader reader = new FileReader(new File(headTemplateFile));

			log.info(headTemplateFile);
			File outputFile = new File(headTemplateFile.split("template")[0] + "html");

			Writer writer = new FileWriter(outputFile);

			MustacheFactory mf = new DefaultMustacheFactory();
			Mustache mustache = mf.compile(reader, "example");
			mustache.execute(writer, templates);
			writer.flush();
			
			log.info(Tools.readFile(outputFile.getAbsolutePath()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
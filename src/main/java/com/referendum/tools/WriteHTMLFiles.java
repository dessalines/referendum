package com.referendum.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.referendum.DataSources;

public class WriteHTMLFiles {

	private Map<String, String> templates;

	private List<File> templateFiles; 

	static final Logger log = LoggerFactory.getLogger(WriteHTMLFiles.class);

	public static void write() {
		new WriteHTMLFiles();
	}

	private WriteHTMLFiles() {


		fetchTemplateFiles();

		readTemplatesIntoMap();

		for (File file : templateFiles) {
			writeFile(file.getAbsolutePath());
		}

	}

	private void fetchTemplateFiles() {
		try {
			templateFiles = Files.walk(Paths.get(DataSources.WEB_HTML()))
					.filter(Files::isRegularFile)
					.filter(p -> p.toString().endsWith("template"))
					.map(Path::toFile)
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

	private void readTemplatesIntoMap() {

		templates = new HashMap<>();

		for (File file : templateFiles) {
			templates.put(file.getName().split("\\.")[0], Tools.readFile(file.getAbsolutePath()));
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
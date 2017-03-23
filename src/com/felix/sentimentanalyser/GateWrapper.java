package com.felix.sentimentanalyser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;

import com.felix.util.KeyValues;
import com.felix.util.logging.LoggerInterface;
import com.felix.util.logging.SystemOutLogger;
import com.google.gson.Gson;

import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

public class GateWrapper {

	private CorpusController _appController;
	private LoggerInterface _logger;
	private KeyValues _config;

	public GateWrapper(LoggerInterface logger, KeyValues config) {
		_logger = logger;
		_config = config;
		String gate_home = "/home/felix/bin/gate-8.3";
		if (config != null) {
			gate_home = config.getString("gate_home");
		}
		// initialise the GATE library
		_logger.debug("Initialising GATE...");
		try {
			Gate.setGateHome(new File(gate_home));
			Gate.init();
		} catch (GateException e) {
			e.printStackTrace();
		}
		_logger.debug("...GATE initialised");

	}

	/**
	 * Initialise the ANNIE system. This creates a "corpus pipeline" application
	 * that can be used to run sets of documents through the extraction system.
	 */
	public void initApplication(String appPath) throws GateException, IOException {
		_logger.debug("Initialising Application...");

		File annieGapp = new File(appPath);
		_appController = (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);
		_logger.debug("...QA loaded");
	} // initAnnie()

	/**
	 * RE initialize the lexicons
	 * 
	 * @param input
	 * @return
	 */
	public void reInitProcessingResources() {
		Collection<ProcessingResource> prs = _appController.getPRs();
		for (Iterator iterator = prs.iterator(); iterator.hasNext();) {
			ProcessingResource processingResource = (ProcessingResource) iterator.next();
//			System.out.println(processingResource.getName());
			try {
				processingResource.reInit();
			} catch (ResourceInstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				_logger.error(e.getMessage());
			}
		}

	}

	public String analyseQuery(String input) {
		String retString = "";
		String polarity = "", posWords = "", negWords = "";
		int wc = 0;
		try {
			// create a GATE corpus and add a document for each command-line
			// argument
			Corpus corpus = Factory.newCorpus("");
			Document testd = Factory.newDocument(input);
			corpus.add(testd);

			// tell the pipeline about the corpus and run it
			this.setCorpus(corpus);
			this.execute();

			Document doc = (Document) corpus.iterator().next();
			AnnotationSet defaultAnnotSet = doc.getAnnotations();

			FeatureMap features = doc.getFeatures();
			polarity = (String) features.get("polarity");
			posWords = (String) features.get("positive words");
			negWords = (String) features.get("negative words");
			wc = (int) features.get("wc");
		} catch (GateException e) {
			e.printStackTrace();
		}
		SentimentResponse gr = new SentimentResponse(polarity, posWords, negWords, wc);
		Gson gson = new Gson();
		retString = StringEscapeUtils.unescapeJava(gson.toJson(gr));
		return retString;
	}

	public static void main(String[] args) {
		SystemOutLogger logger = new SystemOutLogger();
		GateWrapper gw = new GateWrapper(logger, null);
		try {
			gw.initApplication("/home/felix/workspace/SentimentAnalyser/WebContent/apps/Sentiment/Sentiment.gapp");
			String input = "das ist ganz großer Schrott";
			try {
				// input = FileUtil.getFileText(new File(args[0]));

				System.out.println(gw.analyseQuery(input));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gw.reInitProcessingResources();
		} catch (GateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Tell ANNIE's controller about the corpus you want to run on */
	public void setCorpus(Corpus corpus) {
		_appController.setCorpus(corpus);
	} // setCorpus

	/** Run ANNIE */
	public void execute() throws GateException {
		_logger.debug("Running QA...");
		_appController.execute();
		_logger.debug("...QA complete");
	} // execute()

}

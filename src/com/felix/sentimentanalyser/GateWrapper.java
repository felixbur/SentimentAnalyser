package com.felix.sentimentanalyser;

import gate.Annotation;

import gate.AnnotationSet;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.GateConstants;
import gate.corpora.RepositioningInfo;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;

import com.felix.util.FileUtil;
import com.felix.util.KeyValues;
import com.felix.util.logging.LoggerInterface;
import com.felix.util.logging.SystemOutLogger;
import com.google.gson.Gson;

public class GateWrapper {

	private CorpusController _qaController;
	private LoggerInterface _logger;
	private KeyValues _config;

	public GateWrapper(LoggerInterface logger, KeyValues config) {
		_logger = logger;
		_config = config;
		// initialise the GATE library
		_logger.debug("Initialising GATE...");
		try {
			Gate.setGateHome(new File(config.getString("gate_home")));
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
	public void initAnnie() throws GateException, IOException {
		_logger.debug("Initialising QAApp...");

		File annieGapp = new File(_config.getString("gateAppPath"));
		_qaController = (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);
		_logger.debug("...QA loaded");
	} // initAnnie()

	public String getSPAQRQL(String input) {
		String retString = "";
		String sparql = "", answerType = "";
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
			Set annotTypesRequired = new HashSet();
			annotTypesRequired.add("SPARQL");
			annotTypesRequired.add("AnswerType");
			Set<Annotation> collectedAnnotations = new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));

			FeatureMap features = doc.getFeatures();
			String originalContent = (String) features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
			RepositioningInfo info = (RepositioningInfo) features
					.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);
			Iterator annoIterator = collectedAnnotations.iterator();
			while (annoIterator.hasNext()) {
				Annotation currAnnot = (Annotation) annoIterator.next();
				_logger.debug("->" + currAnnot.getType());
				if (currAnnot.getType().compareTo("AnswerType") == 0) {
					answerType = currAnnot.getFeatures().get("type").toString();
				}
				if (currAnnot.getType().compareTo("SPARQL") == 0) {
					sparql = currAnnot.getFeatures().get("sparql").toString();
				}
			}

		} catch (GateException e) {
			e.printStackTrace();
		}
		GateResponse gr = new GateResponse(answerType, org.apache.commons.lang3.StringEscapeUtils.escapeJson(sparql));
		Gson gson = new Gson();
		retString = StringEscapeUtils.unescapeJava(gson.toJson(gr));
		return retString;
	}

	public static void main(String[] args) {
		SystemOutLogger logger = new SystemOutLogger();
		GateWrapper gw = new GateWrapper(logger, null);
		try {
			gw.initAnnie();
			String input;
			try {
				input = FileUtil.getFileText(new File(args[0]));
				gw.getSPAQRQL(input);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		_qaController.setCorpus(corpus);
	} // setCorpus

	/** Run ANNIE */
	public void execute() throws GateException {
		_logger.debug("Running QA...");
		_qaController.execute();
		_logger.debug("...QA complete");
	} // execute()

}

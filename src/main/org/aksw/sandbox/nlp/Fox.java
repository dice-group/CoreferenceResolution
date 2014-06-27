package org.aksw.sandbox.nlp;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.aksw.sandbox.datatypes.Entity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class Fox extends ASpotter {
	static Logger log = LoggerFactory.getLogger(Fox.class);

	private String requestURL = "http://139.18.2.164:4444/api";
	private String outputFormat = "N3";
	private String taskType = "NER";
	private String inputType = "text";

	private String doTASK(String inputText) throws MalformedURLException, IOException, ProtocolException {

		String urlParameters = "type=" + inputType;
		urlParameters += "&task=" + taskType;
		urlParameters += "&output=" + outputFormat;
		urlParameters += "&input=" + URLEncoder.encode(inputText, "UTF-8");

		return requestPOST(urlParameters, requestURL);
	}

	@Override
	public List<Entity> getEntities(String text) {
		List<Entity> list = new ArrayList<>();
		try {
			String foxJSONOutput = doTASK(text);

			JSONParser parser = new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(foxJSONOutput);
			String output = URLDecoder.decode((String) ((JSONObject) jsonArray.get(0)).get("output"), "UTF-8");

			String baseURI = "http://dbpedia.org";
			Model model = ModelFactory.createDefaultModel();
			RDFReader r = model.getReader("N3");
			r.read(model, new StringReader(output), baseURI);

			ResIterator iter = model.listSubjects();
			while (iter.hasNext()) {
				Resource next = iter.next();
				StmtIterator statementIter = next.listProperties();
				Entity ent = new Entity();
				while (statementIter.hasNext()) {
					Statement statement = statementIter.next();
					String predicateURI = statement.getPredicate().getURI();
					if (predicateURI.equals("http://www.w3.org/2000/10/annotation-ns#body")) {
						ent.label = statement.getObject().asLiteral().getString();
					} else if (predicateURI.equals("http://ns.aksw.org/scms/means")) {
						String uri = statement.getObject().asResource().getURI();
						String encode = uri.replaceAll(",", "%2C");
						ent.URI = encode;
					} else if (predicateURI.equals("http://ns.aksw.org/scms/beginIndex")) {
						ent.start = statement.getObject().asLiteral().getInt();
					} else if (predicateURI.equals("http://ns.aksw.org/scms/endIndex")) {
						ent.end = statement.getObject().asLiteral().getInt();
					}
				}
				list.add(ent);
			}

		} catch (IOException | ParseException e) {
			log.error("Could not call FOX for NER/NED", e);
		}
		return list;
	}

	public static void main(String args[]) {
		String test = "Which buildings in art deco style did Shreve, Lamb and Harmon design?";
		ASpotter fox = new Fox();
		List<Entity> list = fox.getEntities(test);
		for (Entity entity : list) {
			System.out.println("\t" + entity.label + " ->" + entity.URI + ": " + entity.start + ", " + entity.end);
		}
	}
}
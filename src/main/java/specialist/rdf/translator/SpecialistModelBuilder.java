package specialist.rdf.translator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import specialist.rdf.dm.DeliveryMechanism;
import specialist.rdf.vocabulary.Specialist;
import specialist.rdf.xml.LexRecordType;

/**
 * Takes an XML source file path and an RDF destination file path. Translates
 * the XML Specialist lexicon to RDF.
 *
 */
public class SpecialistModelBuilder {

	private String source;
	private String destination;

	public SpecialistModelBuilder(String source, String destination) {
		this.source = source;
		this.destination = destination;
	}

	public void build() throws Exception {

		// get lexRecords from XML file
		Logger.getLogger(SpecialistModelBuilder.class.getName()).log(Level.INFO,
				"Parsing the Specialist entries from " + source);
		LexRecordsFactory lexRecordsObjectCreator = new LexRecordsFactory(source);
		lexRecordsObjectCreator.createLexRecordsFromFile();
		List<LexRecordType> lexRecords = lexRecordsObjectCreator.getLexRecords();

		// create new file
		Logger.getLogger(SpecialistModelBuilder.class.getName()).log(Level.INFO, "Creating the file " + destination);
		FileWriter.createFile(destination);

		// build generic graph
		String graphUri = DeliveryMechanism.getGraphName();
		ModelBuilder builder = new ModelBuilder();
		builder.namedGraph(graphUri).subject(graphUri).add(RDF.TYPE, Specialist.LexRecords);
		builder.namedGraph(graphUri).subject(graphUri).add(RDFS.LABEL, "Specialist Lexicon");
		Model model = builder.build();
		// FileWriter.appendStringToFile(destination, model.toString());
		FileWriter.appendModelNquadsToFile(destination, model);

		for (int i = 0; i < lexRecords.size(); i++) {
			LexRecordType lexRecordType = lexRecords.get(i);

			// create RDF for lexRecordType and append to file
			LexRecordConverter lexToRdf = new LexRecordConverter(lexRecordType);
			lexToRdf.buildModel();
			Model lexRecordModel = lexToRdf.getModel();
			if (lexRecordModel != null) {
				FileWriter.appendModelNquadsToFile(destination, lexRecordModel);
			}

			// add dot to output to show progress
			if (i % 1000 == 0) {
				System.out.print(".");
			}
		}
		System.out.println();
		Logger.getLogger(SpecialistModelBuilder.class.getName()).log(Level.INFO, "Finished.");
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}
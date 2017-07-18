package specialist.rdf.dm;

import specialist.rdf.translator.SpecialistModelBuilder;

/**
 * This class carries out tasks forwarded from the CLI.
 *
 */
public class DeliveryMechanism {

	private static final String graphName = "http://www.specialist-rdf.com/lexicon#";

	public static String test() {
		return "Testing 123";
	}

	/**
	 * Read an XML file with SPECIALIST dictionary entries in it and produce RDF
	 * 
	 * @param filepath
	 * @throws Exception
	 */
	public static void newSpecialistXmlToRdfFile(String source, String destination) throws Exception {
		SpecialistModelBuilder smb = new SpecialistModelBuilder(source, destination);
		smb.build();
	}

	public static String getGraphName() {
		return graphName;
	}
}
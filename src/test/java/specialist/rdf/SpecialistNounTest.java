package specialist.rdf;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Test;

import junit.framework.TestCase;
import specialist.rdf.dm.DeliveryMechanism;
import specialist.rdf.translator.LexRecordConverter;
import specialist.rdf.translator.LexRecordsFactory;
import specialist.rdf.xml.LexRecordType;

/**
 * This class tests a general LexRecord, plus inflVars and nounEntry
 */
public class SpecialistNounTest extends TestCase {

	@Test
	public void testTest() throws Exception {

		Logger.getLogger(SpecialistNounTest.class.getName()).log(Level.INFO, "Noun Test");
		String path = "src/test/resources/noun.xml";

		LexRecordsFactory lexRecordsObjectCreator = new LexRecordsFactory(path);
		lexRecordsObjectCreator.createLexRecordsFromFile();
		List<LexRecordType> lexRecords = lexRecordsObjectCreator.getLexRecords();

		// create RDF for lexRecordType and append to file
		LexRecordConverter lexToRdf = new LexRecordConverter(lexRecords.get(0));
		lexToRdf.buildModel();
		Model model = lexToRdf.getModel();

		String ns = DeliveryMechanism.getGraphName();
		ValueFactory vf = SimpleValueFactory.getInstance();

		// http://www.specialist-rdf.com/lexicon#E0000001,
		// http://www.specialist-rdf.com/lexicon#eui,
		// "E0000001"^^<http://www.w3.org/2001/XMLSchema#string>
		IRI euiResource = vf.createIRI(ns, "E0000001");
		IRI euiProperty = vf.createIRI(ns, "eui");
		Literal eui = vf.createLiteral("E0000001");
		assertTrue(model.contains(euiResource, euiProperty, eui));

		// http://www.specialist-rdf.com/lexicon#E0000001,
		// http://www.specialist-rdf.com/lexicon#base,
		// "cotton roll gingivitis"^^<http://www.w3.org/2001/XMLSchema#string>
		IRI baseProperty = vf.createIRI(ns, "base");
		Literal base = vf.createLiteral("cotton roll gingivitis");
		assertTrue(model.contains(euiResource, baseProperty, base));

		// http://www.specialist-rdf.com/lexicon#E0000001,
		// http://www.specialist-rdf.com/lexicon#cat,
		// "NOUN"^^<http://www.w3.org/2001/XMLSchema#string>
		IRI catProperty = vf.createIRI(ns, "cat");
		Literal cat = vf.createLiteral("NOUN");
		assertTrue(model.contains(euiResource, catProperty, cat));

		// http://www.specialist-rdf.com/lexicon#E0000001,
		// http://www.specialist-rdf.com/lexicon#spellingVar, "cotton-roll
		// gingivitis"^^<http://www.w3.org/2001/XMLSchema#string>
		IRI spellingVarProperty = vf.createIRI(ns, "spellingVar");
		Literal spellingVar = vf.createLiteral("cotton-roll gingivitis");
		assertTrue(model.contains(euiResource, spellingVarProperty, spellingVar));
	}
}
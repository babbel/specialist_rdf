package specialist.rdf.translator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import specialist.rdf.xml.LexRecordType;
import specialist.rdf.xml.LexRecordsType;

/**
 * Creates a list of Java objects of type LexRecord from a well-formed XML file
 * using JAXB. Checks the XML input file against the lexRecord.xsd file
 *
 */
public class LexRecordsFactory {

	private File file;
	private LexRecordsType lex;

	public LexRecordsFactory(String filepath) throws IOException {

		if (filepath == null) {
			throw new NullPointerException("File path was null.");
		}

		file = new File(filepath);

		if (!file.exists()) {
			throw new IOException("File was not found: " + filepath);
		}
	}

	public void createLexRecordsFromFile() throws JAXBException {

		Logger.getLogger(LexRecordsFactory.class.getName()).log(Level.INFO, "Get Java objects from " + file.getName());

		final JAXBContext context = JAXBContext.newInstance(LexRecordsType.class);
		final Unmarshaller unmarshaller = context.createUnmarshaller();

		@SuppressWarnings("unchecked")
		JAXBElement<LexRecordsType> lexicon = (JAXBElement<LexRecordsType>) unmarshaller.unmarshal(file);
		setLex(lexicon.getValue());
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public LexRecordsType getLex() {
		return lex;
	}

	public void setLex(LexRecordsType lex) {
		this.lex = lex;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<LexRecordType> getLexRecords() {
		return lex.getLexRecord();
	}
}
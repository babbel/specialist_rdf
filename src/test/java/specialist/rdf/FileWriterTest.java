package specialist.rdf;

import java.io.File;

import org.junit.Test;

import junit.framework.TestCase;
import specialist.rdf.translator.FileWriter;

/**
 * This class tests a general LexRecord, plus inflVars and nounEntry
 */
public class FileWriterTest extends TestCase {

	@Test
	public void testCreateFile() throws Exception {
		String filePath = "src/test/resources/test.txt";
		FileWriter.createFile(filePath);
		File file = new File(filePath);
		assertTrue(file.exists());
		file.delete();
	}
}
package specialist.rdf.translator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

/**
 * File interface. Can create a new empty file or append NQuads to an existing file.
 *
 */
public class FileWriter {

	public static void createFile(String path) throws IOException {
		File f = new File(path);

		File parent = f.getParentFile();
		if (parent != null) {
			f.getParentFile().mkdirs();
		}

		f.createNewFile();
	}

	public static void appendModelNquadsToFile(String pathString, Model model) throws IOException {
		FileOutputStream out = new FileOutputStream(pathString, true);
		Rio.write(model, out, RDFFormat.NQUADS);
		out.close();
	}
}
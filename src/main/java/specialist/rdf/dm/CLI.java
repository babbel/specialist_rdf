package specialist.rdf.dm;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This is the command line interface for the JAR and forwards to the correct
 * method in the DeliveryMechanism class.
 *
 */
public class CLI {
	private static final String NAME = CLI.class.getCanonicalName();

	public static void main(String[] args) throws Exception {

		System.out.println("Running the CLI...");

		CommandLine lvCmd = null;
		HelpFormatter lvFormater = new HelpFormatter();
		CommandLineParser lvParser = new DefaultParser();
		Options lvOptions = new Options();

		Option lvHelp = new Option("h", "help", false,
				"Enter a source XML file path (-s) and a destination RDF file path (-d).");
		lvOptions.addOption(lvHelp);

		Option lvTest = new Option("t", "test", false, "Test method to check functionality of CLI.");
		lvOptions.addOption(lvTest);

		lvOptions.addOption("s", "source", true, "The path of the input file.");
		String source = null;

		lvOptions.addOption("d", "destination", true, "The path of the output file.");
		String destination = null;

		try {
			lvCmd = lvParser.parse(lvOptions, args);

			if (lvCmd.hasOption('h')) {
				lvFormater.printHelp(NAME, lvOptions);
			}

			if (lvCmd.hasOption('t')) {
				System.out.println(DeliveryMechanism.test());
			}

			if (lvCmd.hasOption("s") && lvCmd.hasOption("d")) {
				source = lvCmd.getOptionValue("s");
				destination = lvCmd.getOptionValue("d");

				DeliveryMechanism.newSpecialistXmlToRdfFile(source, destination);
			}

		} catch (ParseException pvException) {
			lvFormater.printHelp(NAME, lvOptions);
			System.out.println("Error:" + pvException.getMessage());
		}
	}
}
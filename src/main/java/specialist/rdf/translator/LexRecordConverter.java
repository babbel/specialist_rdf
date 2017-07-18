package specialist.rdf.translator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import specialist.rdf.dm.DeliveryMechanism;
import specialist.rdf.vocabulary.Specialist;
import specialist.rdf.xml.AdjEntryType;
import specialist.rdf.xml.AdvEntryType;
import specialist.rdf.xml.AuxEntryType;
import specialist.rdf.xml.CatType;
import specialist.rdf.xml.DetEntryType;
import specialist.rdf.xml.GenderType;
import specialist.rdf.xml.InflType;
import specialist.rdf.xml.InflVarsType;
import specialist.rdf.xml.LexRecordType;
import specialist.rdf.xml.ModalEntryType;
import specialist.rdf.xml.ModalVar;
import specialist.rdf.xml.NegativeType;
import specialist.rdf.xml.NounEntryType;
import specialist.rdf.xml.PositionType;
import specialist.rdf.xml.PronEntryType;
import specialist.rdf.xml.PronType;
import specialist.rdf.xml.PronVariant;
import specialist.rdf.xml.TypeType;
import specialist.rdf.xml.VerbEntryType;

/**
 * Takes a LexRecordType object and returns an RDF4J Model.
 *
 */
public class LexRecordConverter {

	private LexRecordType lexRecordType;
	private Model model;

	private ModelBuilder builder;
	private ValueFactory valueFactory;
	private String graphName;

	public LexRecordConverter(LexRecordType lex) {

		this.lexRecordType = lex;
		this.model = null;

		builder = new ModelBuilder();
		valueFactory = SimpleValueFactory.getInstance();
		graphName = DeliveryMechanism.getGraphName();
	}

	/**
	 * Builds the RDF model from the LexRecordsType.
	 * 
	 * @return
	 * @throws Exception
	 */
	public void buildModel() throws Exception {

		String eui = lexRecordType.getEui();
		String lexRecordUri = graphName + eui;
		IRI lexRecordResource = valueFactory.createIRI(lexRecordUri);

		builder.namedGraph(graphName).subject(graphName).add(Specialist.hasLexRecord, lexRecordResource);
		builder.namedGraph(graphName).subject(lexRecordUri).add(RDF.TYPE, Specialist.LexRecord);
		builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.eui, eui);

		String base = lexRecordType.getBase();
		builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.base, base);
		builder.namedGraph(graphName).subject(lexRecordUri).add(RDFS.LABEL, base);

		String signature = lexRecordType.getSignature();
		if (signature != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.signature, signature);
		}

		List<String> abbreviations = lexRecordType.getAbbreviations();
		for (String abbrev : abbreviations) {
			processAbbreviation(abbrev, lexRecordUri);
		}

		List<String> acronyms = lexRecordType.getAcronyms();
		for (String acro : acronyms) {
			processAcronym(acro, lexRecordUri);
		}

		List<String> annotations = lexRecordType.getAnnotations();
		for (String anno : annotations) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.annotation, anno);
		}

		List<String> spellingVars = lexRecordType.getSpellingVars();
		for (String var : spellingVars) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.spellingVar, var);
		}

		AdjEntryType adjEntry = lexRecordType.getAdjEntry();
		if (adjEntry != null) {
			processAdjEntry(adjEntry, lexRecordUri);
		}

		AdvEntryType advEntry = lexRecordType.getAdvEntry();
		if (advEntry != null) {
			processAdvEntry(advEntry, lexRecordUri);
		}

		AuxEntryType auxEntry = lexRecordType.getAuxEntry();
		if (auxEntry != null) {
			processAuxEntry(auxEntry, lexRecordUri);
		}

		CatType cat = lexRecordType.getCat();
		if (cat != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.cat, cat.toString());
		}

		DetEntryType detEntry = lexRecordType.getDetEntry();
		if (detEntry != null) {
			processDetEntry(detEntry, lexRecordUri);
		}

		ModalEntryType modalEntry = lexRecordType.getModalEntry();
		if (modalEntry != null) {
			processModalEntry(modalEntry, lexRecordUri);
		}

		NounEntryType nounEntry = lexRecordType.getNounEntry();
		if (nounEntry != null) {
			processNounEntry(nounEntry, lexRecordUri);
		}

		PronEntryType pronEntry = lexRecordType.getPronEntry();
		if (pronEntry != null) {
			processPronounEntry(pronEntry, lexRecordUri);
		}

		VerbEntryType verbEntry = lexRecordType.getVerbEntry();
		if (verbEntry != null) {
			processVerbEntry(verbEntry, lexRecordUri);
		}

		List<InflVarsType> inflVars = lexRecordType.getInflVars();
		if (inflVars != null) {
			processInflVarsEntry(inflVars, lexRecordUri);
		}

		model = builder.build();
	}

	/**
	 * inflVars in the Specialist lexicon is relevant to any word category
	 * 
	 * Inflectional variants is an RDF node of type Specialist.InflVar and
	 * contains the information for that particular inflectional variant in
	 * datatype properties
	 * 
	 * @param inflVars
	 * @param lexRecordUri
	 * @throws Exception
	 */
	private void processInflVarsEntry(List<InflVarsType> inflVars, String lexRecordUri) throws Exception {

		// <inflVars cat="noun" cit="abdominal epilepsy"
		// eui="E0006454" infl="singular" type="basic"
		// unInfl="abdominal epilepsy">
		// abdominal epilepsy</inflVars>

		for (int i = 0; i < inflVars.size(); i++) {

			InflVarsType inflVar = inflVars.get(i);
			CatType cat = inflVar.getCat();
			String cit = inflVar.getCit();
			String eui = inflVar.getEui();
			String uninfl = inflVar.getUnInfl();
			String value = inflVar.getValue();

			InflType infl = inflVar.getInfl();
			String inflType = inflVar.getType();

			// TODO: Parse irregular inflection types
			// e.g. group(irreg|corps|corps|)
			// although these should be the same as the irregular
			// inflectional variants anyways

			String inflVarsUri = lexRecordUri + "-inflecition-" + i;
			IRI inflVarResource = valueFactory.createIRI(inflVarsUri);

			builder.namedGraph(graphName).subject(inflVarsUri).add(Specialist.inflType, inflType);
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.hasInflVar, inflVarResource);
			builder.namedGraph(graphName).subject(inflVarsUri).add(RDF.TYPE, Specialist.InflVar);
			builder.namedGraph(graphName).subject(inflVarsUri).add(RDFS.LABEL, value);
			builder.namedGraph(graphName).subject(inflVarsUri).add(Specialist.cat, cat.toString());
			builder.namedGraph(graphName).subject(inflVarsUri).add(Specialist.cit, cit);
			builder.namedGraph(graphName).subject(inflVarsUri).add(Specialist.eui, eui);
			builder.namedGraph(graphName).subject(inflVarsUri).add(Specialist.infl, infl.toString());
			builder.namedGraph(graphName).subject(inflVarsUri).add(Specialist.unInfl, uninfl);
			builder.namedGraph(graphName).subject(inflVarsUri).add(Specialist.value, value);
		}
	}

	private void processVerbEntry(VerbEntryType verbEntry, String lexRecordUri) {

		// <verbEntry>
		// <variants>reg</variants>
		// <tran>np</tran>
		// <nominalization>Doppler|noun|E0746646</nominalization>
		// </verbEntry>

		List<JAXBElement<String>> complementType = verbEntry.getComplementType();
		for (int i = 0; i < complementType.size(); i++) {
			JAXBElement<String> jaxbElement = complementType.get(i);
			String complementUri = lexRecordUri + "-complement-" + i;
			IRI complementResource = valueFactory.createIRI(complementUri);

			String name = jaxbElement.getName().toString();
			String value = jaxbElement.getValue();

			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.hasComplement, complementResource);
			builder.namedGraph(graphName).subject(complementUri).add(RDF.TYPE, Specialist.Complement);
			builder.namedGraph(graphName).subject(complementUri).add(RDFS.LABEL, name);
			builder.namedGraph(graphName).subject(complementUri).add(Specialist.name, name);
			builder.namedGraph(graphName).subject(complementUri).add(Specialist.value, value);
			// TODO: Could parse the value of the complement, e.g.
			// "pphr(from,np)"
		}

		List<String> nom = verbEntry.getNominalization();
		for (String n : nom) {
			processNominalization(n, lexRecordUri);
		}

		// TODO: Parse irregular verbs
		// although these should be the same as the irregular
		// inflectional variants anyways

		// regd
		// reg
		// irreg|abide|abides|abided|abided|abiding|
		// irreg|beget|begets|begot|begotten|begetting|
		// irreg|begin|begins|began|begun|beginning|
		// irreg|behold|beholds|beheld|beheld|beholding|
		// ...
		List<String> variants = verbEntry.getVariants();
		for (String v : variants) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.variant, v);
		}
	}

	private void processPronounEntry(PronEntryType pronEntry, String lexRecordUri) {

		// genderTypeType in lexRecords.xsd
		GenderType gender = pronEntry.getGender();
		if (gender != null) { // gender is optional
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.gender, gender.getType().toString());
		}

		Object interrogative = pronEntry.getInterrogative();
		if (interrogative != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.interrogative, true);
		}

		List<PronType> type = pronEntry.getType();
		for (PronType t : type) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.pronType, t.toString());
		}

		List<PronVariant> variants = pronEntry.getVariants();
		for (PronVariant v : variants) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.variant, v.toString());
		}
	}

	private void processNounEntry(NounEntryType nounEntry, String lexRecordUri) {

		List<String> compl = nounEntry.getCompl();
		for (String c : compl) {
			processComp(c, lexRecordUri);
		}

		List<String> nom = nounEntry.getNominalization();
		for (String n : nom) {
			processNominalization(n, lexRecordUri);
		}

		// <proper/>
		Object proper = nounEntry.getProper();
		if (proper != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.proper, true);
		}

		// <trademark/>
		Object trademark = nounEntry.getTrademark();
		if (trademark != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.trademark, true);
		}

		// <base>OcuCoat</base>
		// <tradeName>hydroxypropyl methylcellulose</tradeName>
		String tradename = nounEntry.getTradeName();
		if (tradename != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.tradeName, tradename);
		}

		// TODO: Could parse irregular variants, but should be same as inflVars
		// anyways, e.g.
		// group(irreg|Gully|Gullys|) => group(irreg|<sg>|<pl>|)
		// irreg|Bachelor of Surgery|Bachelors of Surgery| => irreg|<sg>|<pl>|
		List<String> variants = nounEntry.getVariants();
		for (String v : variants) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.variant, v);
		}
	}

	private void processNominalization(String n, String lexRecordUri) {

		// e.g. <nominalization>detorsion|noun|E0070447</nominalization>

		String[] parts = n.split("\\|");
		if (parts.length > 1) {
			String nUri = graphName + parts[2];
			IRI nResource = valueFactory.createIRI(nUri);
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.nominalization, nResource);
		} else {
			Logger.getLogger(LexRecordConverter.class.getName()).log(Level.WARNING,
					lexRecordUri + " Could not process nominalization " + n);
		}
	}

	private void processModalEntry(ModalEntryType modalEntry, String lexRecordUri) {
		List<ModalVar> variants = modalEntry.getVariant();
		for (ModalVar v : variants) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.variant, v.toString());
		}
	}

	private void processDetEntry(DetEntryType detEntry, String lexRecordUri) {

		// <demonstrative/>
		Object demonstrative = detEntry.getDemonstrative();
		if (demonstrative != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.demonstrative, true);
		}

		// <interrogative/>
		Object interrogative = detEntry.getInterrogative();
		if (interrogative != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.interrogative, true);
		}

		// Possible values of variants:
		// sing
		// plur
		// free
		// pluruncount
		// uncount
		// singuncount
		String variants = detEntry.getVariants();
		builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.variant, variants);
	}

	private void processAuxEntry(AuxEntryType auxEntry, String lexRecordUri) {

		// TODO: Parse variant strings
		// be;infinitive
		// is;pres(thr_sing)
		// 's;pres(thr_sing)
		// isn't;pres(thr_sing):negative
		// are;pres(fst_plur,second,thr_plur)
		// 're;pres(fst_plur,second,thr_plur)
		// aren't;pres(fst_plur,second,thr_plur):negative
		// am;pres(fst_sing)
		// 'm;pres(fst_sing)
		// was;past(fst_sing,thr_sing)
		// wasn't;past(fst_sing,thr_sing):negative
		// were;past(fst_plur,second,thr_plur)
		// weren't;past(fst_plur,second,thr_plur):negative
		// been;past_part
		// being;pres_part
		// do;pres(fst_sing,fst_plur,second,thr_plur)
		// don't;pres(fst_sing,fst_plur,second,thr_plur):negative
		// does;pres(thr_sing)
		// doesn't;pres(thr_sing):negative
		// did;past
		// didn't;past:negative
		// have;infinitive
		// have;pres(fst_sing,fst_plur,second,thr_plur)
		// has;pres(thr_sing)
		// had;past
		// having;pres_part
		// hadn't;past:negative
		// hasn't;pres(thr_sing):negative
		// haven't;pres(fst_sing,fst_plur,second,thr_plur):negative
		// 've;pres(fst_sing,fst_plur,second,thr_plur)
		// 'd;past
		List<String> variants = auxEntry.getVariant();
		for (String v : variants) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.variant, v);
		}
	}

	private void processAdvEntry(AdvEntryType advEntry, String lexRecordUri) {

		// Logger.getLogger(SpecialistModelBuilder.class.getName()).log(Level.INFO,
		// "Processing adverb entry " + lexRecordUri);

		// <interrogative/>
		Object interrogative = advEntry.getInterrogative();
		if (interrogative != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.interrogative, true);
		}

		List<String> modification = advEntry.getModification();
		for (String m : modification) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.modification, m);
		}

		// negative equals negativeTypeType in lexRecord.xsd
		NegativeType negative = advEntry.getNegative();
		if (negative != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.negative, negative.getType().toString());
		}

		// TODO: Parse possible values for adverb variants
		// inv
		// inv;periph
		// reg
		// irreg|far|further|furthest|
		// irreg|far|farther|farthest|
		// irreg|less|less|least|
		// irreg|thin|thinner|thinnest|
		// irreg|well|better|best|
		// irreg|much|more|most|
		// irreg|bad|worse|worst|
		List<String> variants = advEntry.getVariants();
		for (String v : variants) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.variant, v);
		}
	}

	private void processAbbreviation(String abbrev, String lexRecordUri) {

		// <abbreviations>plakoglobin|E0309147</abbreviations>

		String[] parts = abbrev.split("\\|");
		if (parts.length > 1) {
			String abbrevUri = graphName + parts[1];
			IRI abbrevResource = valueFactory.createIRI(abbrevUri);
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.abbreviation, abbrevResource);
		} else {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.abbreviation, abbrev);
		}
	}

	private void processAcronym(String acro, String lexRecordUri) {

		// <acronyms>polyethylene glycol 20M|E0699305</acronyms>

		String[] parts = acro.split("\\|");
		if (parts.length > 1) {
			String acrUri = graphName + parts[1];
			IRI acroResource = valueFactory.createIRI(acrUri);
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.acronym, acroResource);
		} else {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.acronym, acro);
		}
	}

	private void processAdjEntry(AdjEntryType adjEntry, String lexRecordUri) {

		List<String> compl = adjEntry.getCompl();
		for (String c : compl) {
			processComp(c, lexRecordUri);
		}

		List<String> nom = adjEntry.getNominalization();
		for (String n : nom) {
			processNominalization(n, lexRecordUri);
		}

		List<PositionType> position = adjEntry.getPosition();
		for (PositionType p : position) {
			TypeType positionType = p.getType();
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.position, positionType.toString());
		}

		// <stative/>
		Object stative = adjEntry.getStative();
		if (stative != null) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.stative, true);
		}

		// inv
		// inv;periph
		// reg
		// regd
		// irreg|bad|worse|worst|
		// irreg|cruel|crueller|cruellest|
		// irreg|damned|damneder|damndest|
		// irreg|far|further|furthest|
		// irreg|far|farther|farthest|
		// irreg|good|better|best|
		// irreg|gooey|gooier|gooiest|
		// irreg|high-density|higher-density|highest-density|
		// irreg|high density|higher density|highest density|
		// irreg|high-efficiency|higher-efficiency|highest-efficiency|
		// irreg|high-priority|higher-priority|highest-priority|
		// irreg|high-quality|higher-quality|highest-quality|
		// irreg|high-resistance|higher-resistance|highest-resistance|
		// irreg|high-risk|higher-risk|highest-risk|
		// irreg|high risk|higher risk|highest risk|
		// irreg|ill|worse|worst|
		// irreg|long-lived|longer-lived|longest-lived|
		// irreg|long-term|longer-term|longest-term|
		// irreg|long-term|longer term|longest term|
		List<String> variants = adjEntry.getVariants();
		for (String v : variants) {
			builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.variant, v);
		}
	}

	private void processComp(String c, String lexRecordUri) {

		// TODO: Could parse complements
		// pphr = prepositional phrase
		// <compl>pphr(of,np)</compl>
		// <compl>pphr(of,np|heart rate variability|)</compl>

		builder.namedGraph(graphName).subject(lexRecordUri).add(Specialist.compl, c);
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public String getGraphName() {
		return graphName;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public ModelBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(ModelBuilder builder) {
		this.builder = builder;
	}

	public ValueFactory getValueFactory() {
		return valueFactory;
	}

	public void setValueFactory(ValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}

	public LexRecordType getLexRecordType() {
		return lexRecordType;
	}

	public void setLexRecordType(LexRecordType lexRecordType) {
		this.lexRecordType = lexRecordType;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
}
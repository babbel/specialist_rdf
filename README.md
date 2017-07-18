# The SPECIALIST Lexicon RDF Translator

This code translates the SPECIALIST lexicon from XML to RDF. It has been tested on a MacBook Pro running OS X El Capitan (10.11.6) and Java SE 1.8.

## Compiling
Download [RDF4J 2.2.2 onejar](http://rdf4j.org/download/), save to the `lib` folder, and install using Maven:

```
mvn install:install-file -DgroupId=org.eclipse.rdf4j -DartifactId=rdf4j \
-Dversion=2.2.2 -Dpackaging=jar -Dfile=lib/eclipse-rdf4j-2.2.2-onejar.jar \
-DgeneratePom=true
```

Run `mvn clean install` to create the JAR file.

Download the SPECIALIST lexicon 2017 XML file from `https://lexsrv3.nlm.nih.gov/LexSysGroup/Projects/lexicon/2017/release/LEX/XML/LEXICON.xml`. 

## Running
Run with `java -jar specialist-rdf-jar-with-dependencies.jar -s <source XML file> -d <destination NQuads file>`

## Dependencies
This code includes a modified version of the [SPECIALIST lexicon XSD schemas](https://lsg3.nlm.nih.gov/LexSysGroup/Projects/lexicon/2017/docs/designDoc/UDF/lexRecord/xml/) and uses the dependencies in the table below:

| Name                            | Licence  |
| ------------------------------- | -------- |
| [SPECIALIST lexicon 2017 release](https://lexsrv3.nlm.nih.gov/LexSysGroup/Projects/lexicon/current/web/release/2017.html) | [Terms and Conditions for Use of the SPECIALIST NLP Tools](https://lexsrv3.nlm.nih.gov/LexSysGroup/docs/TermsAndConditions.html) |
| [Sesame-Schemagen](https://github.com/outofbits/sesame-schemagen) | [MIT License](https://github.com/outofbits/sesame-schemagen/blob/master/LICENSE.txt) |
| [RDF4J](http://rdf4j.org/) | [Eclipse Distribution License - v 1.0](http://www.eclipse.org/legal/epl-v10.html) |
| [JUnit 4](http://junit.org/junit4/) | [Eclipse Public License - v 1.0](http://www.eclipse.org/legal/epl-v10.html) |
| [JAXB2](http://www.mojohaus.org/jaxb2-maven-plugin/Documentation/v2.2/) | [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) |
| [Apache Maven](https://maven.apache.org/) |  [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) |
| [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/) | [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) |
| [SLF4J](https://www.slf4j.org) | [MIT License](https://www.slf4j.org/license.html) |
| [log4j](https://logging.apache.org/log4j/2.x/) | [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) |


## TODOs
Some strings in the lexicon could be meaningfully parsed to produce additional links, e.g. `<compl>pphr(of,np)</compl>`. These have been left as TODOs.
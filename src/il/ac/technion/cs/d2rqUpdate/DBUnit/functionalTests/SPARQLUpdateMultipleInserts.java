/*
   Copyright 2010 Technion - Israel Institute of Technology

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 
 */
package il.ac.technion.cs.d2rqUpdate.DBUnit.functionalTests;

import il.ac.technion.cs.d2rqUpdate.StatementBuilder;
import il.ac.technion.cs.d2rqUpdate.DBUnit.D2RQUpdateTestSuite;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vadim Eisenberg <Vadim.Eisenberg@gmail.com>
 *
 */
public class SPARQLUpdateMultipleInserts extends SPARQLUpdateTest {

	private final String[] NEW_CONFERENCES_TRIPLES = new String[] {
						// add ISWC 2006
						"<http://test/conferences/23543> <"
								+ D2RQUpdateTestSuite.DIRECTORY_BASE_URI
								+ "/vocab/startDate> "
								+ "'2006-11-05T00:00:00'^^xsd:dateTime",
						"<http://test/conferences/23543> rdfs:label "
								+ "'International Semantic Web Conference 2006'",
						"<http://test/conferences/23543> dc:date 'November 5-11 2006'",
						"<http://test/conferences/23543> iswc:location 'Athens, Georgia'",
						// add W3C Workshop on RDF Access to Relational
						// Databases
						"<http://test/conferences/23544> <"
								+ D2RQUpdateTestSuite.DIRECTORY_BASE_URI
								+ "/vocab/startDate> "
								+ "'2007-10-25T00:00:00'^^xsd:dateTime",
						"<http://test/conferences/23544> rdfs:label "
								+ "'W3C Workshop on RDF Access to Relational Databases'",
						"<http://test/conferences/23544> dc:date 'October 25-26  2007'",
						"<http://test/conferences/23544> iswc:location ' Cambridge, MA'", };

	/**
	 * @param name
	 */
	public SPARQLUpdateMultipleInserts(String name) {
		super(name);
	}

	public void testSPARQLInsertConferences() {
		SPARQLTestInsertMultipleTriples(Arrays.asList(NEW_CONFERENCES_TRIPLES));
	}

	public void testSPARQLInsertOrganizations() {
		SPARQLTestInsertMultipleTriples(Arrays
				.asList(NEW_ORGANIZATIONS_TRIPLES));
	}

	public void testSPARQLInsertTopics() {
		SPARQLTestInsertMultipleTriples(Arrays.asList(NEW_TOPICS_TRIPLES));
	}

	public void testSPARQLInsertPersons() {
		SPARQLTestInsertMultipleTriples(Arrays.asList(NEW_PERSONS_TRIPLES));
	}

	public void testSPARQLInsertPapers() {
		SPARQLTestInsertMultipleTriples(Arrays.asList(NEW_PAPERS_TRIPLES));
	}

	public void testSPARQLInsertPapersConferences() {
		List<String> triples = new LinkedList<String>();
		triples.addAll(Arrays.asList(NEW_CONFERENCES_TRIPLES));
		triples.addAll(Arrays.asList(NEW_PAPERS_TRIPLES));
	
		// add new triples that link between the new triples
		triples
				.add("<http://test/papers/9> iswc:conference <http://test/conferences/23544>");
	
		SPARQLTestInsertMultipleTriples(triples);
	
	}

	public void testSPARQLInsertPapersTopics() {
		List<String> triples = new LinkedList<String>();
	
		triples.addAll(Arrays.asList(NEW_TOPICS_TRIPLES));
		triples.addAll(Arrays.asList(NEW_PAPERS_TRIPLES));
	
		// add new triples that link between the new triples
	
		triples
				.add("<http://test/papers/9> skos:primarySubject <http://test/topics/17>");
	
		SPARQLTestInsertMultipleTriples(triples);
	
	}

	public void testSPARQLInsertPapersPersons() {
		List<String> triples = new LinkedList<String>();
	
		triples.addAll(Arrays.asList(NEW_PAPERS_TRIPLES));
		triples.addAll(Arrays.asList(NEW_PERSONS_TRIPLES));
	
		// add new triples that link between the new triples
	
		triples
				.add("<http://test/papers/9> dc:creator <http://test/persons/14>");
	
		SPARQLTestInsertMultipleTriples(triples);
	
	}

	public void testSPARQLInsertPersonsTopics() {
		List<String> triples = new LinkedList<String>();
		triples.addAll(Arrays.asList(NEW_TOPICS_TRIPLES));
		triples.addAll(Arrays.asList(NEW_PERSONS_TRIPLES));
	
		// add new triples that link between the new triples
		triples
				.add("<http://test/persons/14> iswc:research_interests <http://test/topics/17>");
	
		SPARQLTestInsertMultipleTriples(triples);
	
	}

	public void testSPARQLInsertPersonsOrganizations() {
		List<String> triples = new LinkedList<String>();
		triples.addAll(Arrays.asList(NEW_ORGANIZATIONS_TRIPLES));
		triples.addAll(Arrays.asList(NEW_PERSONS_TRIPLES));
	
		// add new triples that link between the new triples
	
		triples
				.add("<http://test/persons/14> iswc:has_affiliation <http://test/organizations/11>");
	
		SPARQLTestInsertMultipleTriples(triples);
	
	}

	public void testSPARQLInsertMultipleTriples() {
		List<String> triples = new LinkedList<String>();
		triples.addAll(Arrays.asList(NEW_CONFERENCES_TRIPLES));
		triples.addAll(Arrays.asList(NEW_ORGANIZATIONS_TRIPLES));
		triples.addAll(Arrays.asList(NEW_TOPICS_TRIPLES));
		triples.addAll(Arrays.asList(NEW_PAPERS_TRIPLES));
		triples.addAll(Arrays.asList(NEW_PERSONS_TRIPLES));
	
		// add new triples that link between the new triples
		triples
				.add("<http://test/persons/14> iswc:research_interests <http://test/topics/17>");
		triples
				.add("<http://test/papers/9> iswc:conference <http://test/conferences/23544>");
		triples
				.add("<http://test/papers/9> skos:primarySubject <http://test/topics/17>");
		triples
				.add("<http://test/papers/9> dc:creator <http://test/persons/14>");
		triples
				.add("<http://test/persons/14> iswc:has_affiliation <http://test/organizations/11>");
	
		SPARQLTestInsertMultipleTriples(triples);
	
	}

	private void SPARQLTestInsertMultipleTriples(List<String> triples) {
		// check that none of the triples is in the database
		for (String triple : triples) {
			boolean askResult = sparqlAsk("ASK { " + triple + "}");
			assertFalse(triple, askResult);
		}
	
		StringBuffer tripleString = new StringBuffer();
		Collections.shuffle(triples);
		StatementBuilder.appendSeparatedStrings(tripleString, triples, ".");
		System.out.println("tripleString = " + tripleString);
		sparqlUpdate("INSERT {" + tripleString + "}");
	
		// check that all the triples is in the database
		for (String triple : triples) {
			boolean askResult = sparqlAsk("ASK { " + triple + "}");
			assertTrue(triple, askResult);
		}
	
		// return the state back
		tripleString.setLength(0);
		Collections.shuffle(triples);
		StatementBuilder.appendSeparatedStrings(tripleString, triples, ".");
		System.out.println("tripleString = " + tripleString);
		sparqlUpdate("DELETE {" + tripleString + "}");
	
		// check that none of the triples is in the database
		for (String triple : triples) {
			boolean askResult = sparqlAsk("ASK { " + triple + "}");
			assertFalse(triple, askResult);
		}
	}

}

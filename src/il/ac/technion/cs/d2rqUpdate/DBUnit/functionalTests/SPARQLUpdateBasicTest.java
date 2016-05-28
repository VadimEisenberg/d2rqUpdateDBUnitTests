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

import il.ac.technion.cs.d2rqUpdate.D2RQUpdateException;

import java.util.List;

import org.dbunit.assertion.Difference;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.d2rq.vocab.ISWC;
import de.fuberlin.wiwiss.d2rq.vocab.SKOS;

/**
 * @author Vadim Eisenberg <Vadim.Eisenberg@gmail.com>
 *
 */
public class SPARQLUpdateBasicTest extends SPARQLUpdateTest {

	/**
	 * @param name
	 */
	public SPARQLUpdateBasicTest(String name) {
		super(name);
	}


	public void testSPARQLFetch() {
		sparql("SELECT ?x ?y WHERE { <http://test/papers/1> ?x ?y }");
		// dump();

		expectVariable("x", ISWC.conference);
		expectVariable("y", this.model
				.createResource("http://test/conferences/23541"));
		assertSolution();

		expectVariable("x", SKOS.primarySubject);
		expectVariable("y", this.model.createResource("http://test/topics/5"));
		assertSolution();

		expectVariable("x", SKOS.subject);
		expectVariable("y", this.model.createResource("http://test/topics/15"));
		assertSolution();

		expectVariable("x", DC.date);
		expectVariable("y", this.model.createTypedLiteral("2002",
				XSDDatatype.XSDgYear));
		assertSolution();

		expectVariable("x", DC.title);
		expectVariable("y", this.model.createLiteral(
				"Trusting Information Sources One Citizen at a Time", "en"));
		assertSolution();

		expectVariable("x", RDF.type);
		expectVariable("y", ISWC.InProceedings);
		assertSolution();

		assertResultCount(12);
	}

	public void testSPARQLDelete() {
		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("310-448-8794"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("310-448-8794", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/persons/1> iswc:phone "
				+ "'310-448-8794' }");

		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "310-448-8794");
		assertEquals(difference.getActualValue(), null);
	}

	public void testSPARQLInsert() {
		sparql("SELECT ?phone WHERE { <http://test/persons/2> "
				+ "iswc:phone ?phone }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 2");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/persons/2> iswc:phone "
				+ "'123-456-7891' }");

		sparql("SELECT ?phone WHERE { <http://test/persons/2> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("123-456-7891"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 2");
			assertEquals("123-456-7891", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), null);
		assertEquals(difference.getActualValue(), "123-456-7891");
	}

	public void testSPARQLDeleteNonExistentValueAndInsert() {
		sparql("SELECT ?phone WHERE { <http://test/persons/2> "
				+ "iswc:phone ?phone }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 2");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		// Delete non existent value and insert
		sparqlUpdate("MODIFY DELETE { <http://test/persons/2> iswc:phone "
				+ "'111-222-4444' }"
				+ "INSERT { <http://test/persons/2> iswc:phone "
				+ "'123-456-7891' }");

		sparql("SELECT ?phone WHERE { <http://test/persons/2> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("123-456-7891"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 2");
			assertEquals("123-456-7891", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), null);
		assertEquals(difference.getActualValue(), "123-456-7891");
	}

	public void testSPARQLDeleteNonExistentSubjectAndInsert() {
		sparql("SELECT ?phone WHERE { <http://test/persons/17> "
				+ "iswc:phone ?phone }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 17");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		// Delete non existent value and insert
		sparqlUpdate("MODIFY DELETE { <http://test/persons/17> iswc:phone "
				+ "'111-222-4444' }"
				+ "INSERT { <http://test/persons/17> iswc:phone "
				+ "'123-456-7891' }");

		sparql("SELECT ?phone WHERE { <http://test/persons/17> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("123-456-7891"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 17");
			assertEquals("123-456-7891", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}
	}

	public void testSPARQLDeleteInsert() {
		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("310-448-8794"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("310-448-8794", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("MODIFY DELETE { <http://test/persons/1> iswc:phone "
				+ "'310-448-8794' }"
				+ "INSERT { <http://test/persons/1> iswc:phone "
				+ "'123-456-7891' }");

		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("123-456-7891"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("123-456-7891", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "310-448-8794");
		assertEquals(difference.getActualValue(), "123-456-7891");
	}

	// insert a new value to a tuple which has the value not NULL
	// without deleting the previous value
	// this operation should fail
	public void testSPARQLInsertNewValueWithoutPreviousDelete() {
		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("310-448-8794"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("310-448-8794", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		try {
			sparqlUpdate("INSERT { <http://test/persons/1> iswc:phone "
					+ "'123-356-7891' }");
			fail();
		} catch (D2RQUpdateException exception) {
			assertEquals(D2RQUpdateException.SQL_STATEMENT_FAILED, exception
					.errorCode());
			String errorPrefix = "SQL UPDATE TO NON NULL VALUE";
			assertEquals("SQL UPDATE TO NON NULL VALUE", exception.getMessage()
					.substring(0, errorPrefix.length()));
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("310-448-8794"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("310-448-8794", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(0, differences.size());

	}

	// insert an existent triple - nothing happens
	public void testSPARQLInsertExistentTriple() {
		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("310-448-8794"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("310-448-8794", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/persons/1> iswc:phone "
				+ " '310-448-8794' }");

		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("310-448-8794"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("310-448-8794", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}
		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(0, differences.size());
	}

	// delete a non existent triple - nothing happens
	public void testSPARQLDeleteNonExistentTriple() {
		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("310-448-8794"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("310-448-8794", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/persons/1> iswc:phone "
				+ " '123-456-789' }");

		sparql("SELECT ?phone WHERE { <http://test/persons/1> "
				+ "iswc:phone ?phone }");
		expectVariable("phone", this.model.createLiteral("310-448-8794"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Phone from Persons where PerID = 1");
			assertEquals("310-448-8794", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(0, differences.size());
	}

	// delete a non NULLABLE value
	public void testSPARQLDeleteNonNullableValue() {

		// set topics.TopicName in the original example to be non NULLable
		// for testing handling non NULLable properties
		try {
			sqlStatement(getTestConnection(),
					"alter table conferences change Name Name "
							+ "varchar(200) NOT NULL;");
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT ?name WHERE { <http://test/conferences/23541> "
				+ "rdfs:label ?name }");
		assertResultCount(1);
		expectVariable("name", this.model
				.createLiteral("International Semantic Web Conference 2002"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Name from conferences where ConfID = 23541");
			assertEquals("International Semantic Web Conference 2002",
					sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		try {
			sparqlUpdate("DELETE { <http://test/conferences/23541> rdfs:label "
					+ "'International Semantic Web Conference 2002' }");
			fail();
		} catch (D2RQUpdateException exception) {
			assertEquals(D2RQUpdateException.DELETE_NOT_NULLABLE_ATTRIBUTE,
					exception.errorCode());
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Name from conferences where ConfID = 23541");
			assertEquals("International Semantic Web Conference 2002",
					sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT ?name WHERE { <http://test/conferences/23541> "
				+ "rdfs:label ?name }");
		assertResultCount(1);
		expectVariable("name", this.model
				.createLiteral("International Semantic Web Conference 2002"));
		assertSolution();

		// return topics.TopicName in the original example to be NULLable
		try {
			sqlStatement(getTestConnection(),
					"alter table conferences change Name Name varchar(100)"
							+ " DEFAULT NULL;");
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(0, differences.size());
	}

	// delete a non existent value for NULLable column
	public void testSPARQLDeleteNonExistentNonNullableValue() {
		// set topics.TopicName in the original example to be non NULLable
		// for testing handling non NULLable properties
		try {
			sqlStatement(getTestConnection(),
					"alter table conferences change Name Name "
							+ "varchar(200) NOT NULL;");
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT ?name WHERE { <http://test/conferences/23541> "
				+ "rdfs:label ?name }");
		assertResultCount(1);
		expectVariable("name", this.model
				.createLiteral("International Semantic Web Conference 2002"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Name from conferences where ConfID = 23541");
			assertEquals("International Semantic Web Conference 2002",
					sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		try {
			sparqlUpdate("DELETE { <http://test/conferences/23541> rdfs:label "
					+ "'DBLP 2010'}");

		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Name from conferences where ConfID = 23541");
			assertEquals("International Semantic Web Conference 2002",
					sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT ?name WHERE { <http://test/conferences/23541> "
				+ "rdfs:label ?name }");
		assertResultCount(1);
		expectVariable("name", this.model
				.createLiteral("International Semantic Web Conference 2002"));
		assertSolution();

		// return topics.TopicName in the original example to be NULLable
		try {
			sqlStatement(getTestConnection(),
					"alter table conferences change Name Name varchar(100)"
							+ " DEFAULT NULL;");
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}
		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(0, differences.size());
	}

}

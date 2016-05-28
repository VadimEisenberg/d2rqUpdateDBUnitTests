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

import il.ac.technion.cs.d2rqUpdate.DBUnit.D2RQUpdateTestSuite;

import java.util.List;

import org.dbunit.assertion.Difference;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

/**
 * @author Vadim Eisenberg <Vadim.Eisenberg@gmail.com>
 *
 */
public class SPARQLUpdateConditions extends SPARQLUpdateTest {

	/**
	 * @param name
	 */
	public SPARQLUpdateConditions(String name) {
		super(name);

	}

	// delete a value with condition
	public void testSPARQLDeleteWithCondition() {
		sparql("SELECT ?title WHERE { <http://test/papers/1> "
				+ "dc:title ?title }");
		assertResultCount(1);
		expectVariable("title", this.model.createLiteral(
				"Trusting Information Sources One Citizen at a Time", "en"));
		assertSolution();

		sparql("SELECT ?year WHERE { <http://test/papers/1> "
				+ "dc:date ?year }");
		assertResultCount(1);
		expectVariable("year", this.model.createTypedLiteral("2002",
				XSDDatatype.XSDgYear));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Title from papers where PaperID = 1");
			assertEquals("Trusting Information Sources One Citizen at a Time",
					sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/papers/1> dc:title "
				+ "'Trusting Information Sources One Citizen at a Time'"
				+ "@en }");

		sparql("SELECT ?title WHERE { <http://test/papers/1> "
				+ "dc:title ?title }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Title from papers where PaperID = 1");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		// check that the dc:date was not deleted when title was deleted
		sparql("SELECT ?year WHERE { <http://test/papers/1> "
				+ "dc:date ?year }");
		assertResultCount(1);
		expectVariable("year", this.model.createTypedLiteral("2002",
				XSDDatatype.XSDgYear));
		assertSolution();

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(),
				"Trusting Information Sources One Citizen at a Time");
		assertEquals(difference.getActualValue(), null);
	}

	// delete a value with condition
	public void testSPARQLDeleteInsertWithCondition() {
		sparql("SELECT ?title WHERE { <http://test/papers/1> "
				+ "dc:title ?title }");
		assertResultCount(1);
		expectVariable("title", this.model.createLiteral(
				"Trusting Information Sources One Citizen at a Time", "en"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Title from papers where PaperID = 1");
			assertEquals("Trusting Information Sources One Citizen at a Time",
					sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("MODIFY " + "DELETE { <http://test/papers/1> dc:title "
				+ "'Trusting Information Sources One Citizen at a Time'@en } "
				+ "INSERT { <http://test/papers/1> dc:title "
				+ "'Dummy paper title'@en }");

		sparql("SELECT ?title WHERE { <http://test/papers/1> "
				+ "dc:title ?title }");
		assertResultCount(1);
		expectVariable("title", this.model.createLiteral("Dummy paper title",
				"en"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Title from papers where PaperID = 1");
			assertEquals("Dummy paper title", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(),
				"Trusting Information Sources One Citizen at a Time");
		assertEquals(difference.getActualValue(), "Dummy paper title");
	}

	public void testSPARQLInsertWithCondition() {
		sparql("SELECT ?title WHERE { <http://test/papers/10> "
				+ "dc:title ?title }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Title from papers where PaperID = 10");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/papers/10> dc:title "
				+ "'Semantic Web'" + "@en }");

		sparql("SELECT ?title WHERE { <http://test/papers/10> "
				+ "dc:title ?title }");
		assertResultCount(1);
		expectVariable("title", this.model.createLiteral("Semantic Web", "en"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Title from papers where PaperID = 10");
			assertEquals("Semantic Web", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

	}

	public void testSPARQLDeleteUriColumnWithCondition() {
		sparql("SELECT ?type WHERE { <http://test/organizations/2> "
				+ "rdf:type ?type }");

		expectVariable("type", this.model
				.createResource("http://annotation.semanticweb.org/"
						+ "iswc/iswc.daml#University"));
		assertSolution();
		expectVariable("type", this.model
				.createResource("http://annotation.semanticweb.org/"
						+ "iswc/iswc.daml#Organization"));
		assertSolution();
		assertResultCount(2);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Type from Organizations where OrgID  = 2");
			assertEquals("U", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/organizations/2> rdf:type "
				+ "iswc:University }");

		sparql("SELECT ?type WHERE { <http://test/organizations/2> "
				+ "rdf:type ?type }");

		expectVariable("type", this.model
				.createResource("http://annotation.semanticweb.org/"
						+ "iswc/iswc.daml#Organization"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Type from Organizations where OrgID  = 2");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "U");
		assertEquals(difference.getActualValue(), null);
	}

	public void testSPARQLDeleteInsertUriColumnWithCondition() {
		sparql("SELECT ?type WHERE { <http://test/organizations/2> "
				+ "rdf:type ?type }");

		expectVariable("type", this.model
				.createResource("http://annotation.semanticweb.org/"
						+ "iswc/iswc.daml#University"));
		assertSolution();
		expectVariable("type", this.model
				.createResource("http://annotation.semanticweb.org/"
						+ "iswc/iswc.daml#Organization"));
		assertSolution();
		assertResultCount(2);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Type from Organizations where OrgID  = 2");
			assertEquals("U", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("MODIFY\n"
				+ "DELETE { <http://test/organizations/2> rdf:type "
				+ "iswc:University}\n"
				+ "INSERT { <http://test/organizations/2> rdf:type "
				+ "iswc:Department}\n");

		sparql("SELECT ?type WHERE { <http://test/organizations/2> "
				+ "rdf:type ?type }");

		expectVariable("type", this.model
				.createResource("http://annotation.semanticweb.org/"
						+ "iswc/iswc.daml#Organization"));
		assertSolution();

		expectVariable("type", this.model
				.createResource("http://annotation.semanticweb.org/"
						+ "iswc/iswc.daml#Department"));
		assertSolution();
		assertResultCount(2);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Type from Organizations where OrgID  = 2");
			assertEquals("D", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "U");
		assertEquals(difference.getActualValue(), "D");
	}

	public void testSPARQLInsertUriColumnWithCondition() {
		sparql("SELECT ?type WHERE { <http://test/organizations/10> "
				+ "rdf:type ?type }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Type from Organizations where OrgID  = 10");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/organizations/10> rdf:type "
				+ "iswc:Institute }");

		sparql("SELECT ?type WHERE { <http://test/organizations/10> "
				+ "rdf:type ?type }");

		expectVariable("type", this.model
				.createResource("http://annotation.semanticweb.org/"
						+ "iswc/iswc.daml#Organization"));
		assertSolution();
		expectVariable("type", this.model
				.createResource("http://annotation.semanticweb.org/"
						+ "iswc/iswc.daml#Institute"));
		assertSolution();
		assertResultCount(2);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Type from Organizations where OrgID  = 10");
			assertEquals("I", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}
	}

	public void testSPARQLDeleteValueWithConditionOnItself() {
		sparql("SELECT ?date WHERE { <http://test/conferences/23543> <"
				+ D2RQUpdateTestSuite.DIRECTORY_BASE_URI
				+ "/vocab/startDate> ?date }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Datum from conferences where ConfID = 23543");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			exception.printStackTrace();
			fail("unexpected exception caught: " + exception.getMessage());
		}

		// insert a new conference via SQL, in order to test deleting it
		try {
			sqlStatement(
					getTestConnection(),
					"INSERT INTO conferences(ConfID, Name, URI, Date,"
							+ "Location, Datum) VALUES(23543, 'International Semantic Web "
							+ "Conference 2006', "
							+ "'http://annotation.semanticweb.org/iswc/iswc.daml#ISWC_2006',"
							+ " 'November 5-9, 2006', 'Athens', '2006-10-05 00:00:00')");
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT ?date WHERE { <http://test/conferences/23543> <"
				+ D2RQUpdateTestSuite.DIRECTORY_BASE_URI
				+ "/vocab/startDate> ?date }");

		expectVariable("date", this.model.createTypedLiteral(
				"2006-10-05T00:00:00", XSDDatatype.XSDdateTime));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Datum from conferences where ConfID = 23543");
			assertEquals("2006-10-05 00:00:00.0", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE {<http://test/conferences/23543> <"
				+ D2RQUpdateTestSuite.DIRECTORY_BASE_URI
				+ "/vocab/startDate> '2006-10-05T00:00:00'^^xsd:dateTime }");

		sparql("SELECT ?date WHERE { <http://test/conferences/23543> <"
				+ D2RQUpdateTestSuite.DIRECTORY_BASE_URI
				+ "/vocab/startDate> ?date }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Datum from conferences where ConfID = 23543");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}
	}

	public void testSPARQLInsertValueWithConditionOnItself() {
		sparql("SELECT ?date ?name WHERE { <http://test/conferences/23543> <"
				+ D2RQUpdateTestSuite.DIRECTORY_BASE_URI
				+ "/vocab/startDate> ?date . "
				+ "<http://test/conferences/23543> rdfs:label ?name }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Name, Datum from conferences where ConfID = 23543");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT {<http://test/conferences/23543> <"
				+ D2RQUpdateTestSuite.DIRECTORY_BASE_URI
				+ "/vocab/startDate> '2006-10-05T00:00:00'^^xsd:dateTime ."
				+ " <http://test/conferences/23543> rdfs:label "
				+ "'International Semantic Web Conference 2006'}");

		sparql("SELECT ?date ?name WHERE { <http://test/conferences/23543> <"
				+ D2RQUpdateTestSuite.DIRECTORY_BASE_URI
				+ "/vocab/startDate> ?date . "
				+ "<http://test/conferences/23543> rdfs:label ?name }");
		assertResultCount(1);

		expectVariable("date", this.model.createTypedLiteral(
				"2006-10-05T00:00:00", XSDDatatype.XSDdateTime));
		expectVariable("name", this.model
				.createLiteral("International Semantic Web Conference 2006"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Name, Datum from conferences where ConfID = 23543");
			assertEquals("International Semantic Web Conference 2006 "
					+ "2006-10-05 00:00:00.0", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}


	// TODO - handle this case
	// this test does not work - the problem is to implement handling conditions
	// other than '='. such conditions could be implemented once the accessors
	// interface would be implemented for D2RQ expressions

	/*
	 * public void
	 * testSPARQLInsertValueWithConditionOnItself_NegativeCondition() {
	 * sparql("SELECT ?date WHERE { <http://test/conferences/23543> <" +
	 * D2RQUpdateTestSuite.DIRECTORY_BASE_URI + "/vocab/startDate> ?date }");
	 * assertResultCount(0);
	 * 
	 * try { String sqlResult = sqlQuery(getConnection(),
	 * "select Datum from conferences where ConfID = 23543"); assertEquals("",
	 * sqlResult); } catch (Exception exception) {
	 * fail("unexpected exception caught: " + exception.getMessage()); }
	 * 
	 * // insert a new conference via SQL, in order to test deleting it //
	 * insert '0001-01-01 00:00:00' as a date, meaning the date is not // mapped
	 * 
	 * try { sqlStatement( getConnection(),
	 * "INSERT INTO conferences(ConfID, Name, URI, Date," +
	 * "Location, Datum) VALUES(23543, 'International Semantic Web " +
	 * "Conference 2006', " +
	 * "'http://annotation.semanticweb.org/iswc/iswc.daml#ISWC_2006'," +
	 * " 'November 5-9, 2006', 'Athens', '0001-01-01 00:00:00')"); } catch
	 * (Exception exception) { fail("unexpected exception caught: " +
	 * exception.getMessage()); }
	 * 
	 * try { String sqlResult = sqlQuery(getConnection(),
	 * "select Datum from conferences where ConfID = 23543");
	 * assertEquals("0001-01-01 00:00:00.0", sqlResult); } catch (Exception
	 * exception) { fail("unexpected exception caught: " +
	 * exception.getMessage()); }
	 * 
	 * sparql("SELECT ?date WHERE { <http://test/conferences/23543> <" +
	 * D2RQUpdateTestSuite.DIRECTORY_BASE_URI + "/vocab/startDate> ?date }");
	 * 
	 * assertResultCount(0);
	 * 
	 * sparqlUpdate("INSERT {<http://test/conferences/23543> <" +
	 * D2RQUpdateTestSuite.DIRECTORY_BASE_URI +
	 * "/vocab/startDate> '2006-10-05T00:00:00'^^xsd:dateTime }");
	 * 
	 * sparql("SELECT ?date WHERE { <http://test/conferences/23543> <" +
	 * D2RQUpdateTestSuite.DIRECTORY_BASE_URI + "/vocab/startDate> ?date }");
	 * 
	 * expectVariable("date", this.model.createTypedLiteral(
	 * "2006-10-05T00:00:00", XSDDatatype.XSDdateTime)); assertSolution();
	 * assertResultCount(1);
	 * 
	 * try { String sqlResult = sqlQuery(getConnection(),
	 * "select Datum from conferences where ConfID = 23543");
	 * assertEquals("2006-10-05 00:00:00.0", sqlResult); } catch (Exception
	 * exception) { fail("unexpected exception caught: " +
	 * exception.getMessage()); } // return the state back try {
	 * sqlStatement(getConnection(),
	 * "DELETE FROM conferences WHERE ConfID = 23543"); } catch (Exception
	 * exception) { fail("unexpected exception caught: " +
	 * exception.getMessage()); }
	 * 
	 * sparql("SELECT ?date WHERE { <http://test/conferences/23543> <" +
	 * D2RQUpdateTestSuite.DIRECTORY_BASE_URI + "/vocab/startDate> ?date }");
	 * assertResultCount(0);
	 * 
	 * try { String sqlResult = sqlQuery(getConnection(),
	 * "select Datum from conferences where ConfID = 23543"); assertEquals("",
	 * sqlResult); } catch (Exception exception) {
	 * fail("unexpected exception caught: " + exception.getMessage()); } }
	 */
}

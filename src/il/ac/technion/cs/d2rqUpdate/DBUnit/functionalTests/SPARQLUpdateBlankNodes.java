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

import java.util.List;

import org.dbunit.assertion.Difference;

/**
 * @author Vadim Eisenberg <Vadim.Eisenberg@gmail.com>
 *
 */
public class SPARQLUpdateBlankNodes extends SPARQLUpdateTest {

	/**
	 * @param name
	 */
	public SPARQLUpdateBlankNodes(String name) {
		super(name);
	}

	public void testDeleteBlankNodeProperty() {
		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/1> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		expectVariable("country", this.model.createLiteral("United States"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 1");
			assertEquals("United States", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { ?addr vcard:Country ?country } WHERE {"
				+ "<http://test/organizations/1> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");

		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/1> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 1");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "United States");
		assertEquals(difference.getActualValue(), null);
	}

	public void testDeleteInsertBlankNodeProperty() {
		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/1> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		expectVariable("country", this.model.createLiteral("United States"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 1");
			assertEquals("United States", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("MODIFY DELETE { ?addr vcard:Country ?country } "
				+ "INSERT { ?addr vcard:Country 'Germany' }"
				+ "WHERE { <http://test/organizations/1> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }\n");

		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/1> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		expectVariable("country", this.model.createLiteral("Germany"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 1");
			assertEquals("Germany", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "United States");
		assertEquals(difference.getActualValue(), "Germany");
	}

	public void testInsertBlankNodeProperty() {
		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/2> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 2");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		// sparqlUpdate("INSERT { <http://test/organizations/2> vcard:ADR _:address ."
		// + "_:address vcard:Country 'Germany' }");

		sparqlUpdate("INSERT { ?addr vcard:Country 'Germany' }"
				+ "WHERE { <http://test/organizations/2> vcard:ADR ?addr }\n");

		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/2> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		expectVariable("country", this.model.createLiteral("Germany"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 2");
			assertEquals("Germany", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), null);
		assertEquals(difference.getActualValue(), "Germany");
	}

	public void testDeleteNewBlankNodeProperty() {
		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/10> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 10");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		// insert a new row to test deleting it
		try {
			sqlStatement(getTestConnection(),
					"insert into organizations(OrgID, Country) values(10,'Ireland')");

		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/10> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		expectVariable("country", this.model.createLiteral("Ireland"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 10");
			assertEquals("Ireland", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { ?addr vcard:Country 'Ireland' }"
				+ "WHERE { <http://test/organizations/10> vcard:ADR ?addr }\n");


	}

	public void testInsertNewBlankNodeProperty() {
		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/10> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 10");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/organizations/10> vcard:ADR _:address ."
				+ "_:address vcard:Country 'Germany' }");

		sparql("SELECT DISTINCT ?country WHERE {"
				+ "<http://test/organizations/10> vcard:ADR ?addr ."
				+ "?addr vcard:Country ?country }");
		expectVariable("country", this.model.createLiteral("Germany"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select Country from organizations where OrgID = 10");
			assertEquals("Germany", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}
}

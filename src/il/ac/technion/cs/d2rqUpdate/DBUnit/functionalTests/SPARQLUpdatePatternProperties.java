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
public class SPARQLUpdatePatternProperties extends SPARQLUpdateTest {

	/**
	 * @param name
	 */
	public SPARQLUpdatePatternProperties(String name) {
		super(name);
	}

	public void testSPARQLDeleteForPatternProperty() {
		sparql("SELECT ?name WHERE { <http://test/persons/1> "
				+ "foaf:name ?name }");
		expectVariable("name", this.model.createLiteral("Yolanda Gil"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select FirstName, LastName from Persons where PerID = 1");
			assertEquals("Yolanda Gil", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/persons/1> foaf:name "
				+ "'Yolanda Gil' }");

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select FirstName, LastName from Persons where PerID = 1");
			assertEquals("null null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT ?name WHERE { <http://test/persons/1> "
				+ "foaf:name ?name }");
		assertResultCount(0);

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(2, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "Yolanda");
		assertEquals(difference.getActualValue(), null);
		difference = differences.get(1);
		assertEquals(difference.getExpectedValue(), "Gil");
		assertEquals(difference.getActualValue(), null);
	}

	public void testSPARQLInsertForPatternProperty() {
		sparql("SELECT ?name WHERE { <http://test/persons/14> "
				+ "foaf:name ?name }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select FirstName, LastName from Persons where PerID =14");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/persons/14> foaf:name "
				+ "'Vadim Eisenberg' }");

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select FirstName, LastName from Persons where PerID = 14");
			assertEquals("Vadim Eisenberg", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT ?name WHERE { <http://test/persons/14> "
				+ "foaf:name ?name }");
		expectVariable("name", this.model.createLiteral("Vadim Eisenberg"));
		assertSolution();
		assertResultCount(1);



	}

	public void testSPARQLDeleteInsertForPatternProperty() {
		sparql("SELECT ?name WHERE { <http://test/persons/1> "
				+ "foaf:name ?name }");
		expectVariable("name", this.model.createLiteral("Yolanda Gil"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select FirstName, LastName from Persons where PerID = 1");
			assertEquals("Yolanda Gil", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("MODIFY\n"
				+ "DELETE { <http://test/persons/1> foaf:name 'Yolanda Gil' }\n"
				+ "INSERT { <http://test/persons/1> foaf:name 'Mary Major' }");

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select FirstName, LastName from Persons where PerID = 1");
			assertEquals("Mary Major", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparql("SELECT ?name WHERE { <http://test/persons/1> "
				+ "foaf:name ?name }");
		expectVariable("name", this.model.createLiteral("Mary Major"));
		assertSolution();
		assertResultCount(1);

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(2, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "Yolanda");
		assertEquals(difference.getActualValue(), "Mary");
		difference = differences.get(1);
		assertEquals(difference.getExpectedValue(), "Gil");
		assertEquals(difference.getActualValue(), "Major");

	}

}

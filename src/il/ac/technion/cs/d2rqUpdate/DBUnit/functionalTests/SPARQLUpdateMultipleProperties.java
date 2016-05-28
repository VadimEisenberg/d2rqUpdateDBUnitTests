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

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

/**
 * @author Vadim Eisenberg <Vadim.Eisenberg@gmail.com>
 *
 */
public class SPARQLUpdateMultipleProperties extends SPARQLUpdateTest {

	/**
	 * @param name
	 */
	public SPARQLUpdateMultipleProperties(String name) {
		super(name);
	}

	// check the delete for skos:prefLabel and rdfs:label mapped to the same
	// column
	public void testSPARQLDeleteInsertByMultipleProperties1() {
		runTestSPARQLDeleteInsertByProperty("skos:prefLabel");
	}

	public void testSPARQLDeleteInsertByMultipleProperties2() {
		runTestSPARQLDeleteInsertByProperty("rdfs:label");
	}

	private void runTestSPARQLDeleteInsertByProperty(String property) {
		sparql("SELECT ?name WHERE { <http://test/topics/1> " + property
				+ " ?name }");
		assertResultCount(1);
		expectVariable("name", this.model.createTypedLiteral(
				"Knowledge Representation Languages", XSDDatatype.XSDstring));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicName from topics where TopicID = 1");
			assertEquals("Knowledge Representation Languages", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/topics/1> " + property
				+ " 'Knowledge Representation Languages'^^xsd:string }");

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicName from topics where TopicID = 1");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}
		sparql("SELECT ?name WHERE { <http://test/topics/1> " + property
				+ " ?name }");
		assertResultCount(0);

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(),
				"Knowledge Representation Languages");
		assertEquals(difference.getActualValue(), null);
	}
}

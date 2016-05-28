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
public class SPARQLUpdateAliases extends SPARQLUpdateTest {

	/**
	 * @param name
	 */
	public SPARQLUpdateAliases(String name) {
		super(name);
	}

	public void testSPARQLDeleteAliasedValue() {
		sparql("SELECT ?topic WHERE { <http://test/topics/1> "
				+ "skos:broader ?topic }");
		expectVariable("topic", this.model
				.createResource("http://test/topics/3"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select ParentID from topics where TopicID = 1");
			assertEquals("3", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/topics/1> "
				+ "skos:broader <http://test/topics/3> }");

		sparql("SELECT ?topic WHERE { <http://test/topics/1> "
				+ "skos:broader ?topic }");
		assertResultCount(0);
		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select ParentID from topics where TopicID = 1");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "3");
		assertEquals(difference.getActualValue(), null);

	}

	public void testSPARQLInsertAliasedValue() {
		sparql("SELECT ?topic WHERE { <http://test/topics/3> "
				+ "skos:broader ?topic }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select ParentID from topics where TopicID = 3");
			assertEquals("null", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		// make Artificial Intelligence sub concept of the Semantic Web :)
		sparqlUpdate("INSERT { <http://test/topics/3> "
				+ "skos:broader <http://test/topics/5> }");

		sparql("SELECT ?topic WHERE { <http://test/topics/3> "
				+ "skos:broader ?topic }");
		expectVariable("topic", this.model
				.createResource("http://test/topics/5"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select ParentID from topics where TopicID = 3");
			assertEquals("5", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), null);
		assertEquals(difference.getActualValue(), 5);

	}

	public void testSPARQLInsertNewAliasedSubject() {
		sparql("SELECT ?topic WHERE { <http://test/topics/17> "
				+ "skos:broader ?topic }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select ParentID from topics where TopicID = 17");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/topics/17> "
				+ "skos:broader <http://test/topics/3> }");

		sparql("SELECT ?topic WHERE { <http://test/topics/17> "
				+ "skos:broader ?topic }");
		expectVariable("topic", this.model
				.createResource("http://test/topics/3"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select ParentID from topics where TopicID = 17");
			assertEquals("3", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}

	public void testSPARQLInsertNewAliasedSubjectAndObject() {
		sparql("SELECT ?topic WHERE { <http://test/topics/17> "
				+ "skos:broader ?topic }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(), "select TopicID from topics"
							+ " where TopicID = 17 OR TopicID = 18");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/topics/17> "
				+ "skos:broader <http://test/topics/18> }");

		sparql("SELECT ?topic WHERE { <http://test/topics/17> "
				+ "skos:broader ?topic }");
		expectVariable("topic", this.model
				.createResource("http://test/topics/18"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(), "select ParentID from topics"
							+ " where TopicID = 17");
			assertEquals("18", sqlResult);
			sqlResult =
					sqlQuery(getTestConnection(), "select TopicID from topics"
							+ " where TopicID = 17 OR TopicID = 18"
							+ " ORDER BY TopicID");
			assertEquals("1718", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}

	public void testSPARQLDeleteInsertAliasedValue() {
		sparql("SELECT ?topic WHERE { <http://test/topics/1> "
				+ "skos:broader ?topic }");
		expectVariable("topic", this.model
				.createResource("http://test/topics/3"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select ParentID from topics where TopicID = 1");
			assertEquals("3", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("MODIFY DELETE { <http://test/topics/1> "
				+ "skos:broader <http://test/topics/3> }\n"
				+ "INSERT { <http://test/topics/1> "
				+ "skos:broader <http://test/topics/5> }\n");

		sparql("SELECT ?topic WHERE { <http://test/topics/1> "
				+ "skos:broader ?topic }");
		expectVariable("topic", this.model
				.createResource("http://test/topics/5"));
		assertSolution();
		assertResultCount(1);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select ParentID from topics where TopicID = 1");
			assertEquals("5", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "3");
		assertEquals(difference.getActualValue(), 5);
	}

}

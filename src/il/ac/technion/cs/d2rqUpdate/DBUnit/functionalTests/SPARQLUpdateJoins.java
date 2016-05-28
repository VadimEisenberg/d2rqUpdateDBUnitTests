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
public class SPARQLUpdateJoins extends SPARQLUpdateTest {

	/**
	 * @param name
	 */
	public SPARQLUpdateJoins(String name) {
		super(name);
	}

	// this test also tests the foreign keys handling
	public void testSPARQLInsertJoinProperty() {
		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(3);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/2"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1 order by TopicID");
			assertEquals("123", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/persons/1> iswc:research_interests "
				+ "<http://test/topics/4> }");

		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(4);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/2"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/4"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1 order by TopicID");
			assertEquals("1234", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}

	public void testSPARQLInsertJoinPropertyMultiple() {
		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(3);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/2"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1 order by TopicID");
			assertEquals("123", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/persons/1> iswc:research_interests "
				+ "<http://test/topics/4> ;"
				+ " iswc:research_interests "
				+ "<http://test/topics/5> }");

		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(5);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/2"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/4"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/5"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1 order by TopicID");
			assertEquals("12345", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}

	public void testSPARQLInsertJoinPropertyWithNewSubjectAndObject() {
		sparql("SELECT ?interests WHERE { <http://test/persons/17> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(0);

		sparql("SELECT ?name WHERE { <http://test/topics/77> "
				+ "rdfs:label ?name }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 17 AND TopicID = 77");
			assertEquals("", sqlResult);
			sqlResult =
					sqlQuery(getTestConnection(),
							"select PerID from persons where PerID = 17");
			assertEquals("", sqlResult);
			sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from topics where TopicID = 77");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/persons/17> iswc:research_interests "
				+ "<http://test/topics/77> }");

		sparql("SELECT ?interests WHERE { <http://test/persons/17> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(1);
		expectVariable("interests", this.model
				.createResource("http://test/topics/77"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select PersonID,TopicID from rel_person_topic "
									+ "where PersonID = 17 AND TopicID = 77");
			assertEquals("17 77", sqlResult);
			sqlResult =
					sqlQuery(getTestConnection(),
							"select PerID from persons where PerID = 17");
			assertEquals("17", sqlResult);
			sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from topics where TopicID = 77");
			assertEquals("77", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}

	public void testSPARQLDeleteJoinProperty() {
		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(3);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/2"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1 order by TopicID");
			assertEquals("123", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/persons/1> iswc:research_interests "
				+ "<http://test/topics/2> }");

		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(2);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();

		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1 order by TopicID");
			assertEquals("13", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}
	}

	public void testSPARQLDeleteJoinPropertyByWildCard() {
		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(3);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/2"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1 order by TopicID");
			assertEquals("123", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/persons/1> iswc:research_interests "
				+ " ?topic } WHERE { <http://test/persons/1> iswc:research_interests "
				+ " ?topic }");

		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}

	public void testSPARQLDeleteJoinPropertyMultiple() {
		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(3);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/2"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1 order by TopicID");
			assertEquals("123", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/persons/1> iswc:research_interests "
				+ "<http://test/topics/1> ; "
				+ " iswc:research_interests "
				+ "<http://test/topics/3>} ");

		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(1);
		expectVariable("interests", this.model
				.createResource("http://test/topics/2"));

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1");
			assertEquals("2", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}

	public void testSPARQLDeleteInsertJoinProperty() {
		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(3);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/2"));
		assertSolution();
		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1");
			assertEquals("123", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("MODIFY DELETE { <http://test/persons/1> iswc:research_interests "
				+ "<http://test/topics/2> }\n"
				+ "INSERT { <http://test/persons/1> iswc:research_interests "
				+ "<http://test/topics/7> }\n");

		sparql("SELECT ?interests WHERE { <http://test/persons/1> "
				+ "iswc:research_interests ?interests }");
		assertResultCount(3);
		expectVariable("interests", this.model
				.createResource("http://test/topics/1"));
		assertSolution();

		expectVariable("interests", this.model
				.createResource("http://test/topics/7"));
		assertSolution();

		expectVariable("interests", this.model
				.createResource("http://test/topics/3"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_person_topic "
									+ "where PersonID = 1 ORDER BY TopicID");
			assertEquals("137", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(2, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "2");
		assertEquals(difference.getActualValue(), 3);
		difference = differences.get(1);
		assertEquals(difference.getExpectedValue(), "3");
		assertEquals(difference.getActualValue(), 7);
	}

}

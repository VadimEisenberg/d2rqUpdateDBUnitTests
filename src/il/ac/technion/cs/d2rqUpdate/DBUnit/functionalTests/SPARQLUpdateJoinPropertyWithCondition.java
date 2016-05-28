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
public class SPARQLUpdateJoinPropertyWithCondition extends SPARQLUpdateTest {

	/**
	 * @param name
	 */
	public SPARQLUpdateJoinPropertyWithCondition(String name) {
		super(name);
	}

	public void testSPARQLDeleteJoinPropertyWithCondition() {
		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:primarySubject ?subject }");
		assertResultCount(1);
		expectVariable("subject", this.model
				.createResource("http://test/topics/5"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1 AND RelationType = 1");
			assertEquals("5", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/papers/1> "
				+ "skos:primarySubject <http://test/topics/5> }");

		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:primarySubject ?subject }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1 AND RelationType = 1");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

	}

	public void testSPARQLInsertJoinPropertyWithCondition() {
		sparql("SELECT ?subject WHERE { <http://test/papers/3> "
				+ "skos:primarySubject ?subject }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 3 AND RelationType = 1");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("INSERT { <http://test/papers/3> "
				+ "skos:primarySubject <http://test/topics/5> }");

		sparql("SELECT ?subject WHERE { <http://test/papers/3> "
				+ "skos:primarySubject ?subject }");
		assertResultCount(1);
		expectVariable("subject", this.model
				.createResource("http://test/topics/5"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 3 AND RelationType = 1");
			assertEquals("5", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}


	}

	public void testSPARQLDeleteNonExistentInsertJoinPropertyWithCondition() {
		sparql("SELECT ?subject WHERE { <http://test/papers/3> "
				+ "skos:primarySubject ?subject }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 3 AND RelationType = 1");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("MODIFY DELETE { <http://test/papers/3> "
				+ "skos:primarySubject <http://test/topics/7> }"
				+ "INSERT { <http://test/papers/3> "
				+ "skos:primarySubject <http://test/topics/5> }");

		sparql("SELECT ?subject WHERE { <http://test/papers/3> "
				+ "skos:primarySubject ?subject }");
		assertResultCount(1);
		expectVariable("subject", this.model
				.createResource("http://test/topics/5"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 3 AND RelationType = 1");
			assertEquals("5", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

	}

	public void testSPARQLDeleteInsertJoinPropertyWithCondition() {
		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:primarySubject ?subject }");
		assertResultCount(1);
		expectVariable("subject", this.model
				.createResource("http://test/topics/5"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1 AND RelationType = 1");
			assertEquals("5", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("MODIFY DELETE { <http://test/papers/1> "
				+ "skos:primarySubject <http://test/topics/5> }\n"
				+ "INSERT { <http://test/papers/1> "
				+ "skos:primarySubject <http://test/topics/7> }");

		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:primarySubject ?subject }");
		assertResultCount(1);
		expectVariable("subject", this.model
				.createResource("http://test/topics/7"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1 AND RelationType = 1");
			assertEquals("7", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		List<Difference> differences = getDifferencesFromTheOriginal();
		assertEquals(1, differences.size());
		Difference difference = differences.get(0);
		assertEquals(difference.getExpectedValue(), "5");
		assertEquals(difference.getActualValue(), 7);
	}

	public void testSPARQLDeleteJoinPropertyWithConditionMappedToSeveralColumns() {
		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:subject ?subject }");
		assertResultCount(2);
		expectVariable("subject", this.model
				.createResource("http://test/topics/5"));
		assertSolution();
		expectVariable("subject", this.model
				.createResource("http://test/topics/15"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1 ORDER BY TopicID");
			assertEquals("515", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/papers/1> "
				+ "skos:subject <http://test/topics/5> }");

		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:subject ?subject }");
		assertResultCount(1);
		expectVariable("subject", this.model
				.createResource("http://test/topics/15"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1");
			assertEquals("15", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}



	}

	public void testSPARQLDeleteJoinPropertyWithConditionMappedToSeveralColumns2() {
		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:subject ?subject }");
		assertResultCount(2);
		expectVariable("subject", this.model
				.createResource("http://test/topics/5"));
		assertSolution();
		expectVariable("subject", this.model
				.createResource("http://test/topics/15"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1 ORDER BY TopicID");
			assertEquals("515", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/papers/1> "
				+ "skos:subject <http://test/topics/15> }");

		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:subject ?subject }");
		assertResultCount(1);
		expectVariable("subject", this.model
				.createResource("http://test/topics/5"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1");
			assertEquals("5", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}



	}

	public void testSPARQLDeleteJoinPropertyWithConditionMappedToSeveralColumnsWildCard() {
		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:subject ?subject }");
		assertResultCount(2);
		expectVariable("subject", this.model
				.createResource("http://test/topics/5"));
		assertSolution();
		expectVariable("subject", this.model
				.createResource("http://test/topics/15"));
		assertSolution();

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1 ORDER BY TopicID");
			assertEquals("515", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		sparqlUpdate("DELETE { <http://test/papers/1> skos:subject ?topic } "
				+ "WHERE { <http://test/papers/1> skos:subject ?topic }");

		sparql("SELECT ?subject WHERE { <http://test/papers/1> "
				+ "skos:subject ?subject }");
		assertResultCount(0);

		try {
			String sqlResult =
					sqlQuery(getTestConnection(),
							"select TopicID from rel_paper_topic "
									+ "where PaperID = 1");
			assertEquals("", sqlResult);
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

	}

}

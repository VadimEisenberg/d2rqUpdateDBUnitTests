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

import il.ac.technion.cs.d2rqUpdate.ModelD2RQUpdate;
import il.ac.technion.cs.d2rqUpdate.DBUnit.D2RQUpdateTestSuite;
import il.ac.technion.cs.d2rqUpdate.DBUnit.helpers.QueryAndUpdateLanguageTestFramework;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.assertion.DiffCollectingFailureHandler;
import org.dbunit.assertion.Difference;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

import de.fuberlin.wiwiss.d2rq.map.Database;
import de.fuberlin.wiwiss.d2rq.parser.MapParser;

/**
 * @author Vadim Eisenberg <Vadim.Eisenberg@gmail.com>
 *
 */
public abstract class SPARQLUpdateTest extends
		QueryAndUpdateLanguageTestFramework {
	protected final String[] NEW_ORGANIZATIONS_TRIPLES =
			new String[] {
					// Add NUI Galway
					"<http://test/organizations/10> rdf:type iswc:University",
					"<http://test/organizations/10> rdfs:label "
							+ "'National University of Ireland, Galway'",
					"<http://test/organizations/10> foaf:homepage "
							+ "<http://www.nuigalway.ie>",
					// Add Deri Galway
					"<http://test/organizations/11> rdf:type iswc:Institute",
					"<http://test/organizations/11> rdfs:label "
							+ "'Digital Enterprise Research Institute, Galway'",
					"<http://test/organizations/11> foaf:homepage "
							+ "<http://www.deri.ie/>" };

	protected final String[] NEW_TOPICS_TRIPLES = new String[] { // Add RDB2RDF
					// topic
					"<http://test/topics/17> rdfs:label 'Mapping Relational Databases to Semantic Web'^^xsd:string",
					"<http://test/topics/17> owl:sameAs iswc:RDB2RDF",
					"<http://test/topics/17> skos:broader <http://test/topics/11>", };

	protected final String[] NEW_PERSONS_TRIPLES = new String[] {// Add Richard
					// Cyganiak
					"<http://test/persons/14> rdf:type iswc:Researcher",
					"<http://test/persons/14> foaf:name 'Richard Cyganiak'",
					"<http://test/persons/14> iswc:address 'Office 102, Deri Galway'",
					"<http://test/persons/14> foaf:mbox <mailto:Richard.Cyganiak@fakemail.com>",
					"<http://test/persons/14> foaf:homepage <http://richard.cyganiak.de>",
					"<http://test/persons/14> iswc:phone '777-77-7777'",

					"<http://test/persons/14> iswc:research_interests <http://test/topics/5>",
					"<http://test/persons/14> iswc:research_interests <http://test/topics/10>" };

	protected final String[] NEW_PAPERS_TRIPLES =
			new String[] {
					// Add the "D2RQ – Lessons Learned" paper
					"<http://test/papers/9> rdfs:label 'D2RQ - Lessons Learned'@en",
					"<http://test/papers/9> <http://purl.org/dc/terms/abstract> 'A lot of valuable data currently resides in relational databases."
							+ " They power most Web sites and are therefore also a natural data source"
							+ " for the Semantic Web. Within enterprise settings, integrating various"
							+ " databases is often a motivation for adopting RDF. Thus, making relational"
							+ " databases accessible to RDF-based systems is an important and recurrent"
							+ " problem. This document describes the D2RQ mapping platform, followed by"
							+ " some issues and requirements that have emerged from experience with D2RQ."
							+ " Finally, we outline areas for future community work.'",

					"<http://test/papers/9> dc:date '2006'^^xsd:gYear",

					"<http://test/papers/9> dc:creator <http://test/persons/12>",
			};

	public SPARQLUpdateTest(String name) {
		super(name);
		System.setProperty(
				PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS,
				"com.mysql.jdbc.Driver");
		System.setProperty(
				PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
				"jdbc:mysql://127.0.0.1/iswc");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME,
				"eisenv");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD,
				"xxx1yyy");


		// set logging to maximum
		/*
		 * org.apache.log4j.Logger.getLogger("de.fuberlin.wiwiss.d2rq").setLevel(
		 * org.apache.log4j.Level.ALL);
		 */
		 org.apache.log4j.Logger.getLogger("il.ac.technion.cs.d2rqUpdate")
		 .setLevel(org.apache.log4j.Level.ALL);


	}



	@Override
	protected void setUpDatabaseConfig(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		try {
			return new FlatXmlDataSetBuilder().build(new FileInputStream(
					"doc/example/iswc.xml"));
		}
		catch (Exception e) {
			System.err.println(e.getStackTrace());
			throw e;
		}
	}



	Connection connection = null;
	IDatabaseConnection dbUnitConnection = null;

	IDatabaseConnection getDBUnitConnection() {
		if (dbUnitConnection == null) {
			try {
				dbUnitConnection = getConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dbUnitConnection;
	}

	@Override
	protected void setUp() {
		try {
			dropForeignKeys();
			super.setUp();


		} catch (Exception exception) {
			exception.printStackTrace();
			fail("unexpected exception caught: " + exception.getMessage());
		}

		finally {
			try {
				returnBackForegnKeys();
			} catch (SQLException exception) {
				exception.printStackTrace();
				fail("unexpected exception caught: " + exception.getMessage());
			}
		}

		Model mapModel = FileManager.get().loadModel(mapURL(), "N3");
		patchTheMapping(mapModel);
		this.model = new ModelD2RQUpdate(mapModel, "http://test/");
		this.queryFramework.setModel(this.model);
	}



	private void returnBackForegnKeys() throws SQLException {
		sqlStatement(
				getTestConnection(),
				"alter table topics ADD CONSTRAINT topics_ibfk_1 "
						+ "FOREIGN KEY (`ParentID`) REFERENCES topics (`TopicID`)");
		sqlStatement(
				getTestConnection(),
				"alter table organizations ADD CONSTRAINT organizations_ibfk_1 "
						+ "FOREIGN KEY (`Belongsto`) REFERENCES organizations (`OrgID`)");
	}



	private void dropForeignKeys() throws SQLException {
		sqlStatement(getTestConnection(),
				"alter table topics drop foreign key topics_ibfk_1");

		sqlStatement(getTestConnection(),
				"alter table organizations drop foreign key organizations_ibfk_1");

	}

	private void patchTheMapping(Model mapModel) {
		// patch the mapping - fix the condition on map:conferences_Datum
		// change to d2rq:condition "conferences.Datum != '0000'"
		// to another condition "conferences.Datum != '0001-01-01 00:00:00'"
		// use a dummy value in the format of timestamp, insted of 0000
		// final Resource subject =
		// mapModel.createResource(D2RQUpdateTestSuite.MAP_PREFIX_URI
		// + "conferences_Datum");
		// final Property property =
		// mapModel.createProperty(D2RQ.NS + "condition");
		//
		// RDFNode oldCondition =
		// mapModel.createLiteral("conferences.Datum != '0000'");
		// mapModel.remove(subject, property, oldCondition);
		//
		// RDFNode newCondition =
		// mapModel
		// .createLiteral("conferences.Datum != '0001-01-01 00:00:00'");
		// mapModel.add(subject, property, newCondition);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			dropForeignKeys();
			super.tearDown();

		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		} finally {
			try {
				returnBackForegnKeys();
			} catch (SQLException exception) {
				exception.printStackTrace();
				fail("unexpected exception caught: " + exception.getMessage());
			}
		}
		this.model.close();
		connection.close();
		connection = null;
	}

	protected Connection getTestConnection() {
		if (connection == null) {
			Model mapModel = FileManager.get().loadModel(mapURL(), "N3");

			MapParser mapParser = new MapParser(mapModel, "http://test/");

			Database firstDatabase =
					(Database) mapParser.parse().databases().iterator().next();

			connection = firstDatabase.connectedDB().connection();
		}
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.d2rq.helpers.QueryLanguageTestFramework#mapURL()
	 */
	@Override
	protected String mapURL() {
		return D2RQUpdateTestSuite.ISWC_MAP;
	}

	@SuppressWarnings("unchecked")
	protected List<Difference> getDifferencesFromTheOriginal() {
		try {
			IDataSet databaseDataSet = getDBUnitConnection().createDataSet();
			IDataSet originalDataSet = getDataSet();
			DiffCollectingFailureHandler myHandler =
					new DiffCollectingFailureHandler();
			// invoke the assertion with the custom handler
			Assertion.assertEquals(originalDataSet, databaseDataSet, myHandler);
			return myHandler.getDiffList();
		} catch (Exception exception) {
			fail("unexpected exception caught: " + exception.getMessage());
		}

		return Collections.emptyList();
	}
}

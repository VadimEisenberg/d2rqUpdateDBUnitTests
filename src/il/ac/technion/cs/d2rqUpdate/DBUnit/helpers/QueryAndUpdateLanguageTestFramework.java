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
package il.ac.technion.cs.d2rqUpdate.DBUnit.helpers;

import il.ac.technion.cs.d2rqUpdate.ModelD2RQUpdate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.DBTestCase;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.VCARD;
import com.hp.hpl.jena.vocabulary.XSD;

import de.fuberlin.wiwiss.d2rq.vocab.FOAF;
import de.fuberlin.wiwiss.d2rq.vocab.ISWC;
import de.fuberlin.wiwiss.d2rq.vocab.SKOS;

/**
 * @author Vadim Eisenberg <Vadim.Eisenberg@gmail.com>
 *
 */
public abstract class QueryAndUpdateLanguageTestFramework extends DBTestCase {
	protected ModelD2RQUpdate model =
			new ModelD2RQUpdate(mapURL(), "N3", "http://test/");

	protected QueryLanguageTestFramework queryFramework =
			new QueryLanguageTestFramework() {

				@Override
				protected String mapURL() {
					return QueryAndUpdateLanguageTestFramework.this.mapURL();
				}
		
			};

	private final String PREFIXES =
			"PREFIX dc: <" + DC.NS + ">\n" + "PREFIX foaf: <" + FOAF.NS + ">\n"
					+ "PREFIX skos: <" + SKOS.NS + ">\n" + "PREFIX iswc: <"
					+ ISWC.NS + ">\n" + "PREFIX xsd: <" + XSD.getURI() + ">\n"
					+ "PREFIX rdfs: <" + RDFS.getURI() + ">\n"
					+ "PREFIX rdf: <" + RDF.getURI() + ">\n" + "PREFIX owl: <"
					+ OWL.getURI() + ">\n" + "PREFIX vcard: <" + VCARD.getURI()
					+ ">\n";

	/**
	 * @param name
	 */
	public QueryAndUpdateLanguageTestFramework(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		queryFramework.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		queryFramework.tearDown();
		super.tearDown();
	}

	/**
	 * @return
	 */
	abstract protected String mapURL();


	protected void sparql(String sparql) {
		queryFramework.sparql(PREFIXES + sparql);
	}

	protected boolean sparqlAsk(String sparql) {
		Query query = QueryFactory.create(PREFIXES + sparql);
		QueryExecution qe = QueryExecutionFactory.create(query, this.model);
		return qe.execAsk();
	}

	protected void sparqlUpdate(String sparqlUpdate) {
		UpdateRequest request = UpdateFactory.create(PREFIXES + sparqlUpdate);
		UpdateAction.execute(request, model);
	}


	/**
	 * @param connection
	 * @param string
	 */
	protected static void sqlStatement(Connection connection,
		String stetementString) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute(stetementString);
	}


	protected static String sqlQuery(Connection connection, String query)
			throws SQLException {
		String resultString = "";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);

		int columnCount = resultSet.getMetaData().getColumnCount();
		while (resultSet.next()) {
			for (int position = 1; position <= columnCount; position++) {
				if (position > 1) {
					resultString += " ";
				}
				resultString += resultSet.getString(position);
			}
		}

		resultSet.close();
		statement.close();
		return resultString;
	}

	protected void expectVariable(String variableName, RDFNode value) {
		queryFramework.expectVariable(variableName, value);
	}

	protected void assertSolution() {
		queryFramework.assertSolution();
	}

	protected void assertResultCount(int count) {
		queryFramework.assertResultCount(count);
	}

	protected void dump() {
		queryFramework.dump();
	}
}

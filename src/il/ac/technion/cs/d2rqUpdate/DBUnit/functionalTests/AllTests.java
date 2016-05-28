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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Vadim Eisenberg <Vadim.Eisenberg@gmail.com>
 *
 */
public class AllTests {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite =
				new TestSuite(
						"Test for de.fuberlin.wiwiss.d2rq.functional_tests");
		// $JUnit-BEGIN$

		suite.addTestSuite(SPARQLUpdateBasicTest.class);
		suite.addTestSuite(SPARQLUpdateJoins.class);
		suite.addTestSuite(SPARQLUpdateConditions.class);
		suite.addTestSuite(SPARQLUpdateJoinPropertyWithCondition.class);
		suite.addTestSuite(SPARQLUpdateAliases.class);
		suite.addTestSuite(SPARQLUpdateMultipleInserts.class);
		suite.addTestSuite(SPARQLUpdateMultipleProperties.class);
		suite.addTestSuite(SPARQLUpdatePatternProperties.class);
		suite.addTestSuite(SPARQLUpdateBlankNodes.class);

		// $JUnit-END$
		return suite;
	}
}

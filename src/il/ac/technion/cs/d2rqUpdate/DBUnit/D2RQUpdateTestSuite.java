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
package il.ac.technion.cs.d2rqUpdate.DBUnit;


import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * @author Vadim Eisenberg <Vadim.Eisenberg@gmail.com>
 *
 */
public class D2RQUpdateTestSuite {
	// public static final String DIRECTORY_URI = "file:doc/example";
	// public static final String ISWC_MAP = DIRECTORY_URI + "mapping-iswc.n3";
	public static final String RELATIVE_MAP_PATH =
			"doc/example/mapping-iswc.n3";
	public static final String DIRECTORY_BASE_URI;
	public static final String MAP_PREFIX_URI =
			"file:///Users/richard/D2RQ/workspace/D2RQ/doc/example/mapping-iswc.n3#";
	public static final String ISWC_MAP;
	
	
	static {
		String currentDirectoryUri = "file:.";
		String mapUri = "file:doc/example/mapping-iswc.n3";
		try {
			String currentDirectory =
					new java.io.File("doc/example/").getCanonicalPath();
			currentDirectoryUri =
					new java.io.File(currentDirectory).toURI().toString();
			// fix the directory URI as is done in
			// com.hp.hpl.jena.util.FileManager.chooseBaseURI
			String partWithoutFilePrefix =
					currentDirectoryUri.substring("file:".length());
			currentDirectoryUri =
					"file:///"
							+ new File(partWithoutFilePrefix)
									.getCanonicalPath();
			currentDirectoryUri = currentDirectoryUri.replace('\\', '/');

			mapUri =
					new java.io.File("doc/example/mapping-iswc.n3").toURI()
							.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		DIRECTORY_BASE_URI = currentDirectoryUri;
		ISWC_MAP = mapUri;

		System.out.println("DIRECTORY_URI = " + DIRECTORY_BASE_URI);
		System.out.println("ISWC_MAP = " + ISWC_MAP);
		System.out.println("MAP_PREFIX_URI = " + MAP_PREFIX_URI);

	}

	
	public static void main(String[] args) {
		TestRunner.run(D2RQUpdateTestSuite.suite());
	}

	public static Test suite() {


		TestSuite suite = new TestSuite("D2RQ/Update Test Suite");
		suite
				.addTest(il.ac.technion.cs.d2rqUpdate.DBUnit.functionalTests.AllTests
				.suite());
		return suite;
	}

}

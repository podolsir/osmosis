// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.set.v0_6;

import java.io.File;

import org.junit.Test;
import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.testutil.AbstractDataTest;

/**
 * Tests for the --merge-change task.
 * 
 * @author Igor Podolskiy
 *
 */
public class ChangeMergerTest extends AbstractDataTest {
	
	/**
	 * Merging two empty sources yields an empty output.
	 * 
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void bothEmpty() throws Exception {
		checkMergeChange("v0_6/empty-change.osc", 
			"v0_6/empty-change.osc", 
			"v0_6/empty-change.osc");
	}
	
	/**
	 * X + empty == X.
	 * 
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void leftEmpty() throws Exception {
		checkMergeChange("v0_6/merge_change/merge-in-1.osc", 
			"v0_6/empty-change.osc", 
			"v0_6/merge_change/merge-in-1.osc");
	}
	
	/**
	 * empty + X == X.
	 * 
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void rightEmpty() throws Exception {
		checkMergeChange("v0_6/empty-change.osc",
			"v0_6/merge_change/merge-in-1.osc", 
			"v0_6/merge_change/merge-in-1.osc");
	}
	
	/**
	 * empty + X == X.
	 * 
	 * @throws Exception if something goes wrong
	 */
	@Test
	public void sameInputs() throws Exception {
		checkMergeChange("v0_6/merge_change/merge-in-1.osc",
			"v0_6/merge_change/merge-in-1.osc", 
			"v0_6/merge_change/merge-in-1.osc");
	}


	
	private void checkMergeChange(String leftFileName, String rightFileName, 
			String expectedOutputFileName) throws Exception {
		File leftFile;
		File rightFile;
		File expectedOutputFile;
		File actualOutputFile;
		
		leftFile = dataUtils.createDataFile(leftFileName);
		rightFile = dataUtils.createDataFile(rightFileName);
		expectedOutputFile = dataUtils.createDataFile(expectedOutputFileName);
		actualOutputFile = dataUtils.newFile();

		Osmosis.run(
				new String [] {
					"-q",
					"--read-xml-change-0.6", rightFile.getPath(),
					"--read-xml-change-0.6", leftFile.getPath(),
					"--merge-change-0.6",
					"--write-xml-change-0.6", actualOutputFile.getPath()
				}
			);

		dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
	}

}

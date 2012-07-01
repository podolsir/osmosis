// This software is released into the Public Domain.  See copying.txt for details.
package org.openstreetmap.osmosis.set.v0_6;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			"v0_6/empty-change.osc", 
			null);
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
			"v0_6/merge_change/merge-in-1.osc", 
			null);
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
			"v0_6/merge_change/merge-in-1.osc", 
			null);
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
			"v0_6/merge_change/merge-in-1.osc",
			null);
	}
	
	/**
	 * Test the timestamp conflict resolution strategy.
	 * 
	 * @throws Exception if something goes wrong
	 * 
	 */
	@Test
	public void timestampConflictResolution() throws Exception {
		checkMergeChange("v0_6/merge_change/merge-in-1.osc",
				"v0_6/merge_change/merge-in-2-timestamp.osc", 
				"v0_6/merge_change/merge-out-timestamp.osc",
				new String[] {"conflictResolutionMethod=timestamp"});

		checkMergeChange("v0_6/merge_change/merge-in-2-timestamp.osc",
				"v0_6/merge_change/merge-in-1.osc", 
				"v0_6/merge_change/merge-out-timestamp.osc",
				new String[] {"conflictResolutionMethod=timestamp"});
	}

	/**
	 * Test the version conflict resolution strategy.
	 * 
	 * @throws Exception if something goes wrong
	 * 
	 */
	@Test
	public void versionConflictResolution() throws Exception {
		checkMergeChange("v0_6/merge_change/merge-in-1.osc",
				"v0_6/merge_change/merge-in-2-version.osc", 
				"v0_6/merge_change/merge-out-version.osc",
				new String[] {"conflictResolutionMethod=version"});

		checkMergeChange("v0_6/merge_change/merge-in-2-version.osc",
				"v0_6/merge_change/merge-in-1.osc", 
				"v0_6/merge_change/merge-out-version.osc",
				new String[] {"conflictResolutionMethod=version"});
	}
	
	/**
	 * Test the last source conflict resolution strategy.
	 * 
	 * @throws Exception if something goes wrong
	 * 
	 */
	@Test
	public void lastSourceConflictResolution() throws Exception {
		checkMergeChange("v0_6/merge_change/merge-in-1.osc",
				"v0_6/merge_change/merge-in-2-timestamp.osc", 
				"v0_6/merge_change/merge-in-2-timestamp.osc",
				new String[] {"conflictResolutionMethod=lastSource"});

		checkMergeChange("v0_6/merge_change/merge-in-2-timestamp.osc",
				"v0_6/merge_change/merge-in-1.osc", 
				"v0_6/merge_change/merge-in-1.osc",
				new String[] {"conflictResolutionMethod=lastSource"});
	}
	
	private void checkMergeChange(String leftFileName, String rightFileName, 
			String expectedOutputFileName, String[] parameters) throws Exception {
		File leftFile;
		File rightFile;
		File expectedOutputFile;
		File actualOutputFile;
		
		leftFile = dataUtils.createDataFile(leftFileName);
		rightFile = dataUtils.createDataFile(rightFileName);
		expectedOutputFile = dataUtils.createDataFile(expectedOutputFileName);
		actualOutputFile = dataUtils.newFile();

		List<String> argsList = new ArrayList<String>(Arrays.asList(
				"-q",
				"--read-xml-change-0.6", rightFile.getPath(),
				"--read-xml-change-0.6", leftFile.getPath(),
				"--merge-change-0.6"));
		
		if (parameters != null) {
			for (String param : parameters) {
				argsList.add(param);
			}
		}
		
		argsList.addAll(Arrays.asList(
				"--write-xml-change-0.6", actualOutputFile.getPath()));
		Osmosis.run(argsList.toArray(new String[argsList.size()]));

		dataUtils.compareFiles(expectedOutputFile, actualOutputFile);
	}

}

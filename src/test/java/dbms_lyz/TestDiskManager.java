package test.java.dbms_lyz;

import junit.framework.TestCase;

public class TestDiskManager extends TestCase {

	public void testGetInstance() {
		
		assertNotNull(DiskManager.getInstance());
	}

	public void testCreateFile() {
		fail("Not yet implemented");
	}

	public void testAddPage(int fileIdx) {
		PageId p = DiskManager.addPage(fileIdx);
		assertEquals(fileIdx, p.getFileIdx());
		
	}

	public void testReadPage() {
		fail("Not yet implemented");
	}

	public void testWritePage() {
		fail("Not yet implemented");
	}

}

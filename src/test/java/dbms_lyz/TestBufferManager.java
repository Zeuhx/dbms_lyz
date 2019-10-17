package test.java.dbms_lyz;

import junit.framework.TestCase;

public class TestBufferManager extends TestCase {
	
	public void testGetInstance() {
		fail("Not yet implemented");
	}

	public void testSearchFrame() throws Exception{
		//Test pour pas trouvé
		PageId p = new PageId("test");
		assertEquals(2,BufferManager.getInstance().searchFrame(p));
		fail("Not yet implemented");
	}

	public void testAfficheFrame() {
		fail("Not yet implemented");
	}

	public void testLRU() {
		fail("Not yet implemented");
	}

	public void testGetPage() {
		fail("Not yet implemented");
	}

	public void testFreePage() {
		fail("Not yet implemented");
	}

	public void testFlushAll() {
		fail("Not yet implemented");
	}

}

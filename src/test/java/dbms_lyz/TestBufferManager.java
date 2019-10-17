package test.java.dbms_lyz;

import junit.framework.TestCase;

import  main.java.dbms_lyz.*; 

public class TestBufferManager extends TestCase {

	public void testGetInstance() {
		assertNotNull(BufferManager.getInstance());
	}

	public void testSearchFrame() throws Exception{
		//Test pour pas trouv�
		PageId p = new PageId("test");
		BufferManager.getInstance().getPage(p);
		assertNotNull(BufferManager.getInstance().searchFrame(p));

	}

	public void testAfficheFrame() {
		fail("Not yet implemented");
	}

	public void testLRU() {
		fail("Not yet implemented");
	}

	public void testGetPage(PageId p) {
		BufferManager.getInstance().getPage(p);
		if(BufferManager.getInstance().searchFrame(p) != BufferManager.getInstance().getFrame(0))
			fail("Echec getPage");
	}

	public void testFreePage() {
		fail("Not yet implemented");
	}

	public void testFlushAll() {
		PageId p1 = new PageId("Page1");
		BufferManager.getInstance().getPage(p1);
		PageId p2 = new PageId("Page2");
		BufferManager.getInstance().getPage(p2);

		BufferManager.getInstance().flushAll();

		if((BufferManager.getInstance().searchFrame(p1) != null) && (BufferManager.getInstance().searchFrame(p2) != null))
			fail("Flush a echoue");
	}

}

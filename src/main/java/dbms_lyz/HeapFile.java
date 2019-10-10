package main.java.dbms_lyz;

public class HeapFile {
	private RelDef relDef ;
	
	public HeapFile(RelDef relDef) {
		this.relDef = relDef ;
	}
	
	public void createOnDisk() {
		int relDeffileIdx = relDef.getFileIdx() ;
		DiskManager.createFile(relDeffileIdx);
		PageId pageId = DiskManager.addPage(relDeffileIdx);
		Frame f1 = new Frame(pageId);
		BufferManager bf = BufferManager.getInstance() ;
		
	}
}

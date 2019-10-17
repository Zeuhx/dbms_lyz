package main.java.dbms_lyz;

import java.nio.ByteBuffer;

public class HeapFile {
	private HeaderPage headerPage ;
	private RelDef relDef ;
	private Object byteMap ;	// A definir le type
	
	public HeapFile(RelDef relDef) {
		this.relDef = relDef ;
		// Faire le calcul de la bytemap
	}
	
	public void createOnDisk() {
		int relDeffileIdx = 0 ;
//		relDeffileIdx = relDef.getFileIdx() ; Ajouter apres avoir creer la fonction
		DiskManager.createFile(relDeffileIdx);
		PageId pageId = DiskManager.addPage(relDeffileIdx);
		Frame f1 = new Frame(pageId);
		BufferManager bf = BufferManager.getInstance() ;
		
	}
}

package main.java.dbms_lyz;

import java.nio.ByteBuffer;

public class HeapFile {
	private PageId headerPage ;	// A 0
	private RelDef relDef ;
	private Object byteMap ;	// A definir le type
	private BufferManager bfm;
	
	public HeapFile(RelDef relDef) {
		this.relDef = relDef ;
		// Faire le calcul de la bytemap
	}
	
	public void createOnDisk() {
		int relDeffileIdx = 0 ;
		relDeffileIdx = relDef.getFileIdx() ; // Ajouter apres avoir creer la fonction
		DiskManager.createFile(relDeffileIdx);
		PageId pageId = DiskManager.addPage(relDeffileIdx);
		Frame f1 = new Frame(pageId);
		BufferManager bf = BufferManager.getInstance() ;
		//TODO liberer qupres du buffer manager (qvec le bon dirty）
	}
	
	public void addDataPage(PageId pageId) {
		DiskManager.addPage(pageId.getFileIdx());

		//actualiser les infor;ations de la header page
		// TODO voir le " attention " du td 
		bfm.freePage(pageId, true);
	}
	
	/*
	 * return PageId d une donnees qui a encore des cases libres
	 */
	public PageId getFreeDataPageId(PageId pageId) {
		
		return null;
	}
	
	
}

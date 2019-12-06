package main.java.dbms_lyz;

import java.nio.ByteBuffer;

/**
 * Classe Frame contenant des id de page 
 * @author LYZ
 *
 */
public class Frame {
	private int compteurGeneralLRU  = 0;
	private int compteurPersoLRU ;
	private ByteBuffer buff = ByteBuffer.allocate(Constants.PAGE_SIZE);;
	private PageId pageId;
	private int pin_count;
	private boolean flag_dirty;

	public Frame(PageId pageId) {
		compteurPersoLRU = compteurGeneralLRU ;
		compteurGeneralLRU++ ;
		this.pageId = pageId;
		pin_count = 0;
		flag_dirty = false;
	}

	/**
	 * si pin_cout est > 0 alors on decrement sinon rien
	 * 
	 * @param flag_dirty
	 */
	public void decrementePinCount() {
		if (pin_count>0) pin_count-- ;
	}
	
	public void flushFrame() {
		pageId = null;
		pin_count = 0;
		flag_dirty = false;
	}

	// Met le buffer de la page dans le Frame
	public void chargerPage() {
		DiskManager.getInstance().readPage(pageId, buff);
	}
	
	// enregistre la page depuis le buffer
	public void enregistrerPage() {
		if(flag_dirty) { 
			DiskManager.getInstance().writePage(pageId, buff); 
			pin_count = 0 ;
		}
	}
	
	public boolean equals(PageId autrePageId) { return this.pageId.equals(autrePageId); }

	// Pas un getter maj de pin_count
	public void incrementePinCount() 		
	{ 
		pin_count++; 
		}
	
	// Pas un setter maj de dirty
	public void setDirty() 					{ this.flag_dirty = true ; }

	// Getters
	public PageId getPageId() 				{ return pageId; }
	public int getPageIdx() 				{ return pageId.getPageIdx(); }
	public ByteBuffer getBuffer() 			{ return buff; }
	public int getPin_count() 				{ return pin_count ;}
	public boolean getFlag_dirty() 			{ return flag_dirty; }
	public ByteBuffer getByteBuffer() 		{ return buff; }
	public int getCompteurPersoLRU()		{ return compteurPersoLRU; }

	// Setters
	public void setBuff(ByteBuffer buff) 	{ this.buff = buff; }
	public void setPageId(PageId pageId) 	{ this.pageId = pageId; }
	public void setCompteurGeneralLRU(int compteurGeneralLRU) { this.compteurGeneralLRU = compteurGeneralLRU; }

	@Override
	public String toString() {
		return "Frame [pageId=" + pageId + ", pc="
				+ pin_count + ", dirty=" + flag_dirty + "]";
	}
	
	
	
}

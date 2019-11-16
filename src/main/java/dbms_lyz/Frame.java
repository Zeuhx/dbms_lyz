package main.java.dbms_lyz;

import java.nio.ByteBuffer;

/**
 * Classe Frame contenant des id de page 
 * @author LYZ
 *
 */
public class Frame {
	private ByteBuffer buff;
	private PageId pageId;
	private int pin_count;
	private boolean flag_dirty;
	private boolean LRU_change;

	public Frame(PageId pageId) {
		this.pageId = pageId;
		DiskManager.writePage(pageId, buff);
		pin_count = 0;
		flag_dirty = false;
	}
	
	public Frame() {
		pageId = null ;
		buff = ByteBuffer.allocate(Constants.PAGE_SIZE);
		pin_count = 0;
		flag_dirty = false;
	}
	
	public Frame(boolean LRU_change) {
		buff = ByteBuffer.allocate(Constants.PAGE_SIZE);
		pin_count = 0;
		flag_dirty = false;
		this.LRU_change = LRU_change;
	}

	public void setLRU_change(boolean b) {
		if(pin_count >=1) LRU_change = false;
		else LRU_change = b;
	}

	public PageId getPageId() { return pageId; }

	public int getPageIdx() {
		return pageId.getPageIdx();
	}

	public ByteBuffer getBuffer() {
		return buff;
	}

	/**
	 * si pin_cout est > 0 alors on decrement sinon rien
	 * 
	 * @param flag_dirty
	 */
	public void freeMoins(boolean flag_dirty) {
		if (pin_count != 0)	pin_count-- ;

		if (this.flag_dirty == true && (flag_dirty==false)) this.flag_dirty = true ;
		else this.flag_dirty = false ;

		if (this.flag_dirty == true && (flag_dirty==false)) {
			this.flag_dirty = true;
		}
		else
			this.flag_dirty = false;
		
		if(pin_count == 0) 
			this.LRU_change = true;
		// Dans quel cas LRU_change => True
//		if(pin_count == 0) {
//			
//			if(f.getLRU_change()) {
//			LRU_change = false;
//			f.setLRU_change(true);
//			}
//			else LRU_change = true;
//		}

	}
	
	public void flushFrame() {
		pageId = null;
		pin_count = 0;
		flag_dirty = false;
	}
	
	// Pas un getter
	public void getPlus() { pin_count++; }

	// Getters
	public int getPin_count() { return pin_count ;}
	public boolean getFlag_dirty() { return flag_dirty; }
	public ByteBuffer getByteBuffer() { return buff; }
	
	public boolean getLRU_change() {
		return LRU_change;
	}

	// Setters
	public void setBuff(ByteBuffer buff) { this.buff = buff; }
	public void setPageId(PageId pageId) { this.pageId = pageId; }
	
	
}

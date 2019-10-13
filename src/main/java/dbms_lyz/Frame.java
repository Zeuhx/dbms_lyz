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
		buff = ByteBuffer.allocate(4096);
		pin_count = 0;
		flag_dirty = false;
	}

	public Frame(boolean LRU_change) {
		this.pageId = null;
		buff = ByteBuffer.allocate(4096);
		pin_count = 0;
		flag_dirty = false;
		this.LRU_change = LRU_change;
	}

	public void setLRU_change(boolean b) {
		LRU_change = b;
	}

	public PageId getPageId() {
		return pageId;
	}

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
	public void free(boolean flag_dirty) {
		if (pin_count != 0)
			pin_count--;

		if (flag_dirty == false)
			this.flag_dirty = true;

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

	public void get() {
		pin_count++;
	}

	public int getPin_count() {
		return pin_count;
	}

	public boolean getFlag_dirty() {
		return flag_dirty;
	}

	public boolean getLRU_change() {
		return LRU_change;
	}

	public ByteBuffer getByteBuffer() {
		return buff;
	}

	public void flushFrame() {
		pageId = null;
		pin_count = 0;
		flag_dirty = false;
	}

}

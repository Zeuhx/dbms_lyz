package dbms_lyz;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Frame {
	private ByteBuffer buff ;
	private PageId pageId ;
	private int pin_count ;
	private boolean flag_dirty ;
	private boolean last_LRU;
	
	public Frame(PageId pageId) {
		this.pageId = pageId ;
		buff = ByteBuffer.allocate(4096);
		pin_count = 0 ;
		flag_dirty = false ;
		last_LRU = false;
	}
	public Frame() {
		this(null);
	}
	public void setLast_LRU(boolean b) {
		last_LRU = b;
	}
	
	public PageId getPageId() {
		return pageId;
	}
	
	public ByteBuffer getBuffer() {
		return buff;
	}
	
	
	
	public void free(boolean flag_dirty, Frame f){
		if(pin_count != 0)	
			pin_count --;
		
		if(flag_dirty== false)
			this.flag_dirty = true;
		if(pin_count == 0 && f.getLast_LRU()) {
			last_LRU = false;
			f.setLast_LRU(true);
		}		
	}
	
	public void get(){
		pin_count ++;
	}
	
	public int getPin_count()
	{
		return pin_count;
	}
	
	public boolean getFlag_dirty() {
		return flag_dirty;
	}
	public boolean getLast_LRU() {
		return last_LRU;
	}

	
}

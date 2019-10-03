package dbms_lyz;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Frame {
	private ByteBuffer buff ;
	private PageId pageId ;
	private int pin_count ;
	private boolean flag_dirty ;
	
	public Frame(PageId pageId) {
		this.pageId = pageId ;
		buff = ByteBuffer.allocate(4096);
		pin_count = 0 ;
		flag_dirty = false ;
	}
	
	public PageId getPageId() {
		return pageId;
	}
	
	public ByteBuffer getBuffer() {
		return buff;
	}
	
	
	public void free(boolean flag_dirty){
		if(pin_count != 0)	
			pin_count --;
		
		if(flag_dirty== false)
			this.flag_dirty = true;
		
		
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
	
}

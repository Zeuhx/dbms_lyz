package dbms_lyz;

import java.nio.ByteBuffer;

public class Frame {
	private ByteBuffer buff ;
	private PageId pageId ;
	private int pin_count ;
	private int flag_dirty ;
	
	public Frame(PageId pageId) {
		this.pageId = pageId ;
		buff = ByteBuffer.allocate(4096);
		pin_count = 0 ;
		flag_dirty = 0 ;
	}
	
	public PageId getPageId() {
		return pageId;
	}

}

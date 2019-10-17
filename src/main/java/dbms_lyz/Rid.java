package main.java.dbms_lyz;

public class Rid {
	private PageId pageId;	// indique la page a laquelle appartient le Record
	private int slotIdx;	// indice de la case ou le Record est stocké 
	
	
	public Rid(PageId pageId, int slotIdx) {
		this.pageId = pageId;
		this.slotIdx = slotIdx;
	}
	

}

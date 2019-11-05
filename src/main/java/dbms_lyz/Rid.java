package main.java.dbms_lyz;
/**
 * Votre classe contiendra deux variables membres :
	ï pageId, un PageId qui indique la page a laquelle appartient le Record
	ï slotIdx, un entier qui est l indice de la case ou le Record est stocke.
 * @author willy
 *
 */
public class Rid {
	private PageId pageId;	// indique la page a laquelle appartient le Record
	private int slotIdx;	// indice de la case ou le Record est stock√© 
	
	public Rid(PageId pageId, int slotIdx) {
		this.pageId = pageId;
		this.slotIdx = slotIdx;
	}
}

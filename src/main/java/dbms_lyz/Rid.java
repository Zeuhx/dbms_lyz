package main.java.dbms_lyz;
/**
 * Votre classe contiendra deux variables membres :
 * 	pageId, un PageId qui indique la page a laquelle appartient le Record
 * 	slotIdx, un entier qui est l indice de la case ou le Record est stocke.
 * @author willy
 *
 */
public class Rid {
	private PageId pageId;	// indique la page a laquelle appartient le Record
	private int slotIdx;	// indice de la case ou le Record est stocke
	
	public Rid(PageId pageId, int slotIdx) {
		this.pageId = pageId;
		this.slotIdx = slotIdx;
	}

	public PageId getPageId() {
		return pageId;
	}

	public int getSlotIdx() {
		return slotIdx;
	}
}

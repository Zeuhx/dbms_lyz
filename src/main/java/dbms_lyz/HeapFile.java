package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Liste de page de donnee dont la premiere page se nomme 
 * HeaderPage avec pour indice 0 possedant un entier N (nb de page en tout)
 * et ensuite X entier pour les nb de cases dispo dans chaque page de donnee
 * @author cedzh
 *
 */
public class HeapFile {
	private RelDef relDef ;

	public HeapFile(RelDef relDef) {
		this.relDef = relDef ;
	}
	
	/**
	 * Creation du fichier disque correspondant au HeapFile
	 */
	public void createNewOnDisk() {
		int relDef_fileIdx = relDef.getFileIdx(); // L'indice du fichier est donnee par relDef
		DiskManager.createFile(relDef_fileIdx);
		/**
		 * TODO Rajouter une headerPage Vide ??
		 */
		PageId headerPage = DiskManager.addPage(relDef_fileIdx); 
		// ^ TODO On ajoute le headerPage ??
		
		Frame f1 = new Frame(headerPage);
		f1.setBuff(ByteBuffer.allocate(Constants.pageSize));
		BufferManager bf = BufferManager.getInstance() ;
		// TODO liberer aupres du buffer manager (avec le bon dirty）
	}
	
	public void addDataPage(PageId pageId) {
		DiskManager.addPage(pageId.getFileIdx());
		//actualiser les informations de la headerPage
		// TODO voir le " attention " du td 
//		freePage(pageId, true);
	}
	
	/*
	 * return PageId d une page de donnees qui a encore des cases libres
	 */
	public PageId getFreeDataPageId() {
		/**
		 * Parcours tous les pageId, et cherche dans chaque fichier, le nombre de case vide
		 */
		return null;
	}
	public Rid writeRecordToDataPage(Record record, PageId pageId) {
		
		return null;
	}

	
	public RelDef getRelDef() {
		return relDef;
	}
	
	public List<Record> getRecordInDataPage(PageId pageId, List<Record> record) {
		
		return null;
	}
	
}

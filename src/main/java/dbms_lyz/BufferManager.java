package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Classe Buffer contenant les frames
 * @author LYZ
 *
 */
public class BufferManager {

	/**
	 * Singleton
	 */
	public static List<Frame> listFrame = new ArrayList<>();
	private BufferManager() {}
	private static BufferManager INSTANCE = null;

	public static BufferManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BufferManager();
			Frame frame1 = new Frame();
			Frame frame2 = new Frame();
			listFrame.add(frame1);
			listFrame.add(frame2);
		}
		return INSTANCE;
	}

	/**
	 * On cherche le Frame qui correpond a un PageId
	 * 
	 * @param page
	 * @return l'index de Frame qui correspondant au PageId
	 * @return -1 : la pageId n'est pas dans le
	 */

	public int searchFrame(PageId pageId) {
		if(pageId == null) {
			throw new NullPointerException("PageId dans searchFrame est null");
		}
		BufferManager.getInstance();
		if(listFrame.get(0).getPageId() == (pageId)) {
			return(0);
		}
		else if(listFrame.get(1).getPageId() == (pageId)) {
			return(1);
		}
		else
			return -1;
		
	}

	/**
	 * POUR LE TEST (TEMPORAIRE) NON DEMANDE
	 */
	public void afficheFrame(List<Frame> listFrame) {
		for (int i = 0; i < listFrame.size(); i++) {
			System.out.println("[frame \" + i + \"] : ");
			System.out.println("page id : " + (listFrame.get(i)).getPageIdx() + "| pin count : "
					+ (listFrame.get(i)).getPin_count() + "| dirty : " 
					+ (listFrame.get(i)).getFlag_dirty()
					+ "| LRU : " + (listFrame.get(i)).getLRU_change());
		}
	}

	/**
	 * Cette methode doit reppondre a une demande de page venant des couches plus
	 * hautes, et donc retourner un des buffers associeer a une case. Le buffer sera
	 * rempli avec le contenu de la page designee par argument pageId.
	 * 
	 * @param pageId
	 * @return
	 */
	public ByteBuffer getPage(PageId pageId) {
		boolean pageExist;
		int indexFrame = searchFrame(pageId);
		ByteBuffer byteBuff ;
		Frame f = null ;
		
		if(pageId == null)
			throw new RuntimeException("PageId dans le getPage est null");
		// on n'a pas trouver la page
		if (indexFrame == -1)
			pageExist = false;
		else {
			// La page est dans un des deux frames
			f = listFrame.get(indexFrame) ;
			pageExist = true;
			byteBuff = f.getBuffer();
		}
			
		// la page est dans l'une des deux frames, donc on incremente juste le pinCount
		if (pageExist) {
			f = listFrame.get(indexFrame);
			f.getPlus(); 
			return f.getBuffer();
		}
		else {
			// On verifie si l'un des deux frame du pool est bon
			if(listFrame.get(0) == null) {
				listFrame.set(0, new Frame(pageId));
				listFrame.get(0).getPlus();
				return(listFrame.get(0).getBuffer());
			} else {
				if(listFrame.get(1) == null) {
					listFrame.set(1, new Frame(pageId));
					listFrame.get(1).getPlus();
					return(listFrame.get(1).getBuffer());
				}
			}
			
			// On rentre dans le cas ou les deux sont occupe
			if(listFrame.get(0).getPin_count() >= 1 && listFrame.get(1).getPin_count() >= 1) {
				throw new RuntimeException("Les deux frames sont occupe");
			}

			// Si un des deux pinCount est a 0, alors on peut remplacer directement
			if(listFrame.get(0).getPin_count()==0 || listFrame.get(1).getPin_count()==0) {
				if(listFrame.get(0).getFlag_dirty()==false) {
					listFrame.set(0, new Frame(pageId));
					listFrame.get(0).getPlus();
					return(listFrame.get(0).getBuffer());
				} 
				else if(listFrame.get(1).getFlag_dirty()==false) {
					listFrame.set(1, new Frame(pageId));
					listFrame.get(1).getPlus();
					return(listFrame.get(1).getBuffer());
				} 
			}
			// Sinon il faut verifier leur etat de LRU
			else {
				// si les deux sont occupe 
				// On ecrit le contenu de la frame concerne dans le disque
				// Pour ne pas perdre les donnees
				if(listFrame.get(0).getLRU_change() && listFrame.get(0).getFlag_dirty()) {
					DiskManager.writePage(listFrame.get(0).getPageId(), listFrame.get(0).getBuffer());
					listFrame.set(0, new Frame(pageId));
					listFrame.get(0).getPlus();
					return(listFrame.get(0).getBuffer());
				}
				else if(listFrame.get(1).getLRU_change() && listFrame.get(1).getFlag_dirty()){
					DiskManager.writePage(listFrame.get(1).getPageId(), listFrame.get(1).getBuffer());
					listFrame.set(1, new Frame(pageId));
					listFrame.get(1).getPlus();
					return(listFrame.get(1).getBuffer());
				}
				//Si le flag dirty est false
				else if(listFrame.get(0).getLRU_change() && !listFrame.get(0).getFlag_dirty()){
					listFrame.set(0, new Frame(pageId));
					listFrame.get(0).getPlus();
					return(listFrame.get(0).getBuffer());
				}
				else if(listFrame.get(1).getLRU_change() && !listFrame.get(1).getFlag_dirty()) {
					listFrame.set(1, new Frame(pageId));
					listFrame.get(1).getPlus();
					return(listFrame.get(1).getBuffer());
				}
				else {
					throw new RuntimeException("(bis) Les deux frames sont occupe");
				}
			}
		}
		return(null);
	}

	/**
	 * 
	 * Cette méthode devra décrémenter le pin_count et actualiser le flag dirty de
	 * la page.
	 * 
	 * @param pageId
	 * @param valdirty
	 */
	public void freePage(PageId pageId, boolean valdirty) {
		int indexFrame = searchFrame(pageId);
		Frame f = listFrame.get(indexFrame);
		if (indexFrame == -1)
			System.err.println("Frame pas trouve");
		else {
			f.freeMoins(valdirty);
		}
	}

	/**
	 * 
	 * Cette méthode s’occupe de : ◦ l’écriture de toutes les pages dont le flag
	 * dirty = 1 sur disque ◦ la remise à 0 de tous les flags/informations et
	 * contenus des buffers (buffer pool « vide »)
	 */
	public void flushAll() throws FlagException {
		DBManager.finish();
		for (int i = 0; i < Constants.FRAME_COUNT; i++) {

			if (listFrame.get(i).getFlag_dirty()) {
				DiskManager.writePage(listFrame.get(i).getPageId(), getPage(listFrame.get(i).getPageId()));
			}
		
		System.out.println("initialisation de la memoire apres une ecriture sur DiskManager");}
		listFrame.get(0).flushFrame();
		listFrame.get(1).flushFrame();
		// TODO : Rajoutez un appel  cette methode dans la methode Finish du DBManager.

	}

}

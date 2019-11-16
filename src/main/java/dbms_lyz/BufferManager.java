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
	private Frame frame;

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
	 * @return 2 : la pageId n'est pas dans le
	 */

	public int searchFrame(PageId pageId) {
		BufferManager.getInstance();
		if(listFrame.get(0).getPageId() == null) {
			listFrame.get(0).setPageId(pageId);
			return(0);
		}
		else if(listFrame.get(1).getPageId() == null) {
			listFrame.get(1).setPageId(pageId);
			return(1);
		}
		
		// sinon, on compare id de la page dans chaque frame avec id de PageID
		for(int i=0 ; i<listFrame.size() ; i++) {
			Frame f = listFrame.get(i);

		//TODO si y 'a rien dans le premier frame on remplace directement
		//TODO si y 'a rien dans le deuxieme frame on remplace directement
		

			if (f.getPageId().equals(pageId))
				return i;
			else {
				System.err.println("la PageId " + pageId.getPageIdx() + " n'est pas dans les frames");
				// TODO ++ Politque de remplacement
			}
					
		}
		return (2); // Pour retourner l'index de la frame concerne retourne 2 l'exeption est traite dans getPage()
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
		Frame getFrame = new Frame(pageId);
		ByteBuffer bf = getFrame.getBuffer();
		int indexFrame = searchFrame(pageId);
		
		//si la new pageId n'est pas trouve dans la liste de frame??
		if (indexFrame == 2)
			pageExist = false;
		else
			pageExist = true;
			getFrame = listFrame.get(indexFrame);

		//si newFrame est dans la liste
		if (pageExist) {
			DiskManager.readPage(pageId, BufferManager.listFrame.get(0).getBuffer());
			//on ajoute si frame exist dans la liste le buffer de newFrame 
			frame.getPlus();
			//Maj du LRU_change
			//si newFrame correspond au premier
			if (pageId == listFrame.get(0).getPageId()) {
				DiskManager.readPage(listFrame.get(0).getPageId(), bf);
				
				// si jamais l'autre frame a pin_count>0 
				if(!listFrame.get(1).getLRU_change()) {
					//les deux frame ne peuvent etre remplacer
					listFrame.get(0).setLRU_change(false);
				}
				else {
					listFrame.get(0).setLRU_change(false);
					listFrame.get(1).setLRU_change(true);
				}
			
				//si newFrame correspond au deuxieme
			} else {
				DiskManager.readPage(listFrame.get(1).getPageId(), bf);

				// si jamais l'autre frame a pin_count>0 
				if(!listFrame.get(0).getLRU_change()) {
					//les deux frame ne peuvent etre remplacer
					listFrame.get(1).setLRU_change(false);
				}
				else {
					listFrame.get(0).setLRU_change(true);
					listFrame.get(1).setLRU_change(false);
				}
			}
		}
		
		//si jamais le newFrame (pageId en entre) n est pas dans la liste on remplace

		else {
			System.out.println("la page n'existe pas dans les frame");
			//condition si pin count > 0
			if (((listFrame.get(0)).getPin_count() == 0) || (listFrame.get(1).getLRU_change())){
				System.out.println("Frame dispo, condition pin_count = 0 [OK]");

				//changement new frame par rapport a LRU
				if(listFrame.get(0).getLRU_change() == true) {
					System.out.println("changement du premier frame ");
					listFrame.remove(0);
					listFrame.add(getFrame);

					//maj LRU etat si jamais l'autre frame a pin_count>0 
					if(!listFrame.get(1).getLRU_change()){
						listFrame.get(0).setLRU_change(true);
					}
					else {
						//maj de la valeur de LRU etat
						listFrame.get(0).setLRU_change(false);
						listFrame.get(1).setLRU_change(true);
					}
				}
				else if (listFrame.get(1).getLRU_change() == true){
					System.out.println("changement du second frame");
					listFrame.remove(1);
					listFrame.add(getFrame);
					// si jamais l'autre frame a pin_count>0 
					if(!listFrame.get(0).getLRU_change()){
						listFrame.get(1).setLRU_change(true);
					}
					else {
						//maj de la valeur de LRU etat
						listFrame.get(0).setLRU_change(true);
						listFrame.get(1).setLRU_change(false);
					}
				}
			}
			else System.out.println("aucune condition n'est realise : aucun frame dispo");
		}
		return (bf);
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
		if (indexFrame == 2)
			System.out.println("Frame pas trouve");
		else {
			frame = listFrame.get(indexFrame);
			frame.free(valdirty);
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

	// Une methode pour modif les pages qui sont dans les frames

	
	/**
	 * Autre getPage()
	 */
//	public ByteBuffer getPage(PageId pageId) {
//	ByteBuffer bf = null;
//	int indexFrame = searchFrame(pageId);
//	
//	//si la new pageId n'est pas trouve dans la liste de frame??;
//	if (indexFrame == 2)
//		frame = null;
//	else
//		//comprend pas pourquoi f doit get le indexFrame
//		frame = listFrame.get(indexFrame);
//
//	if (frame != null) {
//		DiskManager.readPage(pageId, BufferManager.listFrame.get(0).getBuffer());
//		frame.get();
//		if (pageId == listFrame.get(0).getPageId()) {
//			listFrame.get(0).setLRU_change(false);
//			listFrame.get(1).setLRU_change(true);
//		} else {
//			listFrame.get(1).setLRU_change(false);
//			listFrame.get(0).setLRU_change(true);
//		}
//	}
//	return (bf);
//}
}

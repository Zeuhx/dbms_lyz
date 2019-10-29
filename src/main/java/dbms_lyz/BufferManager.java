package main.java.dbms_lyz;

import java.io.IOException;
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

	public static List<Frame> listFrame = new ArrayList<>();

	private BufferManager() {}

	private static BufferManager INSTANCE = null;
	private Frame frame;

	public static BufferManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BufferManager();
			Frame frame1 = new Frame(true);
			Frame frame2 = new Frame(false);
			listFrame.add(frame1);
			listFrame.add(frame2);
		}
		return INSTANCE;
	}

	/**
	 * On cherche le Frame qui correpond a un PageId
	 * 
	 * @param page
	 * @return le Frame correspondant au PageId
	 */

	public int searchFrame(PageId pageId) {
		BufferManager.getInstance();
		frame = null;
		int i = 0;

		for (Frame f : listFrame) {
			if (listFrame.get(i).equals(pageId))
				return i;
		}

		return (2); // Pour retourner l'index de la frame concern� retourne 2 si pas trouve
	}

	/**
	 * POUR LE TEST ( TEMPORAIRE ) NON DEMANDE
	 */
	public void afficheFrame(List<Frame> listFrame) {
		for (int i = 0; i < listFrame.size(); i++) {
			System.out.println("frame " + i);
			System.out.println("page id : " + (listFrame.get(i)).getPageIdx() + ", pin count : "
					+ (listFrame.get(i)).getPin_count() + ", dirty : " + (listFrame.get(i)).getFlag_dirty());
		}
	}


	/**
	 * Cette methode doit reppondre a� une demande de page venant des couches plus
	 * hautes, et donc retourner un des buffers associeer a� une case. Le buffer sera
	 * rempli avec le contenu de la page designee par  argument pageId.
	 * 
	 * @param pageId
	 * @return
	 */
	
//	public ByteBuffer getPage(PageId pageId) {
//		ByteBuffer bf = null;
//		int indexFrame = searchFrame(pageId);
//		
//		//si la new pageId n'est pas trouve dans la liste de frame??;
//		if (indexFrame == 2)
//			frame = null;
//		else
//			//comprend pas pourquoi f doit get le indexFrame
//			frame = listFrame.get(indexFrame);
//
//		if (frame != null) {
//			DiskManager.readPage(pageId, BufferManager.listFrame.get(0).getBuffer());
//			frame.get();
//			if (pageId == listFrame.get(0).getPageId()) {
//				listFrame.get(0).setLRU_change(false);
//				listFrame.get(1).setLRU_change(true);
//			} else {
//				listFrame.get(1).setLRU_change(false);
//				listFrame.get(0).setLRU_change(true);
//			}
//		}
//		return (bf);
//	}

	public ByteBuffer getPage(PageId pageId) {
		//newFrame est de type Frame
		//TODO : on recupere le buff de newFrame puis on l'ajoute sur la listFrame si exist
		Frame newFrame = new Frame(pageId);
		ByteBuffer bf = newFrame.getBuffer();
		int indexFrame = searchFrame(pageId);
		
		//si la new pageId n'est pas trouve dans la liste de frame??
		if (indexFrame == 2)
			frame = null;
		else
			frame = listFrame.get(indexFrame);

		//si newFrame est dans la liste
		if (frame != null) {
			DiskManager.readPage(pageId, BufferManager.listFrame.get(0).getBuffer());
			//on ajoute si frame exist dans la liste le buffer de newFrame 
			frame.get();
			//Maj du LRU_change
			//si newFrame correspond au premier
			if (pageId == listFrame.get(0).getPageId()) {
				DiskManager.readPage(listFrame.get(0).getPageId(), bf);
				
				// si jamais l'autre frame a pin_count>0 
				if(!listFrame.get(1).getLRU_change()) {
					//les deux frame ne peuvent �tre remplacer
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
					//les deux frame ne peuvent �tre remplacer
					listFrame.get(1).setLRU_change(false);
				}
				else {
					listFrame.get(0).setLRU_change(true);
					listFrame.get(1).setLRU_change(false);
				}
			}
		}
		
		//si jamais le newFrame (pageId en entre) n est pas dans la liste on remplaces 
		else if (listFrame.get(0).getPageId()!=newFrame.getPageId() || listFrame.get(0).getPageId()!=newFrame.getPageId() ) {
			System.out.println("le new frame n'existe pas dans la liste");
			 frame = new Frame(frame.getPageId() );
			 
			 //changement new frame par rapport � LRU
			 if(listFrame.get(0).getLRU_change() == true) {
				 System.out.println("changement du premier frame ");
				 listFrame.remove(0);
				 listFrame.add(newFrame);
				
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
				 listFrame.add(newFrame);
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
			 else System.out.println("aucune condition n'est realise : ERROR qlq part");
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
	 * A FAIRE !!
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
		
		System.out.println("initialisation de la m�moire apr�s une �criture sur DiskManager");}
		listFrame.get(0).flushFrame();
		listFrame.get(1).flushFrame();
		// TODO : Rajoutez un appel � cette m�thode dans la m�thode Finish du DBManager.

	}

	// Une m�thode pour modfif les pages qui sont dans les frames

}

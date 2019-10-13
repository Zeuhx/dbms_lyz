package main.java.dbms_lyz;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BufferManager {

	public static List<Frame> listFrame = new ArrayList<>();
	private static Frame frame1 = new Frame(true);
	private static Frame frame2 = new Frame(false);

	private BufferManager() {
	}

	private static BufferManager INSTANCE = null;
	private Frame f;

	public static BufferManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BufferManager();
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
		f = null;
		int i = 0;

		for (Frame f : listFrame) {
			if (listFrame.get(i).equals(pageId))
				return i;
		}

		return (2); // Pour retourner l'index de la frame concern� retourne 2 si pas trouv�
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
	 * 
	 * @return la derniere page utilis�
	 */
//	public Frame LRU() {
//		if(frame1.getLRU_change()) {
//			return frame1;
//		}
//		else return frame2;
//	}
	public Frame LRU() {
		if (listFrame.get(0).getLRU_change()) {
			return listFrame.get(0);
		} else
			return listFrame.get(1);
	}

	/**
	 * Cette méthode doit répondre à une demande de page venant des couches plus
	 * hautes, et donc retourner un des buffers associés à une case. Le buffer sera
	 * rempli avec le contenu de la page désignée par l’argument pageId.
	 * 
	 * @param pageId
	 * @return
	 */
	public ByteBuffer getPage(PageId pageId) {
		ByteBuffer bf = null;
		int indexFrame = searchFrame(pageId);
		if (indexFrame == 2)
			f = null;
		else
			f = listFrame.get(indexFrame);

		if (f != null) {
			DiskManager.readPage(pageId, BufferManager.frame1.getBuffer());
			f.get();
			if (pageId == listFrame.get(0).getPageId()) {
				listFrame.get(0).setLRU_change(false);
				listFrame.get(1).setLRU_change(true);
			} else {
				listFrame.get(1).setLRU_change(false);
				listFrame.get(0).setLRU_change(true);
			}
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
			System.out.println("Frame Pas trouv�");
		else {
			f = listFrame.get(indexFrame);
			f.free(valdirty);
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
		for (int i = 0; i < Constants.frameCount; i++) {

			if (listFrame.get(i).getFlag_dirty()) {
				DiskManager.writePage(listFrame.get(i).getPageId(), getPage(listFrame.get(i).getPageId()));
			}
		}
		frame1.flushFrame();
		frame2.flushFrame();
		// TODO : Rajoutez un appel � cette m�thode dans la m�thode Finish du DBManager.

	}

	// Une m�thode pour modfif les pages qui sont dans les frames

}

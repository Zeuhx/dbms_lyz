package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static Frame[] framePool = new Frame[Constants.FRAME_COUNT];
	private BufferManager() {}
	private static BufferManager INSTANCE = null;

	public static BufferManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BufferManager();
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
	public Frame searchFrame(PageId pageId) {
		for(Frame f : framePool) {
			if(f!=null && (f.getPageId().equals(pageId)))
				return f;
		}
		return null ;
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
		ByteBuffer bytebuff ;
		Frame f = searchFrame(pageId);
		
		System.err.println("Affichage X18 - get("+pageId +")");
		if(f==null) {
			int i = indexLibre();
			if(i==-1) {
				i = calcul_LRU();
				System.out.println("On active la methode de remplacement LRU, on doit remplace la frame " + i);
				framePool[i].enregistrerPage();
			}
			framePool[i] = new Frame(pageId);
			framePool[i].chargerPage();
//			System.out.println("Chargement de " + pageId + " dans "+ i + " " +Arrays.toString(framePool));
			f = framePool[i];
		}
		
		bytebuff = f.getBuffer();
		f.incrementePinCount(); 
		return bytebuff ;
	}

	/**
	 * Cette methode devra decrementer le pin_count 
	 * et actualiser le flag dirty de la page pour savoir si elle a ete modifier
	 * 
	 * @param pageId
	 * @param valdirty
	 */
	public void freePage(PageId pageId, boolean valdirty) {
		Frame f = searchFrame(pageId);
		System.out.println("Affichage X17 - free("+pageId+")");
		if (f == null)
			throw new RuntimeException("Frame pas trouve");
		else {
			f.decrementePinCount();
			if(valdirty)
				f.setDirty();
		}
	}
	
	public int indexLibre() {
		for(int i = 0 ; i<framePool.length ; i++) {
			if(framePool[i] == null) {
				System.out.println("Affichage X20 - Le frame " + i + " est libre, on place la page dedans");
				return i ;
			}
		}
		System.err.println("Il n'y a pas de place"); 
		return -1 ;
	}

	public int calcul_LRU() {
		int min = Integer.MAX_VALUE ;
		int position = -2 ;
		for (int i = 0; i < framePool.length; i++) {
			if(framePool[i].getCompteurPersoLRU() < min && framePool[i].getPin_count() == 0) {
				min = framePool[i].getCompteurPersoLRU();
				position = i ;
			}
		}
		if(min == Integer.MAX_VALUE) throw new RuntimeException("Il n'y a pas de frame dispo");
		return position;
	}
	
	/**
	 * 
	 * Cette méthode s’occupe de : l'ecriture de toutes les pages dont le flag
	 * dirty = 1 sur disque, la remise a 0 de tous les flags/informations et
	 * contenus des buffers (buffer pool « vide »)
	 */
	public void flushAll() throws FlagException {
		DBManager.finish();
		for (Frame frame : framePool) {
			frame.enregistrerPage();
		}
	}

	public static Frame[] getFramePool() { return framePool; }

}

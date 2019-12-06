package main.java.dbms_lyz;

import java.nio.ByteBuffer;

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
	 * Recherche le Frame qui correpond a un PageId
	 * 
	 * @param page
	 * @return le Frame qui correspond
	 * @return null si pas de correspondance
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
	 * hautes, et donc retourner un des buffers associeer a une case. 
	 * 
	 * Le buffer sera rempli avec le contenu de la page designee par argument pageId.
	 * 
	 * @attention : ne pas creer de buffer supplementaire,"recuperer" simplement celui 
	 * qui correspond a la bonne frame, apres l�avoir rempli si besoin par un appel au DiskManager.
	 * @attention : cette methode devra utiuliser une politique de remplacement.
	 * @param pageId un PageId
	 * @return buff un buffer 
	 */
	public ByteBuffer getPage(PageId pageId) {
		ByteBuffer bytebuff ;
		Frame f = searchFrame(pageId);
		if(f==null) {
			int i = indexLibre();
			if(i==-1) {
				i = calcul_LRU(); 
//				System.out.println("Les " + Constants.FRAME_COUNT + " frames sont utilises."
//						+ "\nOn active la methode de remplacement LRU, on doit remplace la frame " + i);
				framePool[i].enregistrerPage();
			}
			framePool[i] = new Frame(pageId);
			framePool[i].chargerPage();
//			System.out.print("Affichage X59 - Affichage du pool - [");
//			for(int j=0 ; j<framePool.length ; j++) {
//				System.out.print(framePool[j] + " | ");
//			}
//			System.out.println("]");
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
	 * @param pageId une PageId
	 * @param valdirty un entier booleen
	 */
	public void freePage(PageId pageId, boolean valdirty) {
		Frame f = searchFrame(pageId);
		if (f == null)
			throw new RuntimeException("La frame que l'on cherche n'a pas ete trouve");
		else {
			f.decrementePinCount();
			if(valdirty)
				f.setDirty();
		}
	}
	
	public int indexLibre() {
		for(int i = 0 ; i<framePool.length ; i++) {
			if(framePool[i] == null) {
				//System.out.println("Affichage X20 - Le frame " + i + " est libre, on place la page dedans");
				return i ;
			}
		}
		return -1 ;
	}

	public int calcul_LRU() {
		int min = Integer.MAX_VALUE;
		int position = -2;
		for (int i = 0; i < framePool.length; i++) {
			if(framePool[i].getCompteurPersoLRU() < min && framePool[i].getPin_count() == 0) {
				min = framePool[i].getCompteurPersoLRU();
				position = i ;
			}
		}
		if(min == Integer.MAX_VALUE) throw new RuntimeException("Il n'y a pas de frame dispo, car les deux sont en cours d'utilisation");
		return position;
	}
	
	/**
	 * Cette methode s'occupe de : l'ecriture de toutes les pages dont le flag
	 * dirty = 1 sur disque, la remise a 0 de tous les flags/informations et
	 * contenus des buffers (buffer pool a vide)
	 */
	public void flushAll(){
		for (Frame frame : framePool) {
			frame.enregistrerPage();
		}
	}

	public static Frame[] getFramePool() { return framePool; }

}

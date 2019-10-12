package main.java.dbms_lyz;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BufferManager {
	
	private static List<Frame> listFrame = new ArrayList<>();
	private static Frame frame1 = new Frame(true);
	private static Frame frame2 = new Frame(false);
	
	private BufferManager(){}

	private static BufferManager INSTANCE = null ;
	private Frame f;

	public static BufferManager getInstance(){           
		if (INSTANCE == null){   
			INSTANCE = new BufferManager(); 
			listFrame.add(frame1);
			listFrame.add(frame2);
		}
		return INSTANCE;
	}

	/**
	 * On cherche le Frame qui correpond a un PageId
	 * @param page
	 * @return le Frame correspondant au PageId
	 */
	
	public Frame searchFrame(PageId pageId) {
		
		f = null;
		for(int i = 0; i< listFrame.size();i++) {
			if((listFrame.get(i)).getPageId().equals(pageId)) {
				f = new Frame(pageId);
			}
		}
		return(f);
	}
	/**
	 * POUR LE TEST ( TEMPORAIRE )
	 * NON DEMANDE
	 */
	public void afficheFrame(List<Frame> listFrame) {
		for(int i=0; i<listFrame.size(); i++) {
			System.out.println("frame "+i);
			System.out.println("page id : "+ (listFrame.get(i)).getPageIdx()+", pin count : "+(listFrame.get(i)).getPin_count()+", dirty : "+(listFrame.get(i)).getFlag_dirty());
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
		if(listFrame.get(0).getLRU_change()) {
			return listFrame.get(0);
		}
		else return listFrame.get(1);
	}
	
	/**
	 * Cette méthode doit répondre à une demande de page venant 
	 * des couches plus hautes, et donc
	 * retourner un des buffers associés à une case.
	 * Le buffer sera rempli avec le contenu de la page 
	 * désignée par l’argument pageId.
	 * @param pageId
	 * @return
	 */
	public ByteBuffer getPage(PageId pageId) {
		ByteBuffer bf = null ;
		Frame f = searchFrame(pageId);
		if(f != null){
			try {
				DiskManager.readPage(pageId, f.getBuffer());
				f.get();
				if(pageId == listFrame.get(0).getPageId()) {
					listFrame.get(0).setLRU_change(false);
					listFrame.get(1).setLRU_change(true);
				}
				else{
					listFrame.get(1).setLRU_change(false);
					listFrame.get(0).setLRU_change(true);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return(bf);
	}

	/**
	 
	 * Cette méthode devra décrémenter le pin_count 
	 * et actualiser le flag dirty de la page.
	 * @param pageId
	 * @param valdirty
	 */
	public void freePage(PageId pageId, boolean valdirty) {
		Frame f = searchFrame(pageId);
		f.free(valdirty);
	}

	/**
	 * A FAIRE !!
	 * 
	 * Cette méthode s’occupe de :
	 * ◦ l’écriture de toutes les pages dont le flag dirty = 1 sur disque
	 * ◦ la remise à 0 de tous les flags/informations 
	 * 		et contenus des buffers (buffer pool « vide »)
	 */
	public void flushAll() {
		DBManager.finish();
		for(int i=0; i<Constants.frameCount; i++) {
			
			//ajout de try catch n�cessaire
			try {
				if(listFrame.get(i).getFlag_dirty()){
					DiskManager.writePage(listFrame.get(i).getPageId(), getPage(listFrame.get(i).getPageId()));
				}
			}
			catch (IOException e) {
				//TODO
				System.out.println("probleme dans flushAll pour la condition si le flag dirty est egal a 1");
						
			}
		}
		frame1.flushFrame();
		frame2.flushFrame();
		//TODO : Rajoutez un appel � cette m�thode dans la m�thode Finish du DBManager.
	}

}

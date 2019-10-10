package test.java.dbms_lyz;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
	 * 
	 * @return la derniere page
	 */
	public Frame LRU() {
		if(frame1.getLRU_change()) {
			return frame1;
		}
		else return frame2;
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
			try {
			if(listFrame.get(i).getFlag_dirty()){

				DiskManager.writePage(listFrame.get(i).getPageId(), getPage(listFrame.get(i).getPageId()));
				DiskManager.writePage(listFrame.get(i).getPageId(), listFrame.get(i).getBuffer());

				DiskManager.writePage(listFrame.get(i).getPageId(), listFrame.get(i).getByteBuffer());
				
				/**ajout de try catch**/
	
			}
			}
			catch (IOException e) {
				//TODO
				System.out.println("probleme dans flushAll dans la condition si le flag dirty est egal a 1");
						
			}
		}
		
		frame1.flushFrame();
		frame2.flushFrame();
	
	}

}

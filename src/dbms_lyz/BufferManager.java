package dbms_lyz;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class BufferManager {

	private BufferManager(){}

	private static BufferManager INSTANCE = null ;
	private Frame f;

	public static BufferManager getInstance(){           
		if (INSTANCE == null){   
			INSTANCE = new BufferManager(); 
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
		if(f.getPageId().equals(pageId)) {
			f = new Frame(pageId);
		}
		return(f);
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return(bf);
	}

	/**
	 *
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
	}

}

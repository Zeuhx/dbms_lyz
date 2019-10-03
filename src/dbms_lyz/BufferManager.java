package dbms_lyz;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class BufferManager {
	
	private BufferManager(){}
	
	private static BufferManager INSTANCE = null ;
	
    public static BufferManager getInstance(){           
        if (INSTANCE == null){   
        	INSTANCE = new BufferManager(); 
        }
        return INSTANCE;
    }
    
    /**
     * A FAIRE !!
     * 
     * On cherche le Frame qui correpond a un PageId
     * @param page
     * @return le Frame correspondant au PageId
     */
    public Frame searchFrame(PageId page) {
    	Frame f = null ;
    	RandomAccessFile rf = null ;
		File fichier = new File("DB/Data_"+page.getFileIdx()+".rf");
		
		if()
		
    	return(f);
    }
    
    /**
     * A FAIRE !!!
     * 
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
    	
    	return(bf);
    }
    
    /**
     * A FAIRE !!
     * 
     * Cette méthode devra décrémenter le pin_count 
     * et actualiser le flag dirty de la page.
     * @param pageId
     * @param valdirty
     */
    public void freePage(PageId pageId, boolean valdirty) {
    	if(!valdirty)
    	{
    		valdirty = true;
    		
    	}
    		
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

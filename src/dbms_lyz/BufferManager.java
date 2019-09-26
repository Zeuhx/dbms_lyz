package dbms_lyz;

public class BufferManager {
private static int compteurRelation ;
	
	private BufferManager(){}
	
	private static BufferManager INSTANCE = null ;
	
    public static BufferManager getInstance(){           
        if (INSTANCE == null){   
        	INSTANCE = new BufferManager(); 
        }
        return INSTANCE;
    }
    
   

}

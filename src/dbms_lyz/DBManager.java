package dbms_lyz;

import java.util.List;

public class DBManager {
	
	/** Constructeur privé */
	private DBManager(){}
	
	/** Instance unique non préinitialisée */
	private static DBManager INSTANCE = null ;
	
    /** Point d'accès pour l'instance unique du singleton */
    public static DBManager getInstance(){           
        if (INSTANCE == null){   
        	INSTANCE = new DBManager(); 
        }
        return INSTANCE;
    }
	
	public static void init() {
		DBDef.init();
	}
	
	public static void finish() {
		DBDef.finish();
	}
	
	public void processCommand(String command) {
		
	}
	
	public RelDef createRelation(String nomRelation, int nombre, List<String> typeCol) {
		RelDef rd = null ;
		return(rd);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBManager.init();
		
		// Boucle de gestion de commande
		
		
	}

}

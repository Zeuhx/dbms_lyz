package dbms_lyz;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Le Marcel, Yu Willy,  Zhang Cedric
 * Garde les infos de schema d'une relation
 *
 */
public class RelDef {
	private String nomRelation ; 
	private int nbCol ;
	private List<String> typeCol ; 
	
	public RelDef(String nomRelation, int nbCol) {
		this.nomRelation = nomRelation ;
		this.nbCol = nbCol ;
		this.typeCol = new ArrayList<String>() ;
	}
	
}


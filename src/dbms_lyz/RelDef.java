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
	private List<Object> typeCol ; 
	
	public RelDef(String nomRelation, int nbCol) {
		this.nomRelation = nomRelation ;
		this.nbCol = nbCol ;
		this.typeCol = new ArrayList<Object>() ;
	}
	
	public RelDef(String nomRelation, int nbCol, List<Object> typeCol) {
		this.nomRelation = nomRelation ;
		this.nbCol = nbCol ;
		this.typeCol = typeCol;
	}
	
	public String getNomRelation() {
		return(nomRelation);
	}
	
	public int getNbCol() {
		return(nbCol);
	}
	
	public List<Object> getTypeCol(){
		return(typeCol);
	}
	
}


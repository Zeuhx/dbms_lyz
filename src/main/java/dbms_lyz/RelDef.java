package main.java.dbms_lyz;

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
	
	public RelDef(String nomRelation, int nbCol, List<Object> typeCol2) {
		this.nomRelation = nomRelation ;
		this.nbCol = nbCol ;
		this.typeCol = typeCol2;
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
	
	public boolean equals(String type) {
		int i = 0 ;
		Object obj = typeCol.get(i).getClass() ;
		
		// stringx 
		if(type.contains("string")) {
			return true ;
		}
		// int float
		else if(obj.toString().contains(type)) {
			return true ;
		}
		else 
			return false ;
	}
	
}


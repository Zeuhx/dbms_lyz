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
	private List<ArrayList<Object>> records;
	
	public RelDef(String nomRelation, int nbCol) {
		this.nomRelation = nomRelation ;
		this.nbCol = 0 ;
		this.typeCol = new ArrayList<Object>() ;
		initRelDef(); //peut etre une erreur
	}
	
	public RelDef(String nomRelation, List<Object> typeCol2) {
		this.nomRelation = nomRelation ;
		this.nbCol = typeCol2.size();
		this.typeCol = typeCol2;
		initRelDef(); //peut etre une erreur

	}
	
	//créer une liste pour chaque colonne a initialiser lorqu'on fait une relation
	public void initRelDef() {
		for(Object o : typeCol) {
				List<Object> l = new ArrayList<>();
				records.add((ArrayList<Object>) l);
			}
	
		}
	public List<ArrayList<Object>> getRecord(){
		return records;
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


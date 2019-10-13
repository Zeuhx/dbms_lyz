package main.java.dbms_lyz;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour garder les infos de schema d'une relation
 * @author LYZ : Le Marcel, Yu Willy, Zhang Cedric 
 *
 */
public class RelDef {
	private String nomRelation;
	private int nbCol;
	private List<Object> typeCol;

	private List<ArrayList<Object>> records;
	private int fileIdx;
	private int recordSize;
	private int slotCount;

	public RelDef(String nomRelation, List<Object> typeCol) {
		this.nomRelation = nomRelation;
		this.nbCol = typeCol.size();
		this.typeCol = typeCol;
		initRelDef(); // peut etre une erreur
	}

	public RelDef(String nomRelation, List<Object> typeCol2, int fileIdx, int recordSize, int slotCount) {
		this.nomRelation = nomRelation;
		this.nbCol = typeCol2.size();
		this.typeCol = typeCol2;
		initRelDef(); // peut etre une erreur (ask Marcel)
		this.fileIdx = fileIdx;
		this.recordSize = recordSize; // Attention : il faut directement initialiser selon la taille du record
		this.slotCount = slotCount;
	}

	// cr�er une liste pour chaque colonne a initialiser lorqu'on fait une relation
	public void initRelDef() {
		for (Object o : typeCol) {
			List<Object> l = new ArrayList<>();
			records.add((ArrayList<Object>) l);
		}

	}

	public List<ArrayList<Object>> getRecord() {
		return records;
	}

	public String getNomRelation() {
		return (nomRelation);
	}

	public int getNbCol() {
		return (nbCol);
	}

	public List<Object> getTypeCol() {
		return (typeCol);
	}

	public boolean equals(String type) {
		int i = 0;
		Object obj = typeCol.get(i).getClass();

		// stringx
		if (type.contains("string")) {
			return true;
		}
		// int float
		else if (obj.toString().contains(type)) {
			return true;
		} else
			return false;
	}

}

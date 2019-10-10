package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Record {
	public static int recordLength = 0; 
	private RelDef relDef ;				// Relation a laquelle Records appartient
	private List<String> values;		// Valeur de Records
	
	public Record(RelDef reldef, List<String> values) {
		this.relDef = reldef ;
		values = new ArrayList<>();
	}
	
	public Record(RelDef reldef) {
		this(reldef, null);
	}
	
	//Cette methode va permettre de recuperer les valeurs de la liste de listes de relDef
	//Sous la forme de string
	//NE pas Oublier d'utiliser des StringBuffer
	
	public void remplirValues() {
		for(int i =0;i<relDef.getRecord().size(); i++) {
			String s ="";
			//je vais supposer que chq col a le meme nombre d'elements
			for(int j=0; j<relDef.getNbCol(); j++) { 
				String t = (String)(relDef.getRecord().get(i).get(j));
				if(j>0)
					s.concat(" ");
				s.concat(t);
			}
			values.add(s);
		}
	}
	
	public void writeToBuffer(ByteBuffer buff, int position) {
		Character c = null ;
		for(String s : values) {
			for(int i=0; i<s.length();i++) {
				buff.putChar(s.charAt(i));
			}
		}
	}
	
	/**
	 * Retourne la taille du record selon le type
	 * @return
	 */
	public int length() {
		RelDef rd = relDef ;
		int i = 0 ;

		// Verifier si c'est bien un Integer
		if(rd.getTypeCol().get(i).getClass().toString().contains("Integer")) {
			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+4");
			recordLength += 4;
		}
		// Float
		else if(rd.getTypeCol().get(i).getClass().toString().equals("Float")) {
			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+4");
			recordLength += 4;
		}
		// String
		else if(rd.getTypeCol().get(i).getClass().toString().equals("String")) {
			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass() + "+2");
			recordLength += 2;
		}
		else 
			recordLength += 0 ;
		
		
		return recordLength ;
	}

	
}

package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Record2 {
	private RelDef relDef ;				// Relation a laquelle Records appartient
	private List<String> values;		// Valeur de Records
	
	public Record2(RelDef reldef, List<String> values) {
		this.relDef = reldef ;
		values = new ArrayList<>();
	}
	
	public Record2(RelDef reldef) {
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
	
}

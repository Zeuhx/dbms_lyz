package main.java.dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LYZ
 *
 */

public class Record {
	public int recordLength = 0;
	private RelDef relDef; // Relation a laquelle Records appartient
	private List<String> values; // Valeur de Records

	public Record(RelDef reldef, List<String> values) {
		relDef = reldef;
		values = new ArrayList<>();
	}
	
	/**
	 * Methode qui ecrit les valeurs du Records les unes a la suite des autres
	 * Ajoute dans le bytebuffer en fonction des types de col
	 * La boucle permet d'ajouter en fonction des types
	 * 
	 * @param buff
	 * @param position
	 */
	public void writeToBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		int i = 0;
		List<String> list = relDef.getTypeCol();
		for(i=0 ; i<list.size() ; i++) {
			boolean isFloat = false;
			boolean isString = false;
			boolean isInt = false;
			
			if(list.get(i).equals("int")) 
				isInt = true;
			else if(list.get(i).equals("float")) 
				isFloat = true;
			else
				isString = true;
			
			if(isString) {
				int tailleString = list.get(i).length();
				int taille = Integer.parseInt(list.get(i).substring(6));
				/**
				 * Si le string saisie est inferieur a la taille demandé du stringx
				 * On rajoute des espaces a la fin pour avoir la taille x
				 */
				for(int j=0; j<taille; j++) {
					if(j>=tailleString)
						buff.putChar(' ');
					else
						buff.putChar(list.get(i).charAt(j));
				}
			}
			else if(isInt) {
				System.err.println("Erreur X9bis : Affichage number exception : " + list.get(i));
				buff.putInt(Integer.parseInt(list.get(i)));
			}
			else if(isFloat)
				buff.putFloat(Float.parseFloat(list.get(i)));
		}
		
	}

	/**
	 * On prend un bytebuffer a une certaine position
	 * Le record est lie a un relDef donc on connait deja les types des cols
	 * La boucle permet de lire le bytebuffer en fonction des types
	 * et les affiche avec println
	 * @param buff
	 * @param position
	 * 
	 */
	public void readFromBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		List<String> list = relDef.getTypeCol();
		for(int i=0 ; i<list.size() ; i++) {
			if(list.get(i).equals("int")) {
				values.add(i, Integer.toString(buff.getInt(buff.position())));
				buff.position(buff.position()+ Integer.BYTES);
			}
			else if(list.get(i).equals("float")) {
				values.add(i, Float.toString(buff.getFloat(buff.position())));
				buff.position(buff.position()+ Float.BYTES);
			}
			else {
				String taille = list.get(i).substring("string".length());
				int t = Integer.parseInt(taille);
				for(int j = 0; j<t; j++) {
					StringBuilder sb = new StringBuilder();
					sb.append(buff.getChar());
					values.add(sb.toString());
				}
			}
		}
				
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append("[Record] de " + relDef.getNomRelation() + " ");
		for(String s : values) {
			build.append(s);
			build.append(" ; ");
		}
		build.append("\n");
		return build.toString();
	}
	
	public List<String> getValues(){
		return values;
	}

	public RelDef getRelDef() { return relDef; }	
	
	

}

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
		this.values = values;
	}
	
	public Record(RelDef reldef) {
		relDef = reldef;
		values = new ArrayList<>();
	}
	
	public Record(List<String> values) {
		relDef = null ;
		this.values = values;
	}
	
	/**
	 * Methode qui ecrit les valeurs du Records les unes a la suite des autres dans le buffer
	 * Ajoute dans le bytebuffer en fonction des types de col
	 * La boucle permet d'ajouter en fonction des types
	 * 
	 * @param buff un buffer
	 * @param position un entier correspondant � une position dans le buffer
	 */
	public void writeToBuffer(ByteBuffer buff, int position) {
		buff.position(position);
		int i = 0;
		List<String> list = relDef.getTypeCol(); //recupere la liste
		
		//System.out.println("Affichage X151 - Affichage du buffer de writeToBuffer et de sa position normalement : " + buff + " pos : " + position);
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
				 * Si le string saisie est inferieur a la taille demande du stringx
				 * On rajoute des espaces a la fin pour avoir la taille x
				 */
				for(int j=0; j<taille; j++) {
					if(j>=tailleString)
						buff.putChar(' ');
					else {
						buff.putChar(values.get(i).charAt(j));
					}
				}
			}
			else if(isInt) {
				try {
					buff.putInt(Integer.parseInt(values.get(i)));
				} catch(NumberFormatException e1) {
					System.err.println("Attention, vous n'avez pas saisit un chiffre, le programme s'arrete, veuillez relancer le programme");
					System.exit(0);
				}
			}
			else if(isFloat)
				buff.putFloat(Float.parseFloat(values.get(i)));
		}
	}

	/**
	 * On prend un bytebuffer a une certaine position
	 * Le record est lie a un relDef donc on connait deja les types des cols
	 * La boucle permet de lire le bytebuffer en fonction des types
	 * et les affiche avec println
	 * @param buffun buffer
	 * @param position un entier correspondant une position dans le buffer
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
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j<t; j++) {
					sb.append(buff.getChar());
				}
				values.add(sb.toString());
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		for(String s : values) {
			build.append(s);
			build.append(" | ");
		}
		return build.toString();
	}
	
	public List<String> getValues(){
		return values;
	}

	public RelDef getRelDef() { return relDef; }	
	
	

}

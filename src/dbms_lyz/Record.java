package dbms_lyz;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Record {
	private RelDef relDef ;				// Relation a laquelle Records appartient
	private List<String> values;		// Valeur de Records
	
	public Record(RelDef reldef, List<String> values) {
		this.relDef = reldef ;
		values = new ArrayList<>();
	}
	
	public Record(RelDef reldef) {
		this(reldef, null);
	}
	
	/**
	 * Ecrit les valeurs du Record dans le buffer, 
	 * l’une après l’autre, à partir de position
	 * @param buff un buffer
	 * @param position , la position dans le buffer
	 * 
	 * On prend la classe de la case i, on verifie si elle est bien presente
	 * Et on inject caractere par caratere dans le buffer
	 */
	public void writeToBuffer(ByteBuffer buff, int position) {
		RelDef rd = relDef ;
		int i = 0 ;
		Character c = null ;
		position = buff.position() ;
		
		// Verifier si c'est bien un Integer
		if(rd.getTypeCol().get(i).getClass().toString().equals("Integer")) {
			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass());
			
			
			for(int p=0 ; p<"Integer".length() ; p++) {
				c = "Integer".charAt(p);
				buff.position(position).putChar(c);
			}
			/**
			 * A faire : Rajouter un espace
			 */
		}
		// Float
		else if(rd.getTypeCol().get(i).getClass().toString().equals("Float")) {
			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass());
			
			
			for(int p=0 ; p<"Float".length() ; p++) {
				c = "Float".charAt(p);
				buff.position(position).putChar(c);
			}
			/**
			 * A faire : Rajouter un espace
			 */
		}
		// String
		else if(rd.getTypeCol().get(i).getClass().toString().equals("String")) {
			System.out.println("Type de la colone : " + rd.getTypeCol().get(i).getClass());
			
			
			for(int p=0 ; p<"String".length() ; p++) {
				c = "String".charAt(p);
				buff.position(position).putChar(c) ;
			}
			/**
			 * A faire : Rajouter un espace
			 */
		}
	}
	
	public void readFromBuffer(ByteBuffer buff, int position) {
		position = buff.position();
		StringBuilder sb = new StringBuilder("");
		while(position!=0) {
			sb.append(buff.position(position).getChar());
			position-- ;
		}
		sb.reverse();
		System.out.println(sb.toString());
	}
}

package main.java.dbms_lyz;

/**
 * 
 * A finir
 * @author LYZ
 * Class Exception au niveau des flag
 *
 */
public class FlagException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FlagException() {
		super();
		System.out.println("probleme dans flushAll pour la condition si le flag dirty est egal a 1");
	}

}

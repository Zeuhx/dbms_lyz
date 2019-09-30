package dbms_lyz;

public class PageId {
	public static int ID = 0 ;
	
	private int fileIdx;
	private int pageIdx;
	
	public PageId(String nomFichier) {
		fileIdx = IdFichier(nomFichier);
		ID += 1; 
	}
	
	/**
	 * 
	 * @param nomFichier
	 * @return le x de Data_x
	 */
	public int IdFichier(String nomFichier) {
		int id ;
		String s = new String(nomFichier.substring("Data_".length(), nomFichier.indexOf(".rf")));
		id = Integer.parseInt(s);
		return(id);
	}
}

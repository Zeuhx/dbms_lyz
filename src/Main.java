import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


/**
 * Classe Main
 *
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Scanner scan = new Scanner(System.in);
		launch(scan);
		// System.out.println();
	}
	
	public static void launch(Scanner scan) {
		DBManager manager = new DBManager();
		DBManager.getInstance().init();
		String commande = new String("");
		System.out.println("----------- [BASE DE DONNEE - LYZ] -----------");
		System.out.println("Pour afficher la liste des commandes, veuillez saisir : commandes");
		do {
			System.out.print("\nSaisir votre commande : ");
			commande = scan.nextLine();
			if(commande.equals("commandes") || commande.equals("commande")) {
				menu();
			}
			else{
				manager.processCommand(commande);	
			}
		} while(!commande.equals("exit"));
		scan.close();
		System.out.println();
	}
	
	private static void menu() {
		System.out.println(" -------------------------------------------------------------");
		System.out.println("|   clean : suppression des fichiers, vide le catalogue.def   |");
		System.out.println("|   create - ex : create nomRelation nbCol typeCol1 typeCol2  |");
		System.out.println("|   insert - ex : insert nomRelation val1 val2                |");
		System.out.println("|   select - ex : select nomRelation indexCol val             |");
		System.out.println("|   insertAll - ex : insertAll nomRelation fichier.csv        |");
		System.out.println("|   selectAll - ex : selectAll nomRelation                    |");
		System.out.println("|   delete - ex : delete nomRelation indexCol val             |");
		System.out.println("|   join - ex : join nomRel1 nomRel2 indexCol1 indexCol2      | ");
		System.out.println("|   exit : sauvegarde et quitte le programme                  |");
		System.out.println(" -------------------------------------------------------------");
	}


}

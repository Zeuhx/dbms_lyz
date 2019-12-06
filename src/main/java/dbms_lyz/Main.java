package main.java.dbms_lyz;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


/**
 * Classe Main
 *
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		launch();
	}
	
	public static void launch() {
		DBManager manager = new DBManager();
		DBManager.init();
		Scanner scan = new Scanner(System.in);
		String commande = "";
		System.out.println("----- BASE DE DONNEE - LYZ -----");
		do {
			System.out.print("\nSaisir votre commande : ");
			commande = scan.nextLine();
			manager.processCommand(commande);	
		} while(!commande.equals("exit"));
		scan.close();
		System.out.println();
	}


}

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
		Scanner scan = new Scanner(System.in);
		launch(scan);
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
			else if(commande.equals("devoirs") || commande.equals("devoir")) {
				System.out.println("Devoir : \nFinir le init lorsqu'on lance le programme");
				System.out.println("Changer les descriptions des methodes");
				System.out.println("Regler le probleme pour la byteMap lorsque c'est le meme nom de relation");
				System.out.println("Demander si c'est bien comme ca pour le oriented page");
				System.out.println("Commencer l'index");
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

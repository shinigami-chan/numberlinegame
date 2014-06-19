package com.wicam.numberlineweb.server.Letris;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Create the list of target words for the Letris game.
 * @author timfissler
 *
 */

// TODO Check if these words make sense for this kind of game together with Stefanie Jung.

public class LetrisGameWordList {
	
	public static ArrayList<String> createWordList(){
		ArrayList<String> wordList = new ArrayList<String>();
		
		/* Sample words.
		wordList.add("Ball");
		wordList.add("billig");
		wordList.add("Biss");
		wordList.add("bitter");
		wordList.add("Blatt");
		wordList.add("blicken");
		wordList.add("Brett");
		wordList.add("Brille");
		wordList.add("Brücke");
		wordList.add("Brunnen");
		wordList.add("Decke");
		wordList.add("dick");
		wordList.add("Donner");
		wordList.add("Doppelbett");
		wordList.add("doppelt");
		wordList.add("dreckig");
		wordList.add("drucken");
		wordList.add("drücken");
		wordList.add("Dummheit");
		wordList.add("Ecke");
		wordList.add("fallen");
		wordList.add("fällen");
		wordList.add("Fell");
		wordList.add("Flossen");
		wordList.add("füllen");
		wordList.add("glatt");
		wordList.add("Glück");
		wordList.add("Griff");
		wordList.add("Halle");
		wordList.add("Hammer");
		wordList.add("hassen");
		wordList.add("hässlich");
		wordList.add("hell");
		wordList.add("herrlich");
		wordList.add("hoffen");
		wordList.add("Hoffnung");
		wordList.add("Irrtum");
		wordList.add("Kammer");
		wordList.add("Keller");
		wordList.add("kennen");
		wordList.add("Kette");
		wordList.add("kippen");
		wordList.add("klappen");
		wordList.add("klappern");
		wordList.add("klettern");
		wordList.add("Klippe");
		wordList.add("knacken");
		wordList.add("knapp");
		wordList.add("knittern");
		wordList.add("knurren");
		wordList.add("Komma");
		wordList.add("krumm");
		wordList.add("lassen");
		wordList.add("lässig");
		wordList.add("Locke");
		wordList.add("lockig");
		wordList.add("Lücke");
		wordList.add("messen");
		wordList.add("Mitte");
		wordList.add("Mücke");
		wordList.add("nennen");
		wordList.add("Nummer");
		wordList.add("öffnen");
		wordList.add("Päckchen");
		wordList.add("packen");
		wordList.add("passen");
		wordList.add("Pfiff");
		wordList.add("pfiffig");
		wordList.add("Puppentheater");
		wordList.add("Qualle");
		wordList.add("Quelle");
		wordList.add("Rennbahn");
		wordList.add("rennen");
		wordList.add("Riss");
		wordList.add("Ritt");
		wordList.add("rollen");
		wordList.add("Rücksicht");
		wordList.add("sammeln");
		wordList.add("schaffen");
		wordList.add("Schall");
		wordList.add("Schallgeschwindigkeit");
		wordList.add("scharren");
		wordList.add("schicken");
		wordList.add("Schiff");
		wordList.add("Schimmel");
		wordList.add("Schlamm");
		wordList.add("schleppen");
		wordList.add("schlimm");
		wordList.add("schlucken");
		wordList.add("Schluss");
		wordList.add("Schlüssel");
		wordList.add("schmettern");
		wordList.add("schmollen");
		wordList.add("schnappen");
		wordList.add("schnell");
		wordList.add("Schramme");
		wordList.add("Schreck");
		wordList.add("Schuppen");
		wordList.add("schuppig");
		wordList.add("Schuss");
		wordList.add("Schusswaffe");
		wordList.add("schütteln");
		wordList.add("Schwamm");
		wordList.add("schwimmen");
		wordList.add("spannen");
		wordList.add("Sperre");
		wordList.add("Splitter");
		wordList.add("Stamm");
		wordList.add("starr");
		wordList.add("Stecker");
		wordList.add("Stelle");
		wordList.add("stellen");
		wordList.add("still");
		wordList.add("Stimme");
		wordList.add("Stoff");
		wordList.add("stoppen");
		wordList.add("stumm");
		wordList.add("summen");
		wordList.add("treffen");
		wordList.add("trennen");
		wordList.add("Trick");
		wordList.add("Tritt");
		wordList.add("trocken");
		wordList.add("Trockenheit");
		wordList.add("Tunnel");
		wordList.add("voll");
		wordList.add("Wasser");
		wordList.add("wecken");
		wordList.add("Wette");
		wordList.add("Wetter");
		wordList.add("Wille");
		wordList.add("wissen");
		wordList.add("zerren");
		wordList.add("Zucker");
		wordList.add("zwicken");
		wordList.add("Zwillinge");

		
		wordList.add("Ahnung");
		wordList.add("Bahn");
		wordList.add("Blume");
		wordList.add("Bruder");
		wordList.add("Ebene");
		wordList.add("Fehler");
		wordList.add("Flug");
		wordList.add("Frieden");
		wordList.add("Frühling");
		wordList.add("Gas");
		wordList.add("Hose");
		wordList.add("Höhle");
		wordList.add("Kino");
		wordList.add("Kohle");
		wordList.add("Lehrer");
		wordList.add("Liege");
		wordList.add("Löwe");
		wordList.add("Lüge");
		wordList.add("Miete");
		wordList.add("Rede");
		wordList.add("Rose");
		wordList.add("Schal");
		wordList.add("Schnee");
		wordList.add("Sieger");
		wordList.add("Sohn");
		wordList.add("Spiegel");
		wordList.add("Spiel");
		wordList.add("Straße");
		wordList.add("Stuhl");
		wordList.add("Tafel");
		wordList.add("Telefon");
		wordList.add("Vater");
		wordList.add("Vogel");
		wordList.add("Wagen");
		wordList.add("Wahrheit");
		wordList.add("Weg");
		wordList.add("Wiese");
		wordList.add("Wohnung");
		wordList.add("Zahl");
		wordList.add("Zähler");
		wordList.add("Ziel");
		wordList.add("Zug");
		wordList.add("ähnlich");
		wordList.add("beten");
		wordList.add("biegen");
		wordList.add("drehen");
		wordList.add("fahren");
		wordList.add("fehlen");
		wordList.add("fröhlich");
		wordList.add("führen");
		wordList.add("geben");
		wordList.add("leben");
		wordList.add("legen");
		wordList.add("lieben");
		wordList.add("nehmen");
		wordList.add("niedlich");
		wordList.add("ohne");
		wordList.add("sagen");
		wordList.add("schieben");
		wordList.add("schief");
		wordList.add("schließlich");
		wordList.add("schwierig");
		wordList.add("tragen");
		wordList.add("viel");
		wordList.add("wählen");
		wordList.add("während");
		wordList.add("wieder");
		wordList.add("wohnen");
		wordList.add("zahlen");
		wordList.add("zählen");
		wordList.add("zehn");
		wordList.add("ziehen");
		wordList.add("ziemlich");
		//*/
		
		//* Test words.
		// TODO Switch that back.
		wordList.add("LeTris");
		wordList.add("stop");
		wordList.add("Block");
		//*/
		
		Collections.shuffle(wordList);
		
		return wordList;
	}
}

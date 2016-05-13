package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.ir.statistics.Preprocessing;
import cz.zcu.kiv.nlp.ir.statistics.Statistics;

public class App {

	public static void main(String [] args) {

		Statistics stats = new Preprocessing();
		stats.RunStatistics(args[0]);
		
	}
	
}

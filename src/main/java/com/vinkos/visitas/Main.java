package com.vinkos.visitas;

import com.vinkos.visitas.etl.VisitasETL;

//TODO: Send main method from classes to test
//TODO: JavaFX for a graphical test
//TODO: test* the process
//TODO: Write a report
//TODO: logger for all
public class Main {

	public static void main(String[] args) {
		if (args.length == 0) {
			new VisitasETL().process();
		}
		new VisitasETL(args[0]).process();
	}
}

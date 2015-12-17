package com.vinkos.visitas;


import com.vinkos.visitas.etl.VisitasETL;

public class MainApp {

	public static void main(String[] args) {
		VisitasETL process = new VisitasETL();
		process.extract("report_7.txt");//Extract
		process.transform();//Validate
		//Transform (Rules) //format, nulls,
		process.load();//Load (beginTransaction, ..., commit, backup, manage files)
	}
}

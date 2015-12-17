package com.vinkos.visitas.entity;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import javax.persistence.*;

import static com.vinkos.visitas.etl.Validator.formatter;

@Entity
@Table(name = "Visitante")
public class Visitante {

	private Integer id;
	private String email;
	private Date fechaPrimeraVisita;
	private Date fechaUltimaVisita;
	private int visitasTotales;
	private int visitasAnioActual;
	private int visitasMesActual;

	public Visitante() {}

	public Visitante(Map<String, Object> row) {
		try {
			this.email = row.get("email").toString();
			this.fechaPrimeraVisita = formatter.parse(row.get("fechaPrimeraVisita").toString());
			this.fechaUltimaVisita = formatter.parse(row.get("fechaUltimaVisita").toString());
			this.visitasTotales = Integer.parseInt(row.get("visitasTotales").toString());
			this.visitasAnioActual = Integer.parseInt(row.get("visitasAnioActual").toString());
			this.visitasMesActual = Integer.parseInt(row.get("visitasMesActual").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Visitante(String email, Date fechaPrimeraVisita, Date fechaUltimaVisita, int visitasTotales,
			int visitasAnioActual, int visitasMesActual) {
		this.email = email;
		this.fechaPrimeraVisita = fechaPrimeraVisita;
		this.fechaUltimaVisita = fechaUltimaVisita;
		this.visitasTotales = visitasTotales;
		this.visitasAnioActual = visitasAnioActual;
		this.visitasMesActual = visitasMesActual;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=63)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Column
	public Date getFechaPrimeraVisita() {
		return fechaPrimeraVisita;
	}
	public void setFechaPrimeraVisita(Date fechaPrimeraVisita) {
		this.fechaPrimeraVisita = fechaPrimeraVisita;
	}

	@Column
	public Date getFechaUltimaVisita() {
		return fechaUltimaVisita;
	}
	public void setFechaUltimaVisita(Date fechaUltimaVisita) {
		this.fechaUltimaVisita = fechaUltimaVisita;
	}

	@Column
	public int getVisitasTotales() {
		return visitasTotales;
	}
	public void setVisitasTotales(int visitasTotales) {
		this.visitasTotales = visitasTotales;
	}

	@Column
	public int getVisitasAnioActual() {
		return visitasAnioActual;
	}
	public void setVisitasAnioActual(int visitasAnioActual) {
		this.visitasAnioActual = visitasAnioActual;
	}

	@Column
	public int getVisitasMesActual() {
		return visitasMesActual;
	}
	public void setVisitasMesActual(int visitasMesActual) {
		this.visitasMesActual = visitasMesActual;
	}

	@Override
	public String toString() {
		return String.format("(%s,%s,%s,%d,%d,%d)", email, fechaPrimeraVisita, 
				fechaUltimaVisita, visitasTotales, visitasAnioActual, visitasMesActual);
	}
}

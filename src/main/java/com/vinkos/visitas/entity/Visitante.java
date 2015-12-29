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
	private Date primeraVisita;
	private Date ultimaVisita;
	private int visitasTotales;
	private int visitasAnioActual;
	private int visitasMesActual;

	public Visitante() {}

	public Visitante(Map<String, Object> row) {
		try {
			this.id = Integer.parseInt(row.get("id").toString());
			this.email = row.get("email").toString();
			this.primeraVisita = formatter.parse(row.get("primeraVisita").toString());
			this.ultimaVisita = formatter.parse(row.get("ultimaVisita").toString());
			this.visitasTotales = Integer.parseInt(row.get("visitasTotales").toString());
			this.visitasAnioActual = Integer.parseInt(row.get("visitasAnioActual").toString());
			this.visitasMesActual = Integer.parseInt(row.get("visitasMesActual").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Visitante(String email, Date primeraVisita, Date ultimaVisita, int visitasTotales,
			int visitasAnioActual, int visitasMesActual) {
		this.email = email;
		this.primeraVisita = primeraVisita;
		this.ultimaVisita = ultimaVisita;
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

	@Column(length=63, nullable=false)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@Column
	public Date getPrimeraVisita() {
		return primeraVisita;
	}
	public void setPrimeraVisita(Date primeraVisita) {
		this.primeraVisita = primeraVisita;
	}

	@Column
	public Date getUltimaVisita() {
		return ultimaVisita;
	}
	public void setUltimaVisita(Date ultimaVisita) {
		this.ultimaVisita = ultimaVisita;
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
		return String.format("(%s,%s,%s,%d,%d,%d)", email, primeraVisita, 
				ultimaVisita, visitasTotales, visitasAnioActual, visitasMesActual);
	}
}

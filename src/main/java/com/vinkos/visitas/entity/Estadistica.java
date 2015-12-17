package com.vinkos.visitas.entity;

import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

import javax.persistence.*;
import com.vinkos.visitas.etl.Validator;

// TODO: tenemos diferentes formas de escribir Win7, windows 7, windows 7.0, etc, se creará un diccionario/tag? se pueden agrupar?
//Diccionario


@Entity
@Table(name = "Estadistica")
public class Estadistica {

	private Integer id;
	private String email;
	private String jyv;
	private String badMail;
	private String baja;
	private Date fechaEnvio;
	private Date fechaOpen;
	private int opens;
	private int opensVirales;
	private Date fechaClick;
	private int clicks;
	private int clicksVirales;
	private String link;
	private String ip;
	private String navegador;
	private String plataforma;

	public Estadistica() {}

	public Estadistica(String email, String jyv, String badMail, String baja,
			Date fechaEnvio, Date fechaOpen, int opens, int opensVirales, 
			Date fechaClick, int clicks, int clicksVirales, String link,
			String ip, String navegador, String plataforma) {
		this(0, email, jyv, badMail, baja, fechaEnvio, fechaOpen,
				opens, opensVirales, fechaClick, clicks, clicksVirales, link,
				ip, navegador, plataforma);
	}

	public Estadistica(List<String> raw) {
		try {
			this.id = 0;
			this.email = raw.get(0);
			this.jyv = raw.get(1);
			this.badMail = raw.get(2);
			this.baja = raw.get(3);
			this.fechaEnvio = Validator.formatter.parse(raw.get(4));
			this.fechaOpen = Validator.formatter.parse(raw.get(5));
			this.opens = Integer.parseInt(raw.get(6));
			this.opensVirales = Integer.parseInt(raw.get(7));
			this.fechaClick = Validator.formatter.parse(raw.get(8));
			this.clicks = Integer.parseInt(raw.get(9));
			this.clicksVirales = Integer.parseInt(raw.get(10));
			this.link = raw.get(11);
			this.ip = raw.get(12);
			this.navegador = raw.get(13);
			this.plataforma = raw.get(14);
		} catch(ParseException ex) {
			ex.printStackTrace();
		}
	}

	public Estadistica(Integer id, String email, String jyv, String badMail,
			String baja, Date fechaEnvio, Date fechaOpen, int opens, 
			int opensVirales, Date fechaClick, int clicks, int clicksVirales,
			String link, String ip, String navegador, String plataforma) {
		super();
		this.id = id;
		this.email = email;
		this.jyv = jyv;
		this.badMail = badMail;
		this.baja = baja;
		this.fechaEnvio = fechaEnvio;
		this.fechaOpen = fechaOpen;
		this.opens = opens;
		this.opensVirales = opensVirales;
		this.fechaClick = fechaClick;
		this.clicks = clicks;
		this.clicksVirales = clicksVirales;
		this.link = link;
		this.ip = ip;
		this.navegador = navegador;
		this.plataforma = plataforma;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(unique=true, length=63)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(length=63)
	public String getJyv() {
		return jyv;
	}

	public void setJyv(String jyv) {
		this.jyv = jyv;
	}

	@Column(length=63)
	public String getBadMail() {
		return badMail;
	}

	public void setBadMail(String badMail) {
		this.badMail = badMail;
	}

	@Column(length=63)
	public String getBaja() {
		return baja;
	}

	public void setBaja(String baja) {
		this.baja = baja;
	}

	@Column
	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	@Column
	public Date getFechaOpen() {
		return fechaOpen;
	}

	public void setFechaOpen(Date fechaOpen) {
		this.fechaOpen = fechaOpen;
	}

	@Column(length=63)
	public int getOpens() {
		return opens;
	}

	public void setOpens(int opens) {
		this.opens = opens;
	}

	@Column(length=63)
	public int getOpensVirales() {
		return opensVirales;
	}

	public void setOpensVirales(int opensVirales) {
		this.opensVirales = opensVirales;
	}

	@Column
	public Date getFechaClick() {
		return fechaClick;
	}

	public void setFechaClick(Date fechaClick) {
		this.fechaClick = fechaClick;
	}

	@Column(length=63)
	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}

	@Column(length=63)
	public int getClicksVirales() {
		return clicksVirales;
	}

	public void setClicksVirales(int clicksVirales) {
		this.clicksVirales = clicksVirales;
	}

	@Column(length=63)
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Column(length=63)
	public String getIp() {
		return ip;
	}

	@Column(length=128)
	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(length=128)
	public String getNavegador() {
		return navegador;
	}

	public void setNavegador(String navegador) {
		this.navegador = navegador;
	}

	@Column(length=128)
	public String getPlataforma() {
		return plataforma;
	}

	public void setPlataforma(String plataforma) {
		this.plataforma = plataforma;
	}

	@Override
	public String toString() {
		return String.format("(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)", 
				email, jyv, badMail, baja, fechaEnvio, fechaOpen, opens, 
				opensVirales, fechaClick, clicks, clicksVirales, link, ip, 
				navegador, plataforma);
	}
}

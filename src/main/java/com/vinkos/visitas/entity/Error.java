package com.vinkos.visitas.entity;

import java.util.Date;
import javax.persistence.*;

//TODO: categorizar errores
// el manejode errores debería ser mucho más profundo...

@Entity
@Table(name = "Error")
public class Error {

	private Integer id;
	private String registro;
	private String tipoError;
	private Date fecha;

	public Error() {}

	public Error(String registro, String tipoError, Date fecha) {
		this.registro = registro;
		this.tipoError = tipoError;
		this.fecha = fecha;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=255)
	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	@Column(length=255)
	public String getTipoError() {
		return tipoError;
	}

	public void setTipoError(String tipoError) {
		this.tipoError = tipoError;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", registro, tipoError);
	}
}

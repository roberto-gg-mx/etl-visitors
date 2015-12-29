package com.vinkos.visitas.entity;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "Error")
public class Error {

	private Integer id;
	private String registro;
	private Type tipoError;
	private Date fecha;

	public enum Type {
		SOURCE_FILE,
		DATA_VALIDATION,
		STORE_INCONSITENCY,
		BACKUP_CREATION,
		BACKUP_DELETION
	}

	public Error() {}

	public Error(String registro, Type tipoError) {
		this.registro = registro;
		this.tipoError = tipoError;
		this.fecha = new Date();
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=255, nullable=false)
	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	@Column(nullable=false)
	public Type getTipoError() {
		return tipoError;
	}

	public void setTipoError(Type tipoError) {
		this.tipoError = tipoError;
	}

	@Column(nullable=false)
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

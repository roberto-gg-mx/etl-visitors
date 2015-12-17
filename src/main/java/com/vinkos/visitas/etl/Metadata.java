package com.vinkos.visitas.etl;

import java.util.regex.Pattern;

class Metadata {

	private String columnName;
	private Pattern format;
	private boolean mandatory;
	private String defaultValue;
	private boolean multivalue;

	public Metadata() {
		super();
	}

	public Metadata(String columnName, Pattern format, boolean mandatory, 
			String defaultValue, boolean multivalue) {
		super();
		this.columnName = columnName;
		this.format = format;
		this.mandatory = mandatory;
		this.defaultValue = defaultValue;
		this.multivalue = multivalue;
		
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Pattern getFormat() {
		return format;
	}

	public void setFormat(Pattern format) {
		this.format = format;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isMultivalue() {
		return multivalue;
	}

	public void setMultivalue(boolean multivalue) {
		this.multivalue = multivalue;
	}
}

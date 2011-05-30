package com.devforfun.guardian;

import java.util.Date;

public class Indice {

	private float voz;

	private float dados;
	
	private Date data;

	public Indice(float voz, float dados, Date data) {
		super();
		this.voz = voz;
		this.dados = dados;
		this.data = data;
	}

	public float getVoz() {
		return voz;
	}

	public void setVoz(float voz) {
		this.voz = voz;
	}

	public float getDados() {
		return dados;
	}

	public void setDados(float dados) {
		this.dados = dados;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

}

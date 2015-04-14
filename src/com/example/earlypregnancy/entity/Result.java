package com.example.earlypregnancy.entity;

import java.io.Serializable;

public class Result implements Serializable {

	private int id;
	private String name;
	private int model;
	private long time;
	private int id_model;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getId_model() {
		return id_model;
	}

	public void setId_model(int id_model) {
		this.id_model = id_model;
	}

}

package com.wicam.numberlineweb.client.NumberLineGame;

import java.io.Serializable;

public class NumberLineGamePlayer implements Serializable, Comparable<NumberLineGamePlayer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2437389490131688801L;
	private String name = "";
	private int points = 0;
	private boolean clicked = false;
	private int actPos = Integer.MIN_VALUE;
	private boolean ready = false; // for synchronization
	private boolean leftGame = false;
	private int colorId = 0;
	
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	

	
	public int getPoints() {
		return points;
	}
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}
	public boolean isClicked() {
		return clicked;
	}
	public void setActPos(int actPos) {
		this.actPos = actPos;
	}
	public int getActPos() {
		return actPos;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public boolean isReady() {
		return ready;
	}
	public void setLeftGame(boolean leftGame) {
		this.leftGame = leftGame;
	}
	public boolean hasLeftGame() {
		return leftGame;
	}
	@Override
	public int compareTo(NumberLineGamePlayer p) {
		return p.points -this.points;
	}
	public void setColorId(int colorId) {
		this.colorId = colorId;
	}
	public int getColorId() {
		return colorId;
	}
	
}
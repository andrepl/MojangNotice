package com.norcode.bukkit.mojangnotice;

import org.json.simple.JSONObject;

public class StatusResponse {
	public double login = 0;
	double session = 0;
	public double skins = 0;
	public double website = 0;
	public double login_h = 0;
	public double session_h = 0;
	public double skins_h = 0;
	public double website_h = 0;
	
	public StatusResponse(JSONObject obj) {
		this.login = Double.parseDouble((String)obj.get("login"));
		this.session = Double.parseDouble((String)obj.get("session"));
		this.skins = Double.parseDouble((String)obj.get("skins"));
		this.website = Double.parseDouble((String)obj.get("website"));
		
		this.login_h = Double.parseDouble((String)obj.get("login_h"));
		this.session_h = Double.parseDouble((String)obj.get("session_h"));
		this.skins_h = Double.parseDouble((String)obj.get("skins_h"));
		this.website_h = Double.parseDouble((String)obj.get("website_h"));
	}

	public double getLogin() {
		return login;
	}

	public boolean getLoginUp() {
		return login == 0;
	}
	
	public boolean getSessionUp() {
		return session == 0;
	}
	
	public void setLogin(double login) {
		this.login = login;
	}

	public double getSession() {
		return session;
	}

	public void setSession(double session) {
		this.session = session;
	}

	public double getSkins() {
		return skins;
	}

	public void setSkins(double skins) {
		this.skins = skins;
	}

	public double getWebsite() {
		return website;
	}

	public void setWebsite(double website) {
		this.website = website;
	}

	public double getLoginH() {
		return login_h;
	}

	public void setLoginH(double login_h) {
		this.login_h = login_h;
	}

	public double getSessionH() {
		return session_h;
	}

	public void setSessionH(double session_h) {
		this.session_h = session_h;
	}

	public double getSkinsH() {
		return skins_h;
	}

	public void setSkinsH(double skins_h) {
		this.skins_h = skins_h;
	}

	public double getWebsiteH() {
		return website_h;
	}

	public void setWebsiteH(double webdsite_h) {
		this.website_h = webdsite_h;
	}


}

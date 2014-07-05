package com.webmons.shared.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable(aliasOf=PlayerMarshallable.class)
@Entity
@NamedQuery(name = "Player.findByName", query = "SELECT u FROM Player u WHERE u.username = :name")
public class Player extends PlayerMarshallable{

	@OneToMany(mappedBy = "player", fetch=FetchType.LAZY)
	private Set<Webbi> webbis;

	public Player() {
		super();
	}

	public Player(String username, String email, String password, Home home) {
		super(username, email, password, home);
	}
	
	public Set<Webbi> getWebbis() {
		if (webbis == null) {
			webbis = new HashSet<Webbi>();
		}
		return webbis;
	}
	public void addWebbi(Webbi webbi) {
		getWebbis().add(webbi);
		if (webbi.getPlayer() != this) {
			webbi.setPlayer(this);
		}
	}
	public void setWebbis(Set<Webbi> webbis) {
		this.webbis= webbis;
	}
}

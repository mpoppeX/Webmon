package com.webmons.shared.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
@Entity
@Table(name = "webbis")
@NamedQuery(name = "Webbi.findByPlayer", query = "SELECT w FROM Webbi w JOIN w.player as p WHERE p.id = :playerId")
public class Webbi extends DomainObject {

	@Column(name = "speed")
	private int speed = 5;
	@Column(name = "viewDistance")
	private int viewDistance = 500;// meters
	@Column(name = "isMoving")
	private boolean isMoving;
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "position_id", nullable = false)
	private GeoPoint position;
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "target_id")
	private GeoPoint target;
	@ManyToOne()
	private Player player;

	public Webbi() {
		super();
	}

	public Webbi(GeoPoint position) {
		super();
		this.position = position;
	}

	public GeoPoint getPosition() {
		return position;
	}

	public void setPosition(GeoPoint position) {
		this.position = position;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getViewDistance() {
		return viewDistance;
	}

	public void setViewDistance(int viewDistance) {
		this.viewDistance = viewDistance;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public GeoPoint getTarget() {
		return target;
	}

	public void setTarget(GeoPoint goal) {
		this.target = goal;
		this.isMoving = true;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (!player.getWebbis().contains(this)) {
			player.getWebbis().add(this);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Webbi other = (Webbi) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

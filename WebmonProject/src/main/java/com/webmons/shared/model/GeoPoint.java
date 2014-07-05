package com.webmons.shared.model;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
@Entity
@Table(name = "geopoints")
public class GeoPoint implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;
	
	@Column(name = "positionLatitude")//,nullable=false)
	private double positionLatitude;
	@Column(name = "positionLongitude")//, nullable = false)
	private double positionLongitude;
	
	public GeoPoint() {
		super();
	}
	public GeoPoint(double positionLatitude, double positionLongitude) {
		super();
		//TODO set overflow if latlng more than borders
		this.positionLatitude = positionLatitude;
		this.positionLongitude = positionLongitude;
	}
	public double getPositionLatitude() {
		return positionLatitude;
	}
	public void setPositionLatitude(double positionLatitude) {
		while(positionLatitude>90) positionLatitude=positionLatitude-180;
		while(positionLatitude<-90) positionLatitude= positionLatitude+180;
		this.positionLatitude = positionLatitude;
	}
	public double getPositionLongitude() {
		return positionLongitude;
	}
	public void setPositionLongitude(double positionLongitude) {
		while(positionLongitude>180) positionLongitude=positionLongitude-360;
		while(positionLongitude<-180) positionLongitude= positionLongitude+360;
		this.positionLongitude = positionLongitude;
	}
 
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoPoint other = (GeoPoint) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Lat=").append(getPositionLatitude()).append(", ");
		sb.append("Lng=").append(getPositionLongitude()).append(", ");
		return sb.toString();
	}
}
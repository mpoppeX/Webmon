package com.webmons.shared.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
@Entity
@Table(name = "domains")
public class DomainMarshallable {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;
	
	@Column(name ="isActive")
	private Boolean isActive;
	@OneToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name = "sw_id")
	private GeoPoint sw;
	@OneToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name = "ne_id")
	private GeoPoint ne;
	
	public DomainMarshallable() {
		super();
		this.isActive=false;
	}
	public DomainMarshallable(GeoPoint sw, GeoPoint ne) {
		super();
		this.sw = sw;
		this.ne = ne;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public GeoPoint getSw() {
		return sw;
	}
	public void setSw(GeoPoint sw) {
		this.sw = sw;
	}
	public GeoPoint getNe() {
		return ne;
	}
	public void setNe(GeoPoint ne) {
		this.ne = ne;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(getId()).append(", ");
		sb.append("version=").append(getVersion()).append(", ");
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainMarshallable other = (DomainMarshallable) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
}

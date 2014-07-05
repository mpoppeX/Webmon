package com.webmons.shared.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable(aliasOf = DomainMarshallable.class)
@Entity
@NamedQuery(name = "Domain.findByPosition", query = "SELECT da FROM Domain da JOIN da.sw as sw JOIN da.ne as ne"
		+ " WHERE :longitude1 < ne.positionLongitude AND :longitude2 >= sw.positionLongitude"
		+ " AND :latitude1 < ne.positionLatitude AND :latitude2 >= sw.positionLatitude"
		+ " AND da.isActive = true")
public class Domain extends DomainMarshallable {

	@OneToMany(mappedBy = "topDomain", fetch = FetchType.LAZY)
	private Set<Domain> subDomains;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topDomain_id")
	private Domain topDomain;

	@OneToMany(mappedBy = "domain", fetch = FetchType.LAZY)
	private Set<DomainObject> domainObjects;

	public Domain() {
		super();
	}

	public Domain(GeoPoint sw, GeoPoint ne, Domain topDomainArea) {
		super(sw, ne);
		this.setTopDomain(topDomainArea);

	}

	public Domain getTopDomain() {
		return topDomain;
	}

	public void setTopDomain(Domain topDomain) {
		this.topDomain = topDomain;
		if (topDomain != null && !topDomain.getSubDomains().contains(this)) {
			topDomain.getSubDomains().add(this);
		}
	}

	public Set<Domain> getSubDomains() {
		if (subDomains == null) {
			subDomains = new HashSet<Domain>();
		}
		return subDomains;
	}

	public void addSubDomain(Domain subDomain) {
		getSubDomains().add(subDomain);
		if (subDomain.getTopDomain() != this) {
			subDomain.setTopDomain(this);
		}
	}

	public void setSubDomains(Set<Domain> subDomains) {
		this.subDomains = subDomains;
	}

	public Set<DomainObject> getDomainObjects() {
		if (domainObjects == null) {
			domainObjects = new HashSet<DomainObject>();
		}
		return domainObjects;
	}

	public void addDomainObject(DomainObject domainObject) {
		getDomainObjects().add(domainObject);
		if (domainObject.getDomain() != this) {
			domainObject.setDomain(this);
		}
	}

	public void setDomainObjects(Set<DomainObject> domainObjects) {
		this.domainObjects = domainObjects;
	}

}

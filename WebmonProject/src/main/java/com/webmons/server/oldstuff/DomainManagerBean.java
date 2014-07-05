package com.webmons.server.oldstuff;

import javax.ejb.AccessTimeout;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.webmons.shared.ReregisterEvent;
import com.webmons.shared.model.Domain;
import com.webmons.shared.model.GeoPoint;
import com.webmons.shared.model.Home;

/**
 * Session Bean implementation class DomainManager
 */
@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class DomainManagerBean {

	@Inject
	private Event<ReregisterEvent> reregisterEvent;

	/**
	 * A JPA EntityManager which is configured according to the
	 * {@code WebmonProject-persistence-unit} persistence context defined in
	 * {@code /META-INF/persistence.xml}. Note that this field is not
	 * initialized by the application: it is injected by the EJB container.
	 */
	@PersistenceContext(unitName = "WebmonProject-persistence-unit")
	private EntityManager em;

	private static int _MAX_DOMAINUSER = 1;
	private static int _MIN_DOMAINUSER = 1;

	/**
	 * Splits and persists a DomainArea with less than 4 Domains, cleans
	 * beforehand remaining DomainArea artifacs.
	 * 
	 * @param domainArea
	 *            The DomainArea to be splitted.
	 */
	public void splitDomain(Domain domainArea) {
		if (domainArea.getSubDomains().size() < 4) {
			// this domainArea has not yet subdomains
			// remove old ones
			for (Domain subDomain : domainArea.getSubDomains()) {
//				this.delete(subDomain.getId());
				
				// TODO check if domainArea has NO subdomains any more after
				// this loop
			}
			domainArea.setSubDomains(null);
			// create new ones
			// create sw subdomain
			double swsdneLat = (domainArea.getNe().getPositionLatitude() - domainArea
					.getSw().getPositionLatitude())
					/ 2
					+ domainArea.getSw().getPositionLatitude();
			double swsdneLng = (domainArea.getNe().getPositionLongitude() - domainArea
					.getSw().getPositionLongitude())
					/ 2
					+ domainArea.getSw().getPositionLongitude();
			Domain sw = this.createDomainArea(domainArea.getSw(),
					new GeoPoint(swsdneLat, swsdneLng), domainArea);
			// create nw subdomain
			double nwsdswLat = swsdneLat;
			double nwsdswLng = domainArea.getSw().getPositionLongitude();
			double nwsdneLat = domainArea.getNe().getPositionLatitude();
			double nwsdneLng = swsdneLng;
			Domain nw = this.createDomainArea(new GeoPoint(nwsdswLat,
					nwsdswLng), new GeoPoint(nwsdneLat, nwsdneLng), domainArea);

			// create se subdomain
			double sesdswLat = domainArea.getSw().getPositionLatitude();
			double sesdswLng = swsdneLng;
			double sesdneLat = swsdneLat;
			double sesdneLng = domainArea.getNe().getPositionLongitude();
			Domain se = this.createDomainArea(new GeoPoint(sesdswLat,
					sesdswLng), new GeoPoint(sesdneLat, sesdneLng), domainArea);

			// create ne subdomain
			double nesdswLat = swsdneLat;
			double nesdswLng = swsdneLng;
			Domain ne = this.createDomainArea(new GeoPoint(nesdswLat,
					nesdswLng), domainArea.getNe(), domainArea);

			// now set the new active states
			sw.setIsActive(true);
			nw.setIsActive(true);
			se.setIsActive(true);
			ne.setIsActive(true);
			domainArea.setIsActive(false);
			em.flush();
//			this.persist(domainArea);
		}
	}

	/**
	 * Creates a new Domain and persists it. The returned DomainArea is managed
	 * then.
	 * 
	 * @param sw
	 *            The southwest border.
	 * @param ne
	 *            The northeast border.
	 * @param topDomainArea
	 * @return
	 */
	private Domain createDomainArea(GeoPoint sw, GeoPoint ne,
			Domain topDomainArea) {
		Domain newDomainArea = new Domain(sw, ne, topDomainArea);
//		this.persist(newDomainArea);
		return newDomainArea;
	}

//	@Schedule(second = "*/1", minute = "*", hour = "*", persistent = false)
//	public void splitDomains() {
//		System.out.println("splitDomains");
//		Query q = em
//				.createQuery(
//						"SELECT da FROM DomainArea da WHERE SIZE(da.users) > :maxUsers",
//						DomainArea.class).setParameter("maxUsers",
//						_MAX_DOMAINUSER);
//		// .createNamedQuery("DomainArea.getDomainAreasWithSubDomainCount",
//		// DomainArea.class)
//		// .setParameter("maxUsers",_MAX_DOMAINUSER);
//		List<DomainArea> domainAreas = null;
//		domainAreas = q.getResultList();
//		for (DomainArea domainArea : domainAreas) {
//			// split Domains and re-register all users
//			splitDomain(domainArea);
//			Set<Long> userIds = new HashSet<Long>();
//			for (User us : domainArea.getUsers()) {
//				userIds.add(us.getId());
//			}
//			reregisterEvent.fire(new ReregisterEvent(userIds));
//		}
//	}

	public void mergeDomains() {
		//
		
	}

//	@Schedule(second = "*/30", minute = "*", hour = "*", persistent = false)
//	public void cleanDomains() {
//		// select domain where isactive=false and count(user)>0
//		//these are "lost" users because of register bug
//		//first registered, then view initialized
//		//TODO register user, set view, then activate user
//		Query q = em
//				.createQuery(
//						"SELECT da FROM DomainArea da WHERE SIZE(da.users) > 0 AND da.isActive=false",
//						DomainArea.class);
//		List<DomainArea> domainAreas = null;
//		domainAreas = q.getResultList();
//		for (DomainArea domainArea : domainAreas) {
//			Set<Long> userIds = new HashSet<Long>();
//			for (User us : domainArea.getUsers()) {
//				userIds.add(us.getId());
//			}
//				reregisterEvent.fire(new ReregisterEvent(userIds));
//		}
//	}


	@AccessTimeout(value = 2000)
	public Domain getDomain(Home home) {
		// TODO handle ConcurrentAccessTimeoutException if too many clients
		
//		user = em.find(User.class, user.getId());
		GeoPoint position = home.getPosition();
		Query q = em
				.createQuery(
						"SELECT da FROM Domain da JOIN da.sw as sw JOIN da.ne as ne"
								+ " WHERE SIZE(da.subDomains)=0"
								+ " AND sw.positionLatitude <= :latitude AND sw.positionLongitude <= :longitude"
								+ " AND ne.positionLatitude > :latitude AND ne.positionLongitude > :longitude")
				.setParameter("latitude", position.getPositionLatitude())
				.setParameter("longitude", position.getPositionLongitude());
		// q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		Domain domain = null;
		try {
			domain = (Domain) q.getSingleResult();
		} catch (NoResultException e) {
			// TODO maybe put in some init method for server?!
			domain = new Domain(new GeoPoint(-90, -180),
					new GeoPoint(90, 180), null);

		} catch (Exception e) {
			System.out.println("Unhandled exception registerUser: " + e);
		}
		domain.setIsActive(true);
		domain.addDomainObject(home);
		em.persist(domain);
		em.flush();
		return domain;
	}
}

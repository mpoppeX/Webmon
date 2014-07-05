package com.webmons.server.oldstuff;

import java.util.List;

import javax.ejb.AccessTimeout;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.jpa.sync.client.shared.DataSyncService;
import org.jboss.errai.jpa.sync.client.shared.JpaAttributeAccessor;
import org.jboss.errai.jpa.sync.client.shared.SyncRequestOperation;
import org.jboss.errai.jpa.sync.client.shared.SyncResponse;
import org.jboss.errai.jpa.sync.client.shared.SyncableDataSet;
import org.jboss.errai.jpa.sync.server.JavaReflectionAttributeAccessor;
import org.mindrot.jbcrypt.BCrypt;

import com.webmons.shared.model.Domain;
import com.webmons.shared.model.GeoPoint;
import com.webmons.shared.model.Player;

@Service
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class UserManagerBean {

	/**
	 * A JPA EntityManager which is configured according to the
	 * {@code WebmonProject-persistence-unit} persistence context defined in
	 * {@code /META-INF/persistence.xml}. Note that this field is not
	 * initialized by the application: it is injected by the EJB container.
	 */
	@PersistenceContext(unitName = "WebmonProject-persistence-unit")
	private EntityManager em;

	@AccessTimeout(value = 2000)
	public Domain getDomain(Player user) {
		// TODO handle ConcurrentAccessTimeoutException if too many clients
		
//		user = em.find(User.class, user.getId());
		GeoPoint position = user.getHome().getPosition();
		Query q = em
				.createQuery(
						"SELECT da FROM DomainArea da JOIN da.sw as sw JOIN da.ne as ne"
								+ " WHERE SIZE(da.subDomains)=0"
								+ " AND sw.positionLatitude <= :latitude AND sw.positionLongitude <= :longitude"
								+ " AND ne.positionLatitude > :latitude AND ne.positionLongitude > :longitude")
				.setParameter("latitude", position.getPositionLatitude())
				.setParameter("longitude", position.getPositionLongitude());
		// q.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		Domain domainArea = null;
		try {
			domainArea = (Domain) q.getSingleResult();
		} catch (NoResultException e) {
			// TODO maybe put in some init method for server?!
			domainArea = new Domain(new GeoPoint(-90, -180),
					new GeoPoint(90, 180), null);
			domainArea.setIsActive(true);
			em.persist(domainArea);
			em.flush();
		} catch (Exception e) {
			System.out.println("Unhandled exception registerUser: " + e);
		}
		return domainArea;
	}
	
	/**
	 * Search for the username in database and compare password with encrypted
	 * password in database. If the passwords match, the user state is set to
	 * isLoggedIn=true, persisted and returned.
	 * 
	 * @param username
	 *            The username to search for as a unique database entry.
	 * @param password
	 *            The unencrypted password to match with database.
	 * @param sessionId 
	 * @return
	 */
//	public User loginUser(String username, String password, String sessionId) {
//
//		Query q = em.createNamedQuery("User.findByName", User.class)
//				.setParameter("name", username);
//		User user = null;
//		try {
//			user = (User) q.getSingleResult();
//		} catch (NoResultException e) {
//			// TODO implement logger in all classes
//			// logger.info("No DomainArea found for position: "
//			// + position.toString());
//			e.addSuppressed(new EntityNotFoundException("User " + username
//					+ " not found in database."));
//		}
//		// decrypt password
//		boolean valid = BCrypt.checkpw(password, user.getPassword());
//		if (valid) {
//			user.setIsLoggedIn(true);
//			user.setSessionId(sessionId);
//			user.setDomainArea(this.getDomain(user));
//			this.persist(user);
//		} else {
//			throw new EntityNotFoundException("Password not matching");
//		}	    
//		return user;
//	}
	public Player loginUser(String username, String password, String sessionId) {

		Query q = em.createNamedQuery("Player.findByName", Player.class)
				.setParameter("name", username);
		Player player = null;
		try {
			player = (Player) q.getSingleResult();
		} catch (NoResultException e) {
			// TODO implement logger in all classes
			// logger.info("No DomainArea found for position: "
			// + position.toString());
			e.addSuppressed(new EntityNotFoundException("Player " + username
					+ " not found in database."));
		}
		// decrypt password
		boolean valid = BCrypt.checkpw(password, player.getPassword());
		if (valid) {
			player.setIsLoggedIn(true);
			player.setSessionId(sessionId);
			this.persist(player);
		} else {
			throw new EntityNotFoundException("Password not matching");
		}	    
		return player;
	}
	/**
	 * Persists the state of the given User in the database. If the User is not
	 * yet persisted, an id is created, else the User is merged
	 * 
	 * @param id
	 *            The unique identifier for the given User.
	 * @param entity
	 *            The User to update the database with.
	 */
	public Player persist(Player entity) {
		em.persist(entity);
		return entity;
	}
	/**
	 * Updates the state of the given User in the database.
	 * 
	 * @param id
	 *            The unique identifier for the given User.
	 * @param entity
	 *            The User to update the database with.
	 */
	public void update(Long id, Player entity) {
		entity.setId(id);
		entity = em.merge(entity);
	}

	/**
	 * Removes the User with the given ID from the database.
	 * 
	 * @param id
	 *            The unique ID of the User to delete. Must not be null.
	 * @throws IllegalArgumentException
	 *             if {@code id} is null, or if there is no User with that ID in
	 *             the database.
	 */
	public void delete(Long id) {
		Player entity = em.find(Player.class, id);
		em.remove(entity);
	}

	/**
	 * Returns the User with the given unique ID.
	 * 
	 * @return The User with the given unique ID, or null if there is no such
	 *         User in the database.
	 * @throws IllegalArgumentException
	 *             if {@code id} is null.
	 */
	public Player getById(Long id) {
		return em.find(Player.class, id);
	}

	/**
	 * Passes a data sync operation on the given data set to the server-side of
	 * the Errai DataSync system.
	 * <p>
	 * This method is not invoked directly by the application code; it is called
	 * via Errai RPC by Errai's ClientSyncManager.
	 * 
	 * @param user
	 * 
	 * @param dataSet
	 *            The data set to synchronize.
	 * @param remoteResults
	 *            The remote results produced by ClientSyncManager, which the
	 *            server-side needs to perform to synchronize the server data
	 *            with the client data.
	 * @return A list of sync response operations that ClientSyncManager needs
	 *         to perform to synchronize the client data with the server data.
	 */
	public <X> List<SyncResponse<X>> sync(SyncableDataSet<X> dataSet,
			List<SyncRequestOperation<X>> remoteResults) {
		JpaAttributeAccessor attributeAccessor = new JavaReflectionAttributeAccessor();
		DataSyncService dss = new org.jboss.errai.jpa.sync.server.DataSyncServiceImpl(
				em, attributeAccessor);
		return dss.coldSync(dataSet, remoteResults);
	}

	public void logoutUser(Player entity) {
		entity = getById(entity.getId());
		entity.setIsLoggedIn(false);
		this.persist(entity);
		// TODO ? em.detach(entity);
	}

	public Player create(Player entity) {
		entity.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt()));
		return this.persist(entity);
	}
}

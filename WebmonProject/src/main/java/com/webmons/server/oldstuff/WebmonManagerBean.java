package com.webmons.server.oldstuff;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.RequestDispatcher;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.jpa.sync.client.shared.DataSyncService;
import org.jboss.errai.jpa.sync.client.shared.JpaAttributeAccessor;
import org.jboss.errai.jpa.sync.client.shared.SyncRequestOperation;
import org.jboss.errai.jpa.sync.client.shared.SyncResponse;
import org.jboss.errai.jpa.sync.client.shared.SyncableDataSet;
import org.jboss.errai.jpa.sync.server.JavaReflectionAttributeAccessor;

import com.webmons.shared.model.Domain;
import com.webmons.shared.model.GeoPoint;
import com.webmons.shared.model.Player;
import com.webmons.shared.model.Webbi;

@Service
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WebmonManagerBean {

	/**
	 * A JPA EntityManager which is configured according to the
	 * {@code WebmonProject-persistence-unit} persistence context defined in
	 * {@code /META-INF/persistence.xml}. Note that this field is not
	 * initialized by the application: it is injected by the EJB container.
	 */
	@PersistenceContext(unitName = "WebmonProject-persistence-unit")
	private EntityManager em;
	@Inject
	private RequestDispatcher dispatcher;

	// @Schedule(second = "*/2", minute = "*", hour = "*", persistent = false)
	// public void moveWebmons() {
	// Query q =
	// em.createQuery("SELECT wm FROM Webmon wm WHERE wm.isMoving = true",
	// Webmon.class);
	// List<Webmon> webmons = null;
	// webmons = q.getResultList();
	// for (Webmon webmon : webmons) {
	//
	// double longdiff = webmon.getTarget().getPositionLongitude()
	// - webmon.getPosition().getPositionLongitude();
	// double latdiff = webmon.getTarget().getPositionLatitude()
	// -webmon.getPosition().getPositionLatitude();
	// double distance = Math.sqrt(Math.pow(longdiff, 2)
	// * Math.pow(latdiff, 2));
	// GeoPoint newPosition;
	// if (webmon.getSpeed() < distance) {
	// double deltaLong = longdiff * webmon.getSpeed() / distance;
	// double deltaLat = latdiff * webmon.getSpeed() / distance;
	// newPosition = new GeoPoint(webmon.getPosition()
	// .getPositionLatitude() + deltaLat, webmon
	// .getPosition().getPositionLongitude() + deltaLong);
	//
	// } else {
	// newPosition = new GeoPoint(webmon.getTarget()
	// .getPositionLatitude(), webmon.getTarget()
	// .getPositionLongitude());
	// webmon.setMoving(false);
	// }
	// webmon.setPosition(newPosition);
	// this.update(webmon);
	// motionEvent.fire(new MotionEvent(webmon));
	// }
	// }




	/**
	 * Passes a data sync operation on the given data set to the server-side of
	 * the Errai DataSync system.
	 * <p>
	 * This method is not invoked directly by the application code; it is called
	 * via Errai RPC by Errai's ClientSyncManager.
	 * 
	 * @param Webmon
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

}

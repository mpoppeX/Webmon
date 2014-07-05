package com.webmons.server;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.jpa.sync.client.shared.DataSyncService;
import org.jboss.errai.jpa.sync.client.shared.SyncRequestOperation;
import org.jboss.errai.jpa.sync.client.shared.SyncResponse;
import org.jboss.errai.jpa.sync.client.shared.SyncableDataSet;

import com.webmons.shared.model.DomainMarshallable;

/**
 * An Errai RPC service which is called by the client when it wishes to
 * synchronize the DomainArea data between itself and the server.
 */
@ApplicationScoped
@Service
public class DataSyncServiceImpl implements DataSyncService {

	/**
	 * An EJB responsible for getting the JPA EntityManager and for transaction
	 * demarcation.
	 */
	// @Inject
	// private UserComplaintService userComplaintService;

	/**
	 * A CDI event source that fires UserComplaint instances to observers, both
	 * on the server and on clients.
	 */
	@Inject
	private Event<DomainMarshallable> updated;
	@PersistenceContext
	private EntityManager em;

	@Override
	public <X> List<SyncResponse<X>> coldSync(SyncableDataSet<X> dataSet,
			List<SyncRequestOperation<X>> remoteResults) {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	// public <X> List<SyncResponse<X>> coldSync(SyncableDataSet<X> dataSet,
	// List<SyncRequestOperation<X>> remoteResults) {
	// //   // Ensure a user is logged in
	// //     User currentUser = loginService.whoAmI();
	// //     if (currentUser == null) {
	// //       throw new IllegalStateException("Nobody is logged in!");
	// //     }
	// //  
	// //     // Ensure user is accessing their own data!
	// //     if (dataSet.getQueryName().equals("groceryListsForUser")) {
	// //       User requestedUser = (User) dataSet.getParameters().get("user");
	// //       if (!currentUser.getId().equals(requestedUser.getId())) {
	// //         throw new
	// AccessDeniedException("You don't have permission to sync user " +
	// requestedUser.getId());
	// //       }
	// //     }
	// //     else {
	// //       throw new
	// IllegalArgumentException("You don't have permission to sync dataset " +
	// dataSet.getQueryName());
	// //     }
	// //  
	// //     DataSyncService dss = new
	// org.jboss.errai.jpa.sync.server.DataSyncServiceImpl(em,
	// attributeAccessor);
	// //     return dss.coldSync(dataSet, remoteResults);
	// //   }
	//
	// // First, let the UserComplaintService EJB do the sync (it gets the
	// correct
	// // EntityManager and also handles transactions)
	// List<SyncResponse<X>> response = userComplaintService.sync(dataSet,
	// remoteResults);
	//
	// // Now fire a CDI event for each UserComplaint which was updated (from
	// client 1) as a result
	// // of this sync
	// for (SyncResponse<X> syncRequestResponse : response) {
	// if (syncRequestResponse instanceof UpdateResponse) {
	// DomainArea newComplaint = (DomainArea) ((UpdateResponse<?>)
	// syncRequestResponse).getEntity();
	// updated.fire(newComplaint);
	// }
	// else if (syncRequestResponse instanceof ConflictResponse) {
	// DomainArea newComplaint = (DomainArea) ((ConflictResponse<?>)
	// syncRequestResponse).getActualNew();
	// updated.fire(newComplaint);
	// }
	// }
	//
	// // Finally, return the list of sync responses to the client, whose sync
	// // manager will update the client database
	// return response;
	// }
}
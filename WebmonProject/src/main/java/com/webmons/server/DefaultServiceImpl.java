package com.webmons.server;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;

import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.RequestDispatcher;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.RpcContext;
import org.mindrot.jbcrypt.BCrypt;

import com.webmons.shared.DefaultService;
import com.webmons.shared.model.Domain;
import com.webmons.shared.model.GeoPoint;
import com.webmons.shared.model.Home;
import com.webmons.shared.model.Player;
import com.webmons.shared.model.Webbi;

@Service
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class DefaultServiceImpl implements DefaultService {


	@Inject
	private CrudService csb;

	@Inject
	private RequestDispatcher dispatcher;


	// private void loginEventObserver(@Observes LoginEvent event) {
	// // this Event is send to requesting Client only
	// User user = umb.loginUser(event.getUsername(), event.getPassword());
	// // userEvent.fire(user);
	// }

	@Override
	public Player create(Player player) {
		player.setPassword(BCrypt.hashpw(player.getPassword(), BCrypt.gensalt()));
		return csb.create(player);// umb.create(player);
	}

	@Override
	public Player login(String username, String password) {

		Player player = (Player) ((List<Object>) csb.findWithNamedQuery(
				"Player.findByName", QueryParameter.with("name", username)
						.parameters(), 1)).get(0);

		// decrypt password
		boolean valid = BCrypt.checkpw(password, player.getPassword());
		if (valid) {
			HttpSession session = RpcContext.getHttpSession();
			player.setIsLoggedIn(true);
			player.setSessionId(session.getId());
			csb.update(player);
		} else {
			throw new EntityNotFoundException("Password not matching");
		}
		return player;
	}

	@Override
	public Domain getDomain(Home home) {
		GeoPoint position = home.getPosition();
		// TODO find a proper jpql for integration as namedquery in EJB Domain
		String query = "SELECT da FROM Domain da JOIN da.sw as sw JOIN da.ne as ne"
				+ " WHERE SIZE(da.subDomains)=0"
				+ " AND sw.positionLatitude <= :latitude AND sw.positionLongitude <= :longitude"
				+ " AND ne.positionLatitude > :latitude AND ne.positionLongitude > :longitude";

		Domain domain = null;
		try {
			domain = (Domain) ((List<Object>) csb.findWithQuery(
					query,
					QueryParameter
							.with("latitude", position.getPositionLatitude())
							.and("longitude", position.getPositionLongitude())
							.parameters(), 1)).get(0);

		} catch (IndexOutOfBoundsException e) {
			// TODO maybe put in some init method for server?!
			domain = new Domain(new GeoPoint(-90, -180), new GeoPoint(90, 180),
					null);

		} catch (Exception e) {
			System.out.println("Unhandled exception registerUser: " + e);
		}
		domain.setIsActive(true);

		// add home to domain
		domain.addDomainObject(home);

		csb.update(domain);
		return domain;

		// return dmb.getDomain(home);
	}

	@Override
	public void addWebmonToPlayer(Long playerId, Webbi webmon) {
		Player playerN = csb.find(Player.class, playerId);
		webmon.setPlayer(playerN);
		csb.update(webmon);

		MessageBuilder.createMessage()
				.toSubject("Player" + playerN.getSessionId()) // (1)
				.signalling() // (2)
				.with("webmonNew", webmon) // (3)
				.noErrorHandling() // (4)
				.sendNowWith(dispatcher); // (5)
	}

	@Override
	public void webmonMoved(Webbi webmon) {
		List<Domain> domains = csb.findWithNamedQuery(
				"Domain.findByPosition",
				QueryParameter
						.with("latitude1",
								webmon.getPosition().getPositionLatitude())
						.and("latitude2",
								webmon.getPosition().getPositionLatitude())
						.and("longitude1",
								webmon.getPosition().getPositionLongitude())
						.and("longitude2",
								webmon.getPosition().getPositionLongitude())
						.parameters());

		for (Domain domain : domains) {
			MessageBuilder.createMessage().toSubject("Domain" + domain.getId()) // (1)
					.signalling() // (2)
					.with("webmonNew", webmon).noErrorHandling() // (4)
					.sendNowWith(dispatcher); // (5)
		}
		// wmb.routeMoveEvent(webmon);
	}

	@Override
	public void ping(Webbi webbi) {
		webmonMoved(webbi);
		// wmb.routeMoveEvent(webbi);
	}

	@Override
	public List<Webbi> getWebmonsForPlayer(Long playerID) {
		List<Webbi> webbis = csb.findWithNamedQuery("Webbi.findByPlayer",
				QueryParameter.with("playerId", playerID).parameters());
		// set is not marshallable by default
		return webbis;
	}

}

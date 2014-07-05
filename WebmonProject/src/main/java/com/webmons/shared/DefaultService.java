package com.webmons.shared;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;

import com.webmons.shared.model.Domain;
import com.webmons.shared.model.Home;
import com.webmons.shared.model.Player;
import com.webmons.shared.model.Webbi;


@Remote
public interface DefaultService {

	public Player create(Player player);
	public Player login(String username, String password);
	public Domain getDomain(Home home);
	public void addWebmonToPlayer(Long playerId, Webbi webmon);
	public void webmonMoved(Webbi webmon);
	public void ping(Webbi webbi);
	public List<Webbi> getWebmonsForPlayer(Long playerID);

}

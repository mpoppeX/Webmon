package com.webmons.client.local.page.widget;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.LoadApi.LoadLibrary;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.webmons.client.local.page.LoginPage;
import com.webmons.client.local.page.MapPage;
import com.webmons.client.local.util.WebmonManagerClient;
import com.webmons.shared.DefaultService;
import com.webmons.shared.model.Domain;
import com.webmons.shared.model.Player;
import com.webmons.shared.model.Webbi;

@Templated
public class MapWidget extends Composite {

	// EntityManager for Client-DB
	@Inject
	EntityManager em;

	@Inject
	@DataField
	private Button logoutButton;
	@Inject
	@DataField
	private Button toggleDomain;
	@Inject
	@DataField
	private Button toggleView;
	@Inject
	@DataField
	private HorizontalPanel map_canvas;
	
	@Inject
	TransitionTo<LoginPage> transitionToLoginPage;

	@Inject 
	private MessageBus bus;
	@Inject
	private Caller<DefaultService> endpoint;
	
	private Player player;
	
	@Inject
	private WebmonManagerClient wmm;
		
	@PostConstruct
	private void postConstruct() {
		// Window.alert("postConstruct");
	}
	
	/**
	 * Initializes the WidgetSetup for the user with {@code userId}. Generates a
	 * map object and gets the User object. Called in @PageShown of
	 * {@link MapPage}.
	 * 
	 * @param player
	 *            The {@code id} of the User.
	 */
	public void setup(Player player) {
		MapWidget.this.player = player;
		// subscribe to own sessionID to exclusivly receive messages from server for uni- & multicast
		bus.subscribe("Player"+player.getSessionId(), new MessageCallback() {
			@Override
			public void callback(Message message) {
				// a new webmon was created for us
				Webbi wb = message.get(Webbi.class, "webmonNew");
				wmm.add(wb, true);

			}
		});
		//get the domain
		endpoint.call(new RemoteCallback<Domain>() {

			@Override
			public void callback(Domain domain) {
				MapWidget.this.wmm.setDomain(domain);
				//get the webmons for this user
				endpoint.call(new RemoteCallback<List<Webbi>>() {

					@Override
					public void callback(List<Webbi> response) {
						for(Webbi webbi:response){
							wmm.add(webbi, true);
						}				
					}
				}).getWebmonsForPlayer(MapWidget.this.player.getId());
			}
		}).getDomain(player.getHome());

		boolean sensor = true;
		//TODO remove libraries not needed
		ArrayList<LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
		loadLibraries.add(LoadLibrary.ADSENSE);
		loadLibraries.add(LoadLibrary.DRAWING);
		loadLibraries.add(LoadLibrary.GEOMETRY);
		loadLibraries.add(LoadLibrary.PANORAMIO);
		loadLibraries.add(LoadLibrary.PLACES);
		loadLibraries.add(LoadLibrary.WEATHER);
		loadLibraries.add(LoadLibrary.VISUALIZATION);
		LoadApi.go(new Runnable() {
			@Override
			public void run() {
				initializeMap();
			}
		}, loadLibraries, sensor);
	}

	/**
	 * 
	 */
	private void initializeMap() {
	    VerticalPanel vp = new VerticalPanel();
	    vp.add(new Button("test"));
	    vp.setWidth("200px");
	    map_canvas.add(vp);;
	    
		wmm.setMap(player);  
		map_canvas.setSize("750px", "500px");
	    map_canvas.add(wmm.getMap());
	    
	    //reload to avoid display failure
	    wmm.triggerMap();
		// create marker for home
	    wmm.setHome(player.getHome());
	}
		
	@EventHandler("logoutButton")
	private void onLogoutButtonClicked(ClickEvent event) {
		// TODO logout procedure; setIsLoggedin=false
		transitionToLoginPage.go();
	}
	
	@EventHandler("toggleDomain")
	private void onDomainButtonClicked(ClickEvent event) {
		wmm.toggleDomain();
	}
	
	@EventHandler("toggleView")
	private void onViewButtonClicked(ClickEvent event) {
		wmm.toggleView();		
	}

}

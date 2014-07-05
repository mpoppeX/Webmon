package com.webmons.client.local.util;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.common.client.api.Caller;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.base.LatLngBounds;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.geometrylib.SphericalUtils;
import com.google.gwt.maps.client.overlays.Circle;
import com.google.gwt.maps.client.overlays.CircleOptions;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.overlays.Rectangle;
import com.google.gwt.maps.client.overlays.RectangleOptions;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.webmons.client.local.page.DefaultBundle;
import com.webmons.shared.DefaultService;
import com.webmons.shared.model.Domain;
import com.webmons.shared.model.Home;
import com.webmons.shared.model.Player;
import com.webmons.shared.model.Webbi;

@ApplicationScoped
public class WebmonManagerClient {

	@Inject
	private MessageBus bus;
	@Inject
	public Event<Webbi> webmonEvent;
	@Inject
	private Caller<DefaultService> endpoint;

	private Set<WebmonObject> managedWebmonObjects = Sets.newHashSet(); // new
																		// HashSet<WebmonObject>();
	private Set<WebmonObject> unmanagedWebmonObjects = new HashSet<WebmonObject>();
	private Domain domain;
	private Home home;
	private MapWidget map;
	private Circle viewUser;
	private Rectangle viewDomain;

	public WebmonManagerClient() {
		super();
		// moveTimer.scheduleRepeating(1000);
	}

	public void setDomain(Domain domain) {

		subscribe(domain);
		// destroy old object
		if (this.viewDomain != null) {
			this.viewDomain.setMap((com.google.gwt.maps.client.MapWidget) null);
		}
		// create new viewObject
		RectangleOptions rectOpts = RectangleOptions.newInstance();
		rectOpts.setStrokeColor("#FF0000");
		rectOpts.setStrokeOpacity(0.8);
		rectOpts.setStrokeWeight(2);
		rectOpts.setFillColor("#FF0000");
		rectOpts.setFillOpacity(0.35);
		LatLng sw = LatLng.newInstance(domain.getSw().getPositionLatitude(),
				domain.getSw().getPositionLongitude());
		LatLng ne = LatLng.newInstance(domain.getNe().getPositionLatitude(),
				domain.getNe().getPositionLongitude());
		LatLngBounds bounds = LatLngBounds.newInstance(sw, ne);
		rectOpts.setBounds(bounds);
		rectOpts.setClickable(false);
		this.viewDomain = Rectangle.newInstance(rectOpts);

	}

	private void subscribe(Domain domain) {
		// unsubscribe old domain
		if (this.domain != null) {
			if (bus.isSubscribed("Domain" + this.domain.getId())) {
				bus.unsubscribeAll("Domain" + this.domain.getId());
			}
		}
		this.domain = domain;
		// listen to (new) Domain
		bus.subscribe("Domain" + this.domain.getId(), new MessageCallback() {
			@Override
			public void callback(Message message) {
				final Webbi wb = message.get(Webbi.class, "webmonNew");
				boolean contains = !Sets.filter(managedWebmonObjects,
						new Predicate<WebmonObject>() {
							@Override
							public boolean apply(WebmonObject input) {
								return input.getWebbi().getId() == wb.getId();
							}
						}).isEmpty();
				if (!contains) {
					// we may have an unmanaged one here
					Set<WebmonObject> filteredWMO = Sets.filter(
							unmanagedWebmonObjects,
							new Predicate<WebmonObject>() {
								@Override
								public boolean apply(WebmonObject input) {
									return input.getWebbi().getId() == wb
											.getId();
								}
							});
					double dist = SphericalUtils.computeDistanceBetween(
							LatLngGeoPointConverter.convert(wb.getPosition()),
							viewUser.getCenter());
					if (filteredWMO.isEmpty()) {
						// we have a NEW unmanaged one here
						for (WebmonObject wmo : managedWebmonObjects) {
							// check if seen by a managed webmon
							if (wmo.sees(wb)) {
								add(wb, false);
								return;
							}
						}
						// check if seen by home
						if (dist < viewUser.getRadius()) {
							add(wb, false);
							return;
						}

					} else {
						// we have to update an existing one here
						// check if still in views
						for (WebmonObject wmo : managedWebmonObjects) {
							// check if seen by a webmon
							if (wmo.sees(wb)) {
								// TODO nicer please
								for (WebmonObject wmoF : filteredWMO) {
									wmoF.setPosition(wb.getPosition());
								}
								return;
							}
						}
						// check if seen by home
						if (dist < viewUser.getRadius()) {
							// TODO nicer please
							for (WebmonObject wmoF : filteredWMO) {
								wmoF.setPosition(wb.getPosition());
							}
							return;
						}

						// here the webmon was not detected any more
						for (WebmonObject wmoF : filteredWMO) {
							wmoF.remove();
							filteredWMO.remove(wmoF);
						}
					}
				}
			}
		});
	}

	public void add(Webbi wb, boolean managed) {
		WebmonObject wmo = new WebmonObject(wb, map, managed, endpoint);
		if (managed) {
			managedWebmonObjects.add(wmo);
		} else {
			unmanagedWebmonObjects.add(wmo);
		}
	}

	public void setMap(final Player player) {
		LatLng center = LatLng.newInstance(player.getHome().getPosition()
				.getPositionLatitude(), player.getHome().getPosition()
				.getPositionLongitude());
		MapOptions mapOpts = MapOptions.newInstance();
		mapOpts.setZoom(14);
		mapOpts.setCenter(center);
		mapOpts.setMapTypeId(MapTypeId.ROADMAP);
		map = new MapWidget(mapOpts);
		map.setHeight("500px");
		map.setWidth("1000px");
		map.addClickHandler(new ClickMapHandler() {
			@Override
			public void onEvent(ClickMapEvent event) {
				// create Webmon
				Webbi webmon = new Webbi(LatLngGeoPointConverter.convert(event
						.getMouseEvent().getLatLng()));
				endpoint.call().addWebmonToPlayer(player.getId(), webmon);
			}
		});
	}

	public Widget getMap() {
		return map;
	}

	public void setHome(Home home) {
		this.home = home;
		LatLng userHome = LatLng.newInstance(home.getPosition()
				.getPositionLatitude(), home.getPosition()
				.getPositionLongitude());
		MarkerOptions markerHome = MarkerOptions.newInstance();
		Image homePic = new Image(
				((DefaultBundle) GWT.create(DefaultBundle.class)).getHome());
		markerHome.setIcon(homePic.getUrl());
		markerHome.setPosition(userHome);
		markerHome.setTitle("This is your home.");
		markerHome.setMap(map);
		Marker.newInstance(markerHome);
		if (this.viewUser == null) {
			CircleOptions options = CircleOptions.newInstance();
			options.setStrokeColor("#AEEEEE");
			options.setStrokeOpacity(0.8);
			options.setStrokeWeight(2);
			options.setFillOpacity(0.35);
			options.setRadius(home.getViewDistance());
			options.setClickable(false);
			LatLng center = LatLngGeoPointConverter.convert(home.getPosition());
			options.setCenter(center);
			this.viewUser = Circle.newInstance(options);
		}
	}

	public void toggleView() {
		if (viewUser.getMap() != null) {
			viewUser.setMap(null);
		} else {
			viewUser.setMap(map);
		}
	}

	public void toggleDomain() {
		if (viewDomain.getMap() != null) {
			viewDomain.setMap(null);
		} else {
			viewDomain.setMap(map);
		}

	}

	public void triggerMap() {
		if (map != null) {
			map.triggerResize();
		}
	}
}

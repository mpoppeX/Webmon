package com.webmons.client.local.page.widget;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.widget.AbstractForm;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.gwt.geolocation.client.Position;
import com.google.gwt.geolocation.client.Position.Coordinates;
import com.google.gwt.geolocation.client.PositionError;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.LoadApi.LoadLibrary;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.services.Geocoder;
import com.google.gwt.maps.client.services.GeocoderAddressComponent;
import com.google.gwt.maps.client.services.GeocoderRequest;
import com.google.gwt.maps.client.services.GeocoderRequestHandler;
import com.google.gwt.maps.client.services.GeocoderResult;
import com.google.gwt.maps.client.services.GeocoderStatus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.webmons.client.local.page.LoginPage;
import com.webmons.shared.DefaultService;
import com.webmons.shared.model.GeoPoint;
import com.webmons.shared.model.Home;
import com.webmons.shared.model.Player;

@Templated
public class RegisterWidget extends AbstractForm {
	// EntityManager for local client storage
	@Inject
	EntityManager em;
	
	@DataField
	private final FormElement form = FormElement.as(DOM.createForm()); //
	@Inject
	@DataField
	private Label adress;
	@Inject
	@DataField
	private TextBox username;
	@Inject
	@DataField
	private TextBox email;
	@Inject
	@DataField
	private TextBox password;
	@Inject
	@DataField
	private Button registerButton;
	@Inject
	@DataField
	private Button cancelButton;
	@Inject
	@DataField
	private SimplePanel map_canvas;
	
	@Inject
	TransitionTo<LoginPage> transitionToLoginPage;

//	@Inject
//	private Caller<UserService> endpoint;
	@Inject
	private Caller<DefaultService> serviceEndpoint;
	
	private Boolean geocodingSupportFlag;
	private LatLng locationLatLng;
	private com.google.gwt.maps.client.MapWidget map;
	private Geocoder geocoder;
	private Marker markerHome;
	
	@PostConstruct
	private void postConstruct() {
		// build map object Map Object
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
				initializeGeocoding();
			}
		}, loadLibraries, sensor);
	}

	@EventHandler("cancelButton")
	private void onCancelButtonClicked(ClickEvent event) {
		transitionToLoginPage.go();
	}

	@EventHandler("registerButton")
	private void onRegisterButtonClicked(ClickEvent event) {
		Home home = new Home(new GeoPoint(locationLatLng.getLatitude(), locationLatLng.getLongitude()));
		Player player = new Player(username.getText(),email.getText(), password.getText(), home);
//		User user = new User(username.getText(),email.getText(), password.getText(), new GeoPoint(
//				locationLatLng.getLatitude(), locationLatLng.getLongitude()));
		serviceEndpoint.call(new RemoteCallback<Player>() {

			@Override
			public void callback(Player player) {
				Window.alert("Player " + player.getUsername()+" created. (ID:"+player.getId()+")\n");
				transitionToLoginPage.go();
			}
		}).create(player);
	}

	private void initializeMap() {
		MapOptions myOptions = MapOptions.newInstance();
		myOptions.setDraggable(true);
		myOptions.setZoom(14);
		myOptions.setCenter(LatLng.newInstance(0, 0));
		myOptions.setMapTypeId(MapTypeId.ROADMAP);
		map = new com.google.gwt.maps.client.MapWidget(myOptions);
		map.setHeight("500px");
//		map.setSize("100%", "300px");
		map_canvas.add(map);
		map.addClickHandler(new ClickMapHandler() {
			
			@Override
			public void onEvent(ClickMapEvent event) {
				locationLatLng = event.getMouseEvent().getLatLng();	
				doGeocode();				
			}
		});
	}
		
	private void doGeocode(){
		//gets the address string for a given LatLng and sets the marker the address label
		GeocoderRequest request = GeocoderRequest.newInstance();
		request.setLocation(locationLatLng);
		geocoder.geocode(request, new GeocoderRequestHandler() {
			
			@Override
			public void onCallback(JsArray<GeocoderResult> results,
					GeocoderStatus status) {
				if (status == GeocoderStatus.OK) {
					if (results.length() > 0) {
						GeocoderResult geocoderResult = results.get(0);
						//parse address to string
						StringBuffer sb = new StringBuffer();
						JsArray<GeocoderAddressComponent> addressComponents = geocoderResult.getAddress_Components();
						for (int i = 0; i < addressComponents.length(); i++) {
							if (i > 0) {
								sb.append(", ");
							}
							sb.append(addressComponents.get(i).getLong_Name());
						}
						//end of parsing
						adress.setText(sb.toString());

						setHomeMarker();
					} else {
						//TODO set and show a red error label with all alert outputs
						Window.alert("No results found");
						adress.setText(locationLatLng.toString());
						setHomeMarker();
					}
				} else {
					Window.alert("Geocode failed due to: " + status);
					adress.setText(locationLatLng.toString());
					setHomeMarker();
				}
				
			}
		}); 
	}

	protected void setHomeMarker() {
		//setMarker for home
		MarkerOptions markerOpts = MarkerOptions.newInstance();
		markerOpts.setMap(map);
		markerOpts.setPosition(locationLatLng);
		if (markerHome == null)
			markerHome = Marker.newInstance(markerOpts);
		else
			markerHome.setOptions(markerOpts);		
	}

	private void initializeGeocoding() {
		// initialize geocoding
		geocoder = Geocoder.newInstance();
		if (Geolocation.isSupported()) {
			geocodingSupportFlag = true;
			Geolocation.getIfSupported().getCurrentPosition(
					new Callback<Position, PositionError>() {

						@Override
						public void onSuccess(Position result) {
							Coordinates coords = result.getCoordinates();
							locationLatLng = LatLng.newInstance(
									coords.getLatitude(), coords.getLongitude());
							map.setCenter(locationLatLng);	
							doGeocode();
						}

						@Override
						public void onFailure(PositionError reason) {
							RegisterWidget.this
									.handleNoGeolocation(geocodingSupportFlag);
						}
					});
		} else {
			geocodingSupportFlag = false;
			handleNoGeolocation(geocodingSupportFlag);
		}
		
	}

	private void handleNoGeolocation(boolean errorFlag) {
		if (errorFlag == true) {
			Window.alert("Geolocation service failed. We've placed you in New York.");
			locationLatLng = LatLng.newInstance(40.703364, -74.014633);
		} else {
			Window.alert("Your browser doesn't support geolocation. We've placed you in New York.");
			locationLatLng = LatLng.newInstance(40.703364, -74.014633);
		}
		map.setCenter(locationLatLng);
	}

	@Override
	protected FormElement getFormElement() {
		return form;
	}
}

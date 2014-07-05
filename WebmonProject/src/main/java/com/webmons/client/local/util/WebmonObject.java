package com.webmons.client.local.util;

import org.jboss.errai.common.client.api.Caller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.events.drag.DragMapEvent;
import com.google.gwt.maps.client.events.drag.DragMapHandler;
import com.google.gwt.maps.client.events.dragend.DragEndMapEvent;
import com.google.gwt.maps.client.events.dragend.DragEndMapHandler;
import com.google.gwt.maps.client.geometrylib.SphericalUtils;
import com.google.gwt.maps.client.overlays.Animation;
import com.google.gwt.maps.client.overlays.Circle;
import com.google.gwt.maps.client.overlays.CircleOptions;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.webmons.client.local.page.DefaultBundle;
import com.webmons.shared.DefaultService;
import com.webmons.shared.model.GeoPoint;
import com.webmons.shared.model.Webbi;

/**
 * @author DaKing
 *
 * An Object that represents a Webmon on the Map.
 * movementTimer cares for the movement of the Object on the map.
 * pingTimer sends a ping to forward the actual location to the server for routing purposes.
 */
public class WebmonObject {
	// TODO use Polyutils to calculate if point is in view

	private Webbi webbi;
	private Marker marker;
	private Circle view;
	private Caller<DefaultService> endpoint;

	public WebmonObject(Webbi wb, MapWidget map, boolean managed,
			Caller<DefaultService> endpoint) {
		this.webbi = wb;
		this.endpoint = endpoint;
		createMarker(map, managed);
		if (managed) {
			createView(map);
			pingTimer.scheduleRepeating(1000);
		}
	}

	private void createMarker(MapWidget map, boolean managed) {
		MarkerOptions markerOptions = MarkerOptions.newInstance();
		Image webmonPic = new Image(
				((DefaultBundle) GWT.create(DefaultBundle.class)).getWebmon());
		markerOptions.setIcon(webmonPic.getUrl());
		LatLng latlng = LatLng.newInstance(this.webbi.getPosition()
				.getPositionLatitude(), this.webbi.getPosition()
				.getPositionLongitude());
		markerOptions.setPosition(latlng);
		markerOptions.setMap(map);
		markerOptions.setAnimation(Animation.DROP);
		markerOptions.setTitle("Webmon #" + this.webbi.getId() + "/n"
				+ this.webbi.getPosition().getPositionLatitude() + " / "
				+ this.webbi.getPosition().getPositionLongitude() + "/nSpeed: "
				+ this.webbi.getSpeed());

		if (managed) {
			markerOptions.setDraggable(true);
		}
		marker = Marker.newInstance(markerOptions);
		marker.addDragHandler(new DragMapHandler() {

			@Override
			public void onEvent(DragMapEvent event) {
				// TODO show time to arrive
			}
		});
		marker.addDragEndHandler(new DragEndMapHandler() {

			@Override
			public void onEvent(DragEndMapEvent event) {
				setTarget(LatLngGeoPointConverter.convert(marker.getPosition()));
			}
		});
	}

	private void createView(MapWidget map) {
		CircleOptions options = CircleOptions.newInstance();
		options.setStrokeColor("#AEEEEE");
		options.setStrokeOpacity(0.8);
		options.setStrokeWeight(2);
		options.setFillOpacity(0.35);
		options.setMap(map);
		options.setRadius(webbi.getViewDistance());
		options.setClickable(false);
		LatLng center = LatLngGeoPointConverter.convert(webbi.getPosition());
		options.setCenter(center);
		view = Circle.newInstance(options);
	}

	public Webbi getWebbi() {
		return webbi;
	}

	public void move() {
		double distance = SphericalUtils.computeDistanceBetween(
				LatLngGeoPointConverter.convert(webbi.getPosition()),
				LatLngGeoPointConverter.convert(webbi.getTarget()));
		GeoPoint newPosition;
		if (webbi.getSpeed() < distance) {
			double heading = SphericalUtils.computeHeading(
					LatLngGeoPointConverter.convert(webbi.getPosition()),
					LatLngGeoPointConverter.convert(webbi.getTarget()));
			newPosition = LatLngGeoPointConverter.convert(SphericalUtils
					.computeOffset(LatLngGeoPointConverter.convert(webbi
							.getPosition()), webbi.getSpeed(), (int) heading));
		} else {
			newPosition = webbi.getTarget();
			setTarget(null);
		}
		this.setPosition(newPosition);
	}

	public void setPosition(GeoPoint position) {
		webbi.setPosition(position);
		marker.setPosition(LatLngGeoPointConverter.convert(webbi.getPosition()));
		if (view != null) {
			view.setCenter(LatLngGeoPointConverter.convert(position));
		}
	}

	@Override
	public boolean equals(Object obj) {
		return this.webbi.equals(((WebmonObject) obj).getWebbi());
	}

	public boolean sees(Webbi wb) {
		double dist = SphericalUtils.computeDistanceBetween(
				LatLngGeoPointConverter.convert(wb.getPosition()),
				view.getCenter());
		return dist < view.getRadius();
	}

	/**
	 * Removes the marker and the view (if managed) from the map
	 */
	public void remove() {
		if (view != null) {
			view.setMap(null);
		}
		marker.setMap((com.google.gwt.maps.client.MapWidget) null);
	}

	private void setTarget(GeoPoint target) {
		if (target != null) {
			//new target
			marker.setPosition(LatLngGeoPointConverter.convert(webbi
					.getPosition()));
			webbi.setTarget(target);
			webbi.setMoving(true);
			movementTimer.scheduleRepeating(1000);
		} else {
			//target reached
			webbi.setTarget(target);
			webbi.setMoving(false);
			movementTimer.cancel();
		}
	}

	private Timer movementTimer = new Timer() {
		@Override
		public void run() {
			move();
		}
	};
	private Timer pingTimer = new Timer() {
		@Override
		public void run() {
			endpoint.call().ping(webbi);
		}
	};
}

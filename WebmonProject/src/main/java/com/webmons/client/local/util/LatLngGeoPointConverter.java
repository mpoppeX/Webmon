package com.webmons.client.local.util;

import com.google.gwt.maps.client.base.LatLng;
import com.webmons.shared.model.GeoPoint;

public class LatLngGeoPointConverter {
	public static GeoPoint convert(LatLng latLng) {
		GeoPoint gp = new GeoPoint(latLng.getLatitude(), latLng.getLongitude());
		return gp;
	}

	public static LatLng convert(GeoPoint latLng) {
		LatLng sw = LatLng.newInstance(latLng.getPositionLatitude(),
				latLng.getPositionLongitude());
		return sw;
	}
}

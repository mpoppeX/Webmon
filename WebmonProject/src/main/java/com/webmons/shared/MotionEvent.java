package com.webmons.shared;

import org.jboss.errai.common.client.api.annotations.Portable;

import com.webmons.shared.model.Webbi;

@Portable
public class MotionEvent {

	private Webbi webmon;

	public MotionEvent() {
		super();
	}

	public MotionEvent(Webbi webmon) {
		super();
		this.webmon = webmon;
	}

	public Webbi getWebmon() {
		return webmon;
	}

	public void setWebmon(Webbi webmon) {
		this.webmon = webmon;
	}

}

package com.webmons.client.local.page;

import javax.annotation.PostConstruct;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.webmons.client.local.page.widget.FooterWidget;
import com.webmons.client.local.page.widget.HeaderWidget;

@Templated
public class PageLayout extends Composite{

	@Inject
	@DataField
	private HeaderWidget header;
	
	@Inject
	@DataField
	private FooterWidget footer;
	
	@PostConstruct
	private void postConstruct(){
//		Window.alert("PageLayout: postConstruct");
	}
}

package com.webmons.client.local.page;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.webmons.client.local.page.widget.RegisterWidget;

@Page
@Templated("PageLayout.html")
public class RegisterPage extends PageLayout{

	@Inject
	@DataField
	private RegisterWidget content;
	
	@PostConstruct
	public void postConstruct(){
//		Window.alert("RegisterPage: postConstruct");
	}
}

package com.webmons.client.local.page;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShowing;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.nav.client.local.PageState;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.webmons.client.local.page.widget.MapWidget;
import com.webmons.shared.model.Player;

@Page
@Templated("PageLayout.html")
public class MapPage extends PageLayout {

	/**
	 * The EntityManager that interacts with client-local storage.
	 */
	@Inject
	EntityManager em;
	
	@PageState
	  private String activeUserId;
	
	@Inject
	@DataField
	private MapWidget content;
	
	@AfterInitialization
	private void afterInitialization(){
	}
	
	@PageShowing
	private void pageShowing(){
		Player player = em.find(Player.class, activeUserId);
		content.setup(player);
	}
	
	@PageShown
	private void pageShown(){
	}
	
	@PostConstruct
	private void postConstruct(){	

		//before @PageShown
		//before content set
//		Window.alert("LoginPage: postConstruct");
	}
}

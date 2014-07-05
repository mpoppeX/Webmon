package com.webmons.client.local.page;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.webmons.client.local.page.widget.LoginWidget;

@EntryPoint
@Page(role = DefaultPage.class)
@Templated("PageLayout.html")
public class LoginPage extends PageLayout {

	/**
	 * The EntityManager that interacts with client-local storage.
	 */
	@Inject
	EntityManager em;

	@Inject
	@DataField
	private LoginWidget content;

	@PageShown
	private void pageShown() {

	}

	@PostConstruct
	private void postConstruct() {
		// Window.alert("LoginPage: postConstruct");
	}
}

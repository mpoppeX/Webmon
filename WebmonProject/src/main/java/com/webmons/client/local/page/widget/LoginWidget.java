package com.webmons.client.local.page.widget;

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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.webmons.client.local.page.MapPage;
import com.webmons.client.local.page.RegisterPage;
import com.webmons.shared.DefaultService;
import com.webmons.shared.model.Player;

@Templated
public class LoginWidget extends AbstractForm {

	/**
	 * The EntityManager that interacts with client-local storage.
	 */
	@Inject
	EntityManager em;

	@DataField
	private final FormElement form = FormElement.as(DOM.createForm()); //
	@Inject
	@DataField
	private TextBox username;
	@Inject
	@DataField
	private PasswordTextBox password;
	@Inject
	@DataField
	private Button login;
	@Inject
	@DataField
	private Button register;

	@Inject
	TransitionTo<RegisterPage> transitionToRegisterPage;
	@Inject
	TransitionTo<MapPage> transitionToMapPage;
//
//	@Inject
//	private Caller<UserService> endpoint;
	@Inject
	private Caller<DefaultService> defaultServiceEndpoint;

	@EventHandler("login")
	private void loginClicked(ClickEvent event) {
		defaultServiceEndpoint.call(new RemoteCallback<Player>() {

			@Override
			public void callback(Player user) {
				// clear all storage to start new;
//				((ErraiEntityManager) em).removeAll();
//				em.flush();
				// persist in local storage
//				user.getDomainArea().setUsers(null);
				em.merge(user);
				// make persistent because we transition page now.
				em.flush();
				//pass id to mapPage
				final Multimap<String, String> map = ArrayListMultimap.create();
				map.put("activeUserId", user.getId().toString());
				transitionToMapPage.go(map);
			}
		}).login(username.getText(), password.getText());
		// TODO use authentication framework see errai documentation
		// (picketlink)
		// authenticationServiceCaller.call(new RemoteCallback<User>() {
		//
		// @Override
		// public void callback(User response) {
		// // Now that we're logged in, submit the form
		// submit(); // 5
		// }
		// }).login(username.getText(), password.getText());
	}

	@PostConstruct
	private void postConstruct() {
		// Window.alert("LoginWidget: postConstruct");
		
	}

	@EventHandler("register")
	private void registerClicked(ClickEvent event) {
		transitionToRegisterPage.go();
	}

	@Override
	protected FormElement getFormElement() {
		return form;
	}

}

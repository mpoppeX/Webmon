package com.webmons.shared;

import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ReregisterEvent {


	private Set<Long> users;

	public ReregisterEvent() {
		super();
	}

	public ReregisterEvent(Set<Long> userIds) {
		super();
		this.users = userIds;
	}

	public Set<Long> getUsers() {
		return users;
	}

	public void setUsers(Set<Long> users) {
		this.users = users;
	}
	
}

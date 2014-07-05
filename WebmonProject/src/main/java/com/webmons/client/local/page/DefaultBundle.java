package com.webmons.client.local.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface DefaultBundle extends ClientBundle {

    public static final DefaultBundle INSTANCE = GWT.create(DefaultBundle.class);
    
    @Source("home2.png")
    ImageResource getHome();

    @Source("kobold2.png")
	ImageResource getWebmon();
}

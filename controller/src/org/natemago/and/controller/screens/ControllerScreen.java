package org.natemago.and.controller.screens;

import org.xdruid.ui.Dispatcher;
import org.xdruid.ui.SimpleScreen;

import android.app.Activity;

public class ControllerScreen extends SimpleScreen{

	public ControllerScreen(Activity parent, Dispatcher dispatcher, String name) {
		super(parent, dispatcher, name);
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	protected void screenReloading(Object dataObejct) throws Exception {
		
	}

	@Override
	protected String getInitialLayout() {
		return null;
	}

}

package io.openliberty.guides.inventory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.openliberty.guides.config.Email;

@RequestScoped
public class InventoryConfig {

	@Inject
	@ConfigProperty(name="io_openliberty_guides_port_number")
	private int portNumber;
	
	@Inject
	@ConfigProperty(name="io_openliberty_guides_inventory_inMaintenace")
	private Provider<Boolean> inMaintenance;
	
	@Inject
	@ConfigProperty(name="io_openliberty_guides_email")
	private Provider<Email> email;
	
	public int getPortNumber() {
		return portNumber;
	}
	
	public boolean isInMaintenance() {
		return inMaintenance.get();
	}
	
	public Email getEmail() {
		return email.get();
	}
	
}

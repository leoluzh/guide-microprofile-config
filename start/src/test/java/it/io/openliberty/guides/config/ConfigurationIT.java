package it.io.openliberty.guides.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class ConfigurationIT {

	private String port;
	private String baseURL;
	private Client client;
	
	private final String INVENTORY_HOSTS = "inventory/systems" ;
	private final String USER_DIR = System.getProperty("user.dir");
	private final String DEFAULT_CONFIG_FILE = USER_DIR + "/src/main/resources/META-INF/microprofile-config.properties" ;
	private final String CUSTOM_CONFIG_FILE = USER_DIR.split("target")[0] + "/resources/CustomConfigSource.json" ;
	private final String INV_MAINTENANCE_PROP = "io_openliberty_guides_inventory_inMaintenance" ;
	
	@BeforeEach
	public void setup() {
		port = System.getProperty("default.http.port");
		baseURL = "http://localhost" + port + "/" ;
		ConfigITUtil.setDefaultJsonFile(CUSTOM_CONFIG_FILE);
		client = ClientBuilder.newClient();
		client.register(JsrJsonpProvider.class);
	}
	
	@AfterEach
	public void teardown() {
		ConfigITUtil.setDefaultJsonFile(CUSTOM_CONFIG_FILE);
		client.close();
	}
	
	@Test
	@Order(1)
	public void testInitialServiceStatus() {
	    boolean status = Boolean.valueOf(ConfigITUtil.readPropertyValueInFile(
	        INV_MAINTENANCE_PROP, DEFAULT_CONFIG_FILE));
	    if (!status) {
	      Response response = ConfigITUtil.getResponse(client, baseURL + INVENTORY_HOSTS);
	      int expected = Response.Status.OK.getStatusCode();
	      int actual = response.getStatus();
	      assertEquals(expected, actual);
	    } else {
	      assertEquals(
	        "{ \"error\" : \"Service is currently in maintenance. Contact: admin@guides.openliberty.io\" }",
	          ConfigITUtil.getStringFromURL(client, baseURL + INVENTORY_HOSTS),
	          "The Inventory Service should be in maintenance");
	    }
	}	
	
	@Test
	@Order(2)
	public void testPutServiceInMaintenance() {
	    Response response = ConfigITUtil.getResponse(client, baseURL + INVENTORY_HOSTS);

	    int expected = Response.Status.OK.getStatusCode();
	    int actual = response.getStatus();
	    assertEquals(expected, actual);

	    ConfigITUtil.switchInventoryMaintenance(CUSTOM_CONFIG_FILE, true);

	    String error = ConfigITUtil.getStringFromURL(client, baseURL + INVENTORY_HOSTS);

	    assertEquals(
	      "{ \"error\" : \"Service is currently in maintenance. Contact: admin@guides.openliberty.io\" }",
	        error, "The inventory service should be down in the end");
	}	
	
	
	@Test
	@Order(3)
	public void testChangeEmail() {
	    ConfigITUtil.switchInventoryMaintenance(CUSTOM_CONFIG_FILE, true);

	    String error = ConfigITUtil.getStringFromURL(client, baseURL + INVENTORY_HOSTS);

	    assertEquals(
	      "{ \"error\" : \"Service is currently in maintenance. Contact: admin@guides.openliberty.io\" }",
	        error, "The email should be admin@guides.openliberty.io in the beginning");

	    ConfigITUtil.changeEmail(CUSTOM_CONFIG_FILE, "service@guides.openliberty.io");

	    error = ConfigITUtil.getStringFromURL(client, baseURL + INVENTORY_HOSTS);

	    assertEquals(
	      "{ \"error\" : \"Service is currently in maintenance. Contact: service@guides.openliberty.io\" }",
	        error, "The email should be service@guides.openliberty.io in the beginning");
	}
	
	
}

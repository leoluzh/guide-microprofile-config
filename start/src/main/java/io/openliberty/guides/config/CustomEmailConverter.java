package io.openliberty.guides.config;

import org.eclipse.microprofile.config.spi.Converter;

@SuppressWarnings("serial")
public class CustomEmailConverter implements Converter<Email> {

	@Override
	public Email convert( String value ) {
		return new Email( value );
	}
	
}

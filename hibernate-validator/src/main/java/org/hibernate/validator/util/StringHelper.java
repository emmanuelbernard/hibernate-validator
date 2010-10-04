package org.hibernate.validator.util;

/**
 * @author Emmanuel Bernard
 */
public class StringHelper {
	public static boolean toBoolean(String value, boolean defaultValue, String comment) {
		if ( isEmpty( value ) ) {
			return defaultValue;
		}
		if ( value.equalsIgnoreCase( "true" ) ) {
			return true;
		}
		if ( value.equalsIgnoreCase( "false" ) ) {
			return false;
		}
		throw new IllegalArgumentException( "Unable to parse " + (comment != null ? comment : "") + "into boolean: " + value );
	}

	private static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}
}

package org.cytoscape.rest.internal.datamapper;

public class MapperUtil {

	public static final Class<?> getColumnClass(final String type) {
		if (type.equals(Double.class.getSimpleName())) {
			return Double.class;
		} else if (type.equals(Long.class.getSimpleName())) {
			return Long.class;
		} else if (type.equals(Integer.class.getSimpleName())) {
			return Integer.class;
		} else if (type.equals(Float.class.getSimpleName())) {
			return Float.class;
		} else if (type.equals(Boolean.class.getSimpleName())) {
			return Boolean.class;
		} else if (type.equals(String.class.getSimpleName())) {
			return String.class;
		} else if (type.equals(Number.class.getSimpleName())) {
			return Double.class;
		} else {
			return null;
		}
	}
	
	public static final Object getRawValue(final String queryString, Class<?> type) {
		Object raw = queryString;

		if (type == Boolean.class) {
			raw = Boolean.parseBoolean(queryString);
		} else if (type == Double.class) {
			raw = Double.parseDouble(queryString);
		} else if (type == Integer.class) {
			raw = Integer.parseInt(queryString);
		} else if (type == Long.class) {
			raw = Long.parseLong(queryString);
		} else if (type == Float.class) {
			raw = Float.parseFloat(queryString);
		}
		return raw;
	}
}

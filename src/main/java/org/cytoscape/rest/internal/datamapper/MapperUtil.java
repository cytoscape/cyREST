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
		} else {
			return null;
		}
	}
}

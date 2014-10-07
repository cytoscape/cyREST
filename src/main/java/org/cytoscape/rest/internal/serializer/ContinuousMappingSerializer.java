package org.cytoscape.rest.internal.serializer;

import java.io.IOException;
import java.util.List;

import org.cytoscape.rest.internal.datamapper.VisualStyleMapper;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.ContinuousMappingPoint;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@SuppressWarnings("rawtypes")
public class ContinuousMappingSerializer extends JsonSerializer<ContinuousMapping> {

	@Override
	public Class<ContinuousMapping> handledType() {
		return ContinuousMapping.class;
	}

	@Override
	public void serialize(ContinuousMapping mapping, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.useDefaultPrettyPrinter();
		
		jgen.writeStartObject();
		jgen.writeStringField(VisualStyleMapper.MAPPING_TYPE, "continuous");
		jgen.writeStringField(VisualStyleMapper.MAPPING_COLUMN, mapping.getMappingColumnName());
		jgen.writeStringField(VisualStyleMapper.MAPPING_COLUMN_TYPE, mapping.getMappingColumnType().getSimpleName());
		jgen.writeStringField(VisualStyleMapper.MAPPING_VP, mapping.getVisualProperty().getIdString());
		
		serializePoints(mapping, jgen);
		
		jgen.writeEndObject();
	}
	
	@SuppressWarnings("unchecked")
	private final void serializePoints(final ContinuousMapping mapping, final JsonGenerator jgen) throws IOException {
		
		final VisualProperty<Object> vp = mapping.getVisualProperty();
		jgen.writeArrayFieldStart("points");
		final List<ContinuousMappingPoint> points = mapping.getAllPoints();
		for(final ContinuousMappingPoint<?, ?> point : points) {
			jgen.writeStartObject();
			jgen.writeNumberField("value", (Double) point.getValue());
			final BoundaryRangeValues<?> range = point.getRange();
			jgen.writeStringField("lesser", vp.toSerializableString(range.lesserValue));
			jgen.writeStringField("equal", vp.toSerializableString(range.equalValue));
			jgen.writeStringField("greater", vp.toSerializableString(range.greaterValue));
			jgen.writeEndObject();
		}
		jgen.writeEndArray();
	}
}

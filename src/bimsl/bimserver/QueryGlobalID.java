package bimsl.bimserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class QueryGlobalID {

	private final List<IfcRoot> objects;

	public QueryGlobalID(IfcRoot object) {
		this.objects = Arrays.asList(object);
	}

	public QueryGlobalID(List<IfcRoot> objects) {
		this.objects = objects;
	}

	public Map<IfcRoot, String> getResult() {
		Map<IfcRoot, String> result = new HashMap<IfcRoot, String>();
		objects.forEach(object -> result.put(object, object.getGlobalId()));
		return result;
	}
	
}

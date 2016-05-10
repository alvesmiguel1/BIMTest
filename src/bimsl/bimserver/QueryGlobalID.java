package bimsl.bimserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class QueryGlobalID {

	private final Set<IfcRoot> objects;

	public QueryGlobalID(Set<IfcRoot> objects) {
		this.objects = objects;
	}

	public Map<IfcRoot, List<Object>> getResult() {
		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		objects.forEach(object -> result.put(object, Arrays.asList(object.getGlobalId())));
		return result;
	}
	
}

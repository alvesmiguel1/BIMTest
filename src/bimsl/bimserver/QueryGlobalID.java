package bimsl.bimserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class QueryGlobalID {

	private final Set<IfcRoot> objects;
	private Map<IfcRoot, List<Object>> result;

	public QueryGlobalID(Set<IfcRoot> objects) {
		this.objects = objects;
	}

	public void verifyAndPut(IfcRoot object) {
		String id = object.getGlobalId();
		if (id != null)
			result.put(object, Arrays.asList(id));
	}

	public Map<IfcRoot, List<Object>> getResult() {
		result = new HashMap<IfcRoot, List<Object>>();
		objects.forEach(object -> verifyAndPut(object));
		return result;
	}

}

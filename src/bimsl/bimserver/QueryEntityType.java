package bimsl.bimserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class QueryEntityType {

	private final List<IfcRoot> objects;

	public QueryEntityType(IfcRoot object) {
		this.objects = Arrays.asList(object);
	}

	public QueryEntityType(List<IfcRoot> objects) {
		this.objects = objects;
	}

	public List<String> getResult() {
		List<String> result = new ArrayList<String>();
		objects.forEach(object -> result.add(object.eClass().getName()));
		return result;
	}

}

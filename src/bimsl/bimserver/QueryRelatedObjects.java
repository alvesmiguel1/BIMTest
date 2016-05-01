package bimsl.bimserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcObject;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.eclipse.emf.common.util.EList;

public class QueryRelatedObjects {

	private final List<IfcRoot> objects;
	private final String type;
	private final int depth;
	
	public QueryRelatedObjects(List<IfcRoot> objects, String type, int depth) {
		this.objects = objects;
		this.type = type;
		this.depth = depth;
	}
	
	private List<Object> addRelated(EList<?> objects, int count) {
		
		List<Object> result = new ArrayList<Object>();
		for (Object object : objects) {
			if (type.equals("all") || object.getClass().getSimpleName().equals(type)) 
				result.add(object);
		 	if (count != 1 && object instanceof IfcObject) {
		 		int newcount = count--;
		 		IfcObject ifcObject = (IfcObject) object;
		 		result.addAll(addRelated(ifcObject.getDecomposes(), newcount));
		 		result.addAll(addRelated(ifcObject.getHasAssignments(), newcount));
		 		result.addAll(addRelated(ifcObject.getHasAssociations(), newcount));
		 		result.addAll(addRelated(ifcObject.getIsDecomposedBy(), newcount));
		 		result.addAll(addRelated(ifcObject.getIsDefinedBy(), newcount));
		 	}	 		
		}
		return result;
		
	}
	
	public Map<IfcRoot, List<Object>> getResult() {
		
		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		for (IfcRoot object : objects) {
			List<Object> relatedObjects = new ArrayList<Object>();
			if (object instanceof IfcObject) {
				IfcObject ifcObject = (IfcObject) object;
				relatedObjects.addAll(addRelated(ifcObject.getDecomposes(), depth));
				relatedObjects.addAll(addRelated(ifcObject.getHasAssignments(), depth));
				relatedObjects.addAll(addRelated(ifcObject.getHasAssociations(), depth));
				relatedObjects.addAll(addRelated(ifcObject.getIsDecomposedBy(), depth));
				relatedObjects.addAll(addRelated(ifcObject.getIsDefinedBy(), depth));
			}
			result.put(object, relatedObjects);
		}
		return result;
		
	}
	
}

package bimsl.bimserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.eclipse.emf.common.util.EList;

public class QueryAttribute {

	private final Set<IfcRoot> objects;
	private final String attribute;
	private Map<IfcRoot, List<Object>> result;

	public QueryAttribute(Set<IfcRoot> objects, String attribute) {
		this.objects = objects;
		this.attribute = attribute;
	}

	public void getAttribute(IfcRoot object) {
		
		try {
			List<Object> resList = new ArrayList<Object>();
			Class<?> newClass = Class.forName(object.getClass().getCanonicalName());
			Object newObject = newClass.cast(object);
			Method getAttribute;

			if (attribute.equals("GlobalIdObject")) {
				getAttribute = newClass.getMethod("getGlobalId");
			} else if (attribute.equals("WrappedValue")) {
				resList.add(newObject);
				result.put(object, resList);
				return;
			} else if (attribute.equals("DefaultName")) {
				getAttribute = newClass.getMethod("getName");
			} else if (attribute.equals("Package")) {
				getAttribute = newClass.getMethod("getEPackage");
			} else
				getAttribute = newClass.getMethod("get" + attribute);

			Object ret = getAttribute.invoke(newObject);
			if (ret != null) {
				if (ret instanceof EList<?>)
					resList.addAll((EList<?>) ret);
				else
					resList.add(ret);
			}
			result.put(object, resList);
		} catch (ClassNotFoundException e) {
			// Empty Block
		} catch (NoSuchMethodException e) {
			// Empty Block
		} catch (SecurityException e) {
			// Empty Block
		} catch (IllegalAccessException e) {
			// Empty Block
		} catch (IllegalArgumentException e) {
			// Empty Block
		} catch (InvocationTargetException e) {
			// Empty Block
		}
		
	}

	public Map<IfcRoot, List<Object>> getResult() {

		result = new HashMap<IfcRoot, List<Object>>();
		objects.forEach(object -> getAttribute(object));
		return result;

	}

}

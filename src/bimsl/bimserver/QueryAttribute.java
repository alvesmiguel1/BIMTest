package bimsl.bimserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
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

	public List<Object> getAttribute(IfcRoot object) {

		try {
			List<Object> resList = new ArrayList<Object>();
			Class<?> newClass = Class.forName(object.getClass().getCanonicalName());
			Object newObject = newClass.cast(object);
			Method getAttribute;

			if (attribute.equals("GlobalIdObject")) {
				getAttribute = newClass.getMethod("getGlobalId");
			} else if (attribute.equals("WrappedValue")) {
				resList.add(newObject);
				return resList;
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
			return resList;
		} catch (ClassNotFoundException e) {
			return Collections.emptyList();
		} catch (NoSuchMethodException e) {
			return Collections.emptyList();
		} catch (SecurityException e) {
			return Collections.emptyList();
		} catch (IllegalAccessException e) {
			return Collections.emptyList();
		} catch (IllegalArgumentException e) {
			return Collections.emptyList();
		} catch (InvocationTargetException e) {
			return Collections.emptyList();
		}

	}

	public void wrap(IfcRoot object) {

		List<Object> lst = getAttribute(object);
		if (!lst.isEmpty())
			result.put(object, lst);

	}

	public Map<IfcRoot, List<Object>> getResult() {

		result = new HashMap<IfcRoot, List<Object>>();
		objects.forEach(object -> wrap(object));
		return result;

	}

}

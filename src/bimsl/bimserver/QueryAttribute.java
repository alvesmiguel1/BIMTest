package bimsl.bimserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.eclipse.emf.common.util.EList;

public class QueryAttribute {

	private final List<IfcRoot> objects;
	private String attribute;

	public QueryAttribute(List<IfcRoot> objects, String attribute) {
		this.objects = objects;
		this.attribute = attribute;
	}

	public Map<IfcRoot, List<Object>> getResult() {

		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		for (IfcRoot object : objects) {

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
					continue;
				} else if (attribute.equals("DefaultName")) {
					getAttribute = newClass.getMethod("getName");
				} else if (attribute.equals("Package")) {
					getAttribute = newClass.getMethod("getEPackage");
				} else
					getAttribute = newClass.getMethod("get" + attribute);

				Object ret = getAttribute.invoke(newObject);
				if (ret instanceof EList<?>)
					resList.addAll((EList<?>) ret);
				else
					resList.add(ret);
				
				result.put(object, resList);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}

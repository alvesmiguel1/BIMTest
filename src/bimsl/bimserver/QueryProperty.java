package bimsl.bimserver;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bimserver.models.ifc2x3tc1.IfcComplexProperty;
import org.bimserver.models.ifc2x3tc1.IfcObject;
import org.bimserver.models.ifc2x3tc1.IfcProperty;
import org.bimserver.models.ifc2x3tc1.IfcPropertySet;
import org.bimserver.models.ifc2x3tc1.IfcPropertySetDefinition;
import org.bimserver.models.ifc2x3tc1.IfcPropertySingleValue;
import org.bimserver.models.ifc2x3tc1.IfcRelDefines;
import org.bimserver.models.ifc2x3tc1.IfcRelDefinesByProperties;
import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class QueryProperty {

	private final Set<IfcRoot> objects;
	private final String property;
	private Map<IfcRoot, List<Object>> result;

	public QueryProperty(Set<IfcRoot> objects, String property) {
		this.objects = objects;
		this.property = property;
	}

	public Object checkProperty(IfcPropertySingleValue ifcProperty) {

		try {
			Class<?> propClass = ifcProperty.getNominalValue().getClass();
			Class<?> newClass = Class.forName(propClass.getCanonicalName());
			Object newObject = newClass.cast(ifcProperty.getNominalValue());
			String simpleName = propClass.getSimpleName();
			if (simpleName.equals("Ifc2x3tc1Package") || simpleName.equals("Ifc2x3tc1PackageImpl"))
				return newObject;
			else
				return newClass.getMethod("getWrappedValue").invoke(newObject);

		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (NoSuchMethodException e) {
			System.err.println(e.getMessage());
		} catch (SecurityException e) {
			System.err.println(e.getMessage());
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} catch (InvocationTargetException e) {
			System.err.println(e.getMessage());
		}
		return null;

	}

	public void getProperty(IfcRoot object) {

		List<Object> resList = new ArrayList<Object>();
		if (object instanceof IfcObject) {
			for (IfcRelDefines ifcRelDefines : ((IfcObject) object).getIsDefinedBy()) {
				if (ifcRelDefines.eClass().getName().equals("IfcRelDefinesByProperties")) {
					IfcPropertySetDefinition ifcPropertySetDefinition = ((IfcRelDefinesByProperties) ifcRelDefines)
							.getRelatingPropertyDefinition();
					if (ifcPropertySetDefinition.eClass().getName().equals("IfcPropertySet")) {
						for (IfcProperty ifcProperty : ((IfcPropertySet) ifcPropertySetDefinition).getHasProperties()) {
							if (ifcProperty.getName().equals(property)) {
								String propertyClassName = ifcProperty.getClass().getSimpleName();
								if (propertyClassName.equals("IfcPropertySingleValueImpl")) {
									Object ret = checkProperty((IfcPropertySingleValue) ifcProperty);
									if (ret != null)
										resList.add(ret);
								} else if (propertyClassName.equals("IfcComplexPropertyImpl")) {
									for (IfcProperty ifcComplexProperty : ((IfcComplexProperty) ifcProperty)
											.getHasProperties()) {
										Object complexRet = checkProperty((IfcPropertySingleValue) ifcComplexProperty);
										if (complexRet != null)
											resList.add(complexRet);
									}
								}
							}
						}
					}
				}
			}
		}
		result.put(object, resList);

	}

	public Map<IfcRoot, List<Object>> getResult() {

		result = new HashMap<IfcRoot, List<Object>>();
		objects.forEach(object -> getProperty(object));
		return result;

	}

}

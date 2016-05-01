package bimsl.bimserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcComplexProperty;
import org.bimserver.models.ifc2x3tc1.IfcObject;
import org.bimserver.models.ifc2x3tc1.IfcProperty;
import org.bimserver.models.ifc2x3tc1.IfcPropertySet;
import org.bimserver.models.ifc2x3tc1.IfcPropertySetDefinition;
import org.bimserver.models.ifc2x3tc1.IfcPropertySingleValue;
import org.bimserver.models.ifc2x3tc1.IfcRelDefines;
import org.bimserver.models.ifc2x3tc1.IfcRelDefinesByProperties;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.eclipse.emf.common.util.EList;

public class QueryProperty {

	private final List<IfcRoot> objects;
	private final String property;

	public QueryProperty(List<IfcRoot> objects, String property) {
		this.objects = objects;
		this.property = property;
	}

	public Map<IfcRoot, List<Object>> getResult() {

		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		for (IfcRoot object : objects) {
			
			List<Object> foundProperties = new ArrayList<Object>();
			if (object instanceof IfcObject) {

				EList<IfcRelDefines> ifcRelDefinesList = ((IfcObject) object).getIsDefinedBy();
				for (IfcRelDefines ifcRelDefines : ifcRelDefinesList) {

					if (ifcRelDefines.eClass().getName().equals("IfcRelDefinesByProperties")) {

						IfcPropertySetDefinition ifcPropertySetDefinition = ((IfcRelDefinesByProperties) ifcRelDefines)
								.getRelatingPropertyDefinition();
						if (ifcPropertySetDefinition.eClass().getName().equals("IfcPropertySet")) {

							EList<IfcProperty> ifcPropertyList = ((IfcPropertySet) ifcPropertySetDefinition)
									.getHasProperties();
							for (IfcProperty ifcProperty : ifcPropertyList) {

								if (ifcProperty.getName().equals(property)) {

									String propertyClassName = ifcProperty.getClass().getSimpleName();
									if (propertyClassName.equals("IfcPropertySingleValueImpl")) {

										String simpleName = ((IfcPropertySingleValue) ifcProperty).getNominalValue()
												.getClass().getSimpleName();
										String canonicalName = ((IfcPropertySingleValue) ifcProperty).getNominalValue()
												.getClass().getCanonicalName();
										try {
											Class<?> newClass = Class.forName(canonicalName);
											Object newObject = newClass
													.cast(((IfcPropertySingleValue) ifcProperty).getNominalValue());

											if (simpleName.equals("Ifc2x3tc1Package")
													|| simpleName.equals("Ifc2x3tc1PackageImpl"))
												foundProperties.add(newObject);

											else {
												Method getWrappedValue = newClass.getMethod("getWrappedValue");
												Object wrappedValue = getWrappedValue.invoke(newObject);
												foundProperties.add(wrappedValue);
											}
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

									else if (propertyClassName.equals("IfcComplexPropertyImpl")) {

										EList<IfcProperty> ifcComplexPropertyList = ((IfcComplexProperty) ifcProperty)
												.getHasProperties();

										for (IfcProperty ifcComplexProperty : ifcComplexPropertyList) {

											String simpleName = ((IfcPropertySingleValue) ifcComplexProperty)
													.getNominalValue().getClass().getSimpleName();
											String canonicalName = ((IfcPropertySingleValue) ifcComplexProperty)
													.getNominalValue().getClass().getCanonicalName();

											try {
												Class<?> newClass = Class.forName(canonicalName);
												Object newObject = newClass
														.cast(((IfcPropertySingleValue) ifcComplexProperty)
																.getNominalValue());

												if (simpleName.equals("Ifc2x3tc1Package")
														|| simpleName.equals("Ifc2x3tc1PackageImpl"))
													foundProperties.add(newObject);

												else {
													Method getWrappedValue = newClass.getMethod("getWrappedValue");
													Object wrappedValue = getWrappedValue.invoke(newObject);
													foundProperties.add(wrappedValue);
												}
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
									}
								}
							}
						}
					}
				}
			}
			result.put(object, foundProperties);
		}
		return result;
	}

	/*
	 * public static List<String> getAttributes(Customer c) { return
	 * Arrays.asList(c.getName(), c.getStreet(), c.getCity()); }
	 * 
	 * public static void main(String[] args) { List<Customer> persons =
	 * getCustomers(); Map<Customer, List<String>> map = persons.stream()
	 * .collect(Collectors.toMap(Function.identity(), App::getAttributes));
	 * System.out.println(map); }
	 */

}

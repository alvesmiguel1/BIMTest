package bimsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

import bimsl.bimserver.BIMQueryEngine;

public class App {
	
	private App() {
		// Empty Block
	}
	
	public static void printObjectsAfterQuery(BIMQueryEngine bimengine, Map<IfcRoot, List<Object>> objects) {
		System.out.println("------------------------- Result ----------------------");
		objects.forEach((key, value) -> value.forEach(obj -> System.out.println(key.getGlobalId() + " " + bimengine.querySingleGlobalID((IfcRoot) obj))));
		System.out.println("-------------------------------------------------------");
	}
	
	public static void printValuesAfterQuery(BIMQueryEngine bimengine, Map<IfcRoot, List<Object>> objects) {
		System.out.println("------------------------- Result ----------------------");
		objects.forEach((key, value) -> value.forEach(obj -> System.out.println(bimengine.querySingleEntityType(key) + " " + obj)));
		System.out.println("-------------------------------------------------------");
	}
	
	public static void printValuesAfterCondition(BIMQueryEngine bimengine, Map<IfcRoot, Object> objects) {
		System.out.println("------------------------- Result ----------------------");
		objects.forEach((key, value) -> System.out.println(bimengine.querySingleEntityType(key) + " " + value));
		System.out.println("-------------------------------------------------------");
	}

	public static void main(String[] args) {
		
		// Initialization
		BIMQueryEngine bimengine = new BIMQueryEngine();
		//bimengine.addNewProject("testproj1");
		//bimengine.checkIfcFile("C:/Users/Miguel/Documents/TESE/BIMSL/Diagramas/AC11-Institute-Var-2-IFC.ifc");
		bimengine.initializeProject("testproj1");
		bimengine.refreshModel();
		
		// Examples
		System.out.println("Running Examples...");
		
		// select Obj.EntityType, Obj.Attribute.GlobalId 
		// from AllObjects 
		// where Obj.Attribute.GlobalId = 2OU4curhvDMPArGs3c5lc0
		Map<IfcRoot, List<Object>> globalIds = bimengine.queryAttribute(bimengine.getAllObjects(), "GlobalId");
		Map<IfcRoot, Object> conditionOne = bimengine.conditionEqual(globalIds, "2OU4curhvDMPArGs3c5lc0");
		App.printValuesAfterCondition(bimengine, conditionOne);
		
		// select Obj.EntityType, Obj.Property.ThermalTransmittance 
		// from AllObjects 
		// where Obj.Attribute.GlobalId = 2OU4curhvDMPArGs3c5lc0
		Map<IfcRoot, List<Object>> thermal = bimengine.queryProperty(new ArrayList<IfcRoot>(conditionOne.keySet()), "ThermalTransmittance");
		App.printValuesAfterQuery(bimengine, thermal);

		// select Obj.EntityType, Obj.Attribute.Name 
		// from AllObjects 
		// where Obj.Attribute.GlobalId = 2WnDGvXIP14xQJyJ3RQPXx
		Map<IfcRoot, Object> conditionThree = bimengine.conditionEqual(globalIds, "2WnDGvXIP14xQJyJ3RQPXx");
		Map<IfcRoot, List<Object>> objnames = bimengine.queryAttribute(new ArrayList<IfcRoot>(conditionThree.keySet()), "Name");
		App.printValuesAfterQuery(bimengine, objnames);
		
		// select Obj.EntityType, Obj.Attribute.GlobalId 
		// from AllObjects 
		// where Obj.Attribute.Name = *Level
		Map<IfcRoot, List<Object>> allobjnames = bimengine.queryAttribute(bimengine.getAllObjects(), "Name");
		Map<IfcRoot, Object> conditionFour = bimengine.conditionEqual(allobjnames, "*Level");
		Map<IfcRoot, List<Object>> idsConditionFour = bimengine.queryAttribute(new ArrayList<IfcRoot>(conditionFour.keySet()), "GlobalId");
		App.printValuesAfterQuery(bimengine, idsConditionFour);
		
		// select Obj.EntityType, Obj.Attribute.GlobalId
		// from AllObjects 
		// where Obj.EntityType = IfcSensorType
		
		//TODO
		
		// select Obj1.Attribute.GlobalId (IfcDoor), Obj2.Attribute.GlobalId (IfcBuildingStorey)
		// from AllObjects
		// where Obj1.Attribute.GlobalId = 1OWERn04z20wIPbxd7iM$J 
		// and Obj2.EntityType = IfcBuildingStorey
		// and Obj2 is directly related with Obj1
		Map<IfcRoot, Object> conditionFive = bimengine.conditionEqual(globalIds, "1OWERn04z20wIPbxd7iM$J");
		Map<IfcRoot, List<Object>> relObjects = bimengine.queryRelatedObjects(new ArrayList<IfcRoot>(conditionFive.keySet()), "IfcBuildingStorey");
		App.printObjectsAfterQuery(bimengine, relObjects);
		
	}

}

// -> queryAttribute pode devolver objecto em vez de String
// -> query de um objecto que nao existe
// -> aplicar predicado a > < >= <=
// -> select allentities for one type
// -> query devolver mais de 2 coisas: falta processo de merge ou join: considerar adicionar elementos ao final da lista de objects

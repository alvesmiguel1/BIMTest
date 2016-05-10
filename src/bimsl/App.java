package bimsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

import bimsl.bimserver.BIMQueryEngine;

public class App {
	
	private App() {
		// Empty Block
	}
	
	public static void printSingleElement(List<Object> values) {
		
		values.forEach(value -> System.out.print(value + " "));
		
	}
	
	public static void printQueryResult(Set<IfcRoot> keys, List<Map<IfcRoot, List<Object>>> result) {
		
		System.out.println("------------------------- Result ----------------------");
		keys.forEach(key -> {
			result.forEach(mapper -> App.printSingleElement(mapper.get(key)));
			System.out.println("");
		});
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
		List<Map<IfcRoot, List<Object>>> finalResult1 = new ArrayList<Map<IfcRoot, List<Object>>>();
		Map<IfcRoot, List<Object>> object1 = bimengine.queryObjectSingle("2OU4curhvDMPArGs3c5lc0");
		Map<IfcRoot, List<Object>> entity1 = bimengine.queryEntityType(object1.keySet());
		finalResult1.add(entity1);
		finalResult1.add(object1);	
		App.printQueryResult(entity1.keySet(), finalResult1);
		
		// select Obj.EntityType, Obj.Property.ThermalTransmittance 
		// from AllObjects 
		// where Obj.Attribute.GlobalId = 2OU4curhvDMPArGs3c5lc0
		List<Map<IfcRoot, List<Object>>> finalResult2 = new ArrayList<Map<IfcRoot, List<Object>>>();
		Map<IfcRoot, List<Object>> property2 = bimengine.queryProperty(object1.keySet(), "ThermalTransmittance");
		finalResult2.add(entity1);
		finalResult2.add(property2);	
		App.printQueryResult(entity1.keySet(), finalResult2);
		
		// select Obj.EntityType, Obj.Attribute.Name 
		// from AllObjects 
		// where Obj.Attribute.GlobalId = 2WnDGvXIP14xQJyJ3RQPXx
		List<Map<IfcRoot, List<Object>>> finalResult3 = new ArrayList<Map<IfcRoot, List<Object>>>();
		Map<IfcRoot, List<Object>> object3 = bimengine.queryObjectSingle("2WnDGvXIP14xQJyJ3RQPXx");
		Map<IfcRoot, List<Object>> attr3 = bimengine.queryAttribute(object3.keySet(), "Name");
		Map<IfcRoot, List<Object>> entity3 = bimengine.queryEntityType(object3.keySet());
		finalResult3.add(entity3);
		finalResult3.add(attr3);	
		App.printQueryResult(entity3.keySet(), finalResult3);
		
		// select Obj.EntityType, Obj.Attribute.GlobalId , Obj.Attribute.Name
		// from AllObjects 
		// where Obj.Attribute.Name = *Level
		List<Map<IfcRoot, List<Object>>> finalResult4 = new ArrayList<Map<IfcRoot, List<Object>>>();
		Map<IfcRoot, List<Object>> attr4 = bimengine.queryAttribute(bimengine.getAllObjects(), "Name");
		Set<IfcRoot> condition4 = bimengine.conditionEqual(attr4, "*Level");
		Map<IfcRoot, List<Object>> ids4 = bimengine.queryGlobalID(condition4);
		Map<IfcRoot, List<Object>> entity4 = bimengine.queryEntityType(condition4);
		finalResult4.add(entity4);
		finalResult4.add(attr4);
		finalResult4.add(ids4);
		App.printQueryResult(condition4, finalResult4);
		
		// select Obj1.Attribute.GlobalId (IfcDistributionControlElement), Obj2.Attribute.GlobalId (IfcSpace)
		// from AllObjects
		// where Obj1.Attribute.GlobalId = 1OWERn04z20wIPbxd7iM$J 
		// and Obj2.EntityType = IfcSpace
		// and Obj1 is directly related with Obj2
		List<Map<IfcRoot, List<Object>>> finalResult5 = new ArrayList<Map<IfcRoot, List<Object>>>();
		Map<IfcRoot, List<Object>> object5 = bimengine.queryObjectSingle("1OWERn04z20wIPbxd7iM$J");
		Set<IfcRoot> relObjects5 = bimengine.queryRelatedObjects(object5.keySet().iterator().next(), "IfcSpace");
		Map<IfcRoot, List<Object>> related5 = bimengine.relateObjects(object5.get(object5.keySet().iterator().next()), relObjects5);
		Map<IfcRoot, List<Object>> valids5 = bimengine.queryGlobalID(relObjects5);
		finalResult5.add(related5);
		finalResult5.add(valids5);
		App.printQueryResult(relObjects5, finalResult5);
		
		// select Obj.EntityType, Obj.Attribute.GlobalId
		// from AllObjects 
		// where Obj.EntityType = IfcDistributionControlElement (Sensors)
		List<Map<IfcRoot, List<Object>>> finalResult6 = new ArrayList<Map<IfcRoot, List<Object>>>();
		Set<IfcRoot> entities6 = bimengine.queryEntity("IfcDistributionControlElement");
		Map<IfcRoot, List<Object>> ent6 = bimengine.queryEntityType(entities6);
		Map<IfcRoot, List<Object>> ids6 = bimengine.queryGlobalID(entities6);
		finalResult6.add(ent6);
		finalResult6.add(ids6);
		App.printQueryResult(entities6, finalResult6);
		
	}

}

// -> queryAttribute pode devolver objecto em vez de String
// -> query de um objecto que nao existe
// -> aplicar predicado a > < >= <=s

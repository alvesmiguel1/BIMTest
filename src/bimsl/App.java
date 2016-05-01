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
	
	public static void printAfterQuery(BIMQueryEngine bimengine, Map<IfcRoot, List<Object>> objects) {
		System.out.println("------------------------- Result ----------------------");
		objects.forEach((key, value) -> value.forEach((obj) -> System.out.println(bimengine.querySingleEntityType(key) + " " + obj)));
		System.out.println("-------------------------------------------------------");
	}
	
	public static void printAfterCondition(BIMQueryEngine bimengine, Map<IfcRoot, Object> objects) {
		System.out.println("------------------------- Result ----------------------");
		objects.forEach((key, value) -> System.out.println(bimengine.querySingleEntityType(key) + " " + value));
		System.out.println("-------------------------------------------------------");
	}

	public static void main(String[] args) {
		
		System.out.println("Connecting to BIMserver...");
		BIMQueryEngine bimengine = new BIMQueryEngine();
		System.out.println("Initializing Project...");
		bimengine.initializeProject("testproj");
		System.out.println("Refreshing...");
		bimengine.refreshModel();
		System.out.println("Querying...");
		
		// Examples
		
		// select Obj.EntityType, Obj.Attribute.GlobalId 
		// from AllObjects 
		// where Obj.Attribute.GlobalId = 2pkKx0m_bEbfjzzBNbASG_
		Map<IfcRoot, List<Object>> globalIds = bimengine.queryAttribute(bimengine.getAllObjects(), "GlobalId");
		Map<IfcRoot, Object> conditionOne = bimengine.conditionEqual(globalIds, "2pkKx0m_bEbfjzzBNbASG_");
		App.printAfterCondition(bimengine, conditionOne);
		
		// select Obj.EntityType, Obj.Property.MainColor 
		// from AllObjects 
		// where Obj.Attribute.GlobalId = 2pkKx0m_bEbfjzzBNbASG_
		Map<IfcRoot, List<Object>> colorsTwo = bimengine.queryProperty(new ArrayList<IfcRoot>(conditionOne.keySet()), "MainColor");
		App.printAfterQuery(bimengine, colorsTwo);

		// select Obj.EntityType, Obj.Attribute.Name 
		// from AllObjects 
		// where Obj.Attribute.GlobalId = 2WnDGvXIP14xQJyJ3RQPXx
		Map<IfcRoot, Object> conditionThree = bimengine.conditionEqual(globalIds, "2WnDGvXIP14xQJyJ3RQPXx");
		Map<IfcRoot, List<Object>> objnames = bimengine.queryAttribute(new ArrayList<IfcRoot>(conditionThree.keySet()), "Name");
		App.printAfterQuery(bimengine, objnames);
		
		// select Obj.EntityType, Obj.Attribute.GlobalId 
		// from AllObjects 
		// where Obj.Attribute.Name = *Level
		Map<IfcRoot, List<Object>> allobjnames = bimengine.queryAttribute(bimengine.getAllObjects(), "Name");
		Map<IfcRoot, Object> conditionFour = bimengine.conditionEqual(allobjnames, "*Level");
		Map<IfcRoot, List<Object>> idsConditionFour = bimengine.queryAttribute(new ArrayList<IfcRoot>(conditionFour.keySet()), "GlobalId");
		App.printAfterQuery(bimengine, idsConditionFour);
		
		// Test: QueryRelatedObjects
		Map<IfcRoot, Object> conditionFive = bimengine.conditionEqual(globalIds, "03hhkFXKT4RPFNu5rDrZPL");
		Map<IfcRoot, List<Object>> relObjects = bimengine.queryRelatedObjects(new ArrayList<IfcRoot>(conditionFive.keySet()), "all", 3);
		App.printAfterQuery(bimengine, relObjects);
		
	}

}

// -> queryAttribute pode devolver objecto em vez de String
// -> query de um objecto que nao existe
// -> aplicar predicado a > < >= <=
// -> query devolver mais de 2 coisas: falta processo de merge ou join: considerar adicionar elementos ao final da lista de objects

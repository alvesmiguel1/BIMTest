package bimsl.bimserver.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class OrOperator {

	private final Map<IfcRoot, Object> leftOperand;
	private final Map<IfcRoot, Object> rightOperand;

	public OrOperator(Map<IfcRoot, Object> leftOperand, Map<IfcRoot, Object> rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public List<Object> getResult() {

		List<Object> result = new ArrayList<Object>();
		for (IfcRoot object : leftOperand.keySet())
			if (result.indexOf(object) == -1)
				result.add(object);
		for (IfcRoot object : rightOperand.keySet())
			if (result.indexOf(object) == -1)
				result.add(object);
		return result;
		
	}

}

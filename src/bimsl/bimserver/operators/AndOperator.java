package bimsl.bimserver.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class AndOperator {

	private final Map<IfcRoot, Object> leftOperand;
	private final Map<IfcRoot, Object> rightOperand;

	public AndOperator(Map<IfcRoot, Object> leftOperand, Map<IfcRoot, Object> rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public List<Object> getResult() {

		List<Object> result = new ArrayList<Object>();
		result.addAll(leftOperand.keySet());
		result.retainAll(rightOperand.keySet());
		return result;

	}

}

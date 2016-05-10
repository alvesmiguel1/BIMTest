package bimsl.bimserver.operators;

import java.util.HashSet;
import java.util.Set;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class AndOperator {

	private final Set<IfcRoot> leftOperand;
	private final Set<IfcRoot> rightOperand;

	public AndOperator(Set<IfcRoot> leftOperand, Set<IfcRoot> rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public Set<IfcRoot> getResult() {

		Set<IfcRoot> result = new HashSet<IfcRoot>(leftOperand);
		result.retainAll(rightOperand);
		return result;

	}

}

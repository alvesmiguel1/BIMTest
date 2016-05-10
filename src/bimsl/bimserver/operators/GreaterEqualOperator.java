package bimsl.bimserver.operators;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class GreaterEqualOperator {

	private final Map<IfcRoot, List<Object>> leftOperand;
	private final String rightOperand;
	private Set<IfcRoot> result;

	public GreaterEqualOperator(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public void checkEntry(IfcRoot key, List<Object> values) {

		if (values.stream().anyMatch(value -> value.getClass().getSimpleName().equals("Double")
				&& Double.compare((Double) value, Double.parseDouble(rightOperand)) >= 0))
			result.add(key);

	}

	public Set<IfcRoot> getResult() {

		result = new HashSet<IfcRoot>();
		leftOperand.forEach((key, values) -> checkEntry(key, values));
		return result;

	}

}

package bimsl.bimserver.operators;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class EqualOperator {

	private final Map<IfcRoot, List<Object>> leftOperand;
	private final String rightOperand;
	private Set<IfcRoot> result;

	public EqualOperator(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public boolean checkForDouble(Object value) {

		return value.getClass().getSimpleName().equals("Double")
				&& ((Double) value).equals(Double.parseDouble(rightOperand));

	}

	public boolean checkForString(Object value) {

		return value.getClass().getSimpleName().equals("String") && (((String) value).equals(rightOperand)
				|| ((String) value).matches(rightOperand.replace(".", "\\.").replace("*", ".*").replace("?", ".?")));

	}

	public void checkEntry(IfcRoot key, List<Object> values) {

		if (values.stream().anyMatch(value -> checkForString(value) || checkForDouble(value)))
			result.add(key);

	}

	public Set<IfcRoot> getResult() {

		result = new HashSet<IfcRoot>();
		leftOperand.forEach((key, values) -> checkEntry(key, values));
		return result;

	}

}

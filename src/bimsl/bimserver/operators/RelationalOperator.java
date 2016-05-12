package bimsl.bimserver.operators;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntPredicate;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class RelationalOperator {

	private final IntPredicate predicate;
	private final Map<IfcRoot, List<Object>> leftOperand;
	private final String rightOperand;	
	private Set<IfcRoot> result;

	public RelationalOperator(Map<IfcRoot, List<Object>> leftOperand, String rightOperand, IntPredicate predicate) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		this.predicate = predicate;
	}

	public void checkEntry(IfcRoot key, List<Object> values) {

		if (values.stream().anyMatch(value -> value.getClass().getSimpleName().equals("Double")
				&& predicate.test(Double.compare((Double) value, Double.parseDouble(rightOperand)))))
			result.add(key);

	}

	public Set<IfcRoot> getResult() {

		result = new HashSet<IfcRoot>();
		leftOperand.forEach((key, values) -> checkEntry(key, values));
		return result;

	}

}

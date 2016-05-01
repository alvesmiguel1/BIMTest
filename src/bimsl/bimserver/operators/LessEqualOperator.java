package bimsl.bimserver.operators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class LessEqualOperator {

	private final Map<IfcRoot, List<Object>> leftOperand;
	private final String rightOperand;

	public LessEqualOperator(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public Map<IfcRoot, Object> getResult() {

		Map<IfcRoot, Object> result = new HashMap<IfcRoot, Object>();
		for (Map.Entry<IfcRoot, List<Object>> entry : leftOperand.entrySet()) {
			IfcRoot key = entry.getKey();
			List<Object> values = entry.getValue();
			if (!values.isEmpty()) {
				int size = values.size();
				for (int i = 0; i < size; i++) {
					Object object = values.get(i);
					if (object != null) {
						String className = object.getClass().getSimpleName();
						if (className.equals("Double")) {
							if (Double.compare((Double) object, Double.parseDouble(rightOperand)) <= 0) {
								result.put(key, object);
								break;
							}
						}
					}
				}
			}
		}
		return result;

	}
	
}

package bimsl.bimserver.operators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class EqualOperator {

	private final Map<IfcRoot, List<Object>> leftOperand;
	private final String rightOperand;

	public EqualOperator(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
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
							if (object.equals(Double.parseDouble(rightOperand))) {
								result.put(key, object);
								break;
							}
						} else if (className.equals("String")) {
							if (((String) object).equals(rightOperand)) {
								result.put(key, object);
								break;
							}
							String regex = rightOperand;
							regex = regex.replace(".", "\\.").replace("*", ".*").replace("?", ".?");
							if (((String) object).matches(regex)) {
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

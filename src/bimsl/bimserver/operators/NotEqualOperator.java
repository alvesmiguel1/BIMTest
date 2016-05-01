package bimsl.bimserver.operators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bimserver.models.ifc2x3tc1.IfcRoot;

public class NotEqualOperator {

	private final Map<IfcRoot, List<Object>> leftOperand;
	private final String rightOperand;

	public NotEqualOperator(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public Map<IfcRoot, Object> getResult() {

		Map<IfcRoot, Object> result = new HashMap<IfcRoot, Object>();
		for (Map.Entry<IfcRoot, List<Object>> entry : leftOperand.entrySet()) {
			IfcRoot key = entry.getKey();
			List<Object> values = entry.getValue();
			if (!values.isEmpty()) {
				int max = values.size() - 1;
				for (int i = 0; i <= max; i++) {
					Object object = values.get(i);
					if (object != null) {
						String className = object.getClass().getSimpleName();
						if (className.equals("Double")) {
							if (object.equals(Double.parseDouble(rightOperand)))
								break;
						} else if (className.equals("String")) {
							String regex = rightOperand;
							regex = regex.replace(".", "\\.");
							regex = regex.replace("*", ".*");
							regex = regex.replace("?", ".?");
							if (((String) object).matches(regex))
								break;
						}
					} else if (i == max)
						result.put(key, object);
				}
			} else
				result.put(key, null);
		}
		return result;

	}

}

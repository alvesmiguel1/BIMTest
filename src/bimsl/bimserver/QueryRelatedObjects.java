package bimsl.bimserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.eclipse.emf.common.util.EList;

public class QueryRelatedObjects {

	private final boolean all;
	private final IfcRoot ifcRootObject;
	private final String type;
	private boolean noMaxDepth;
	private int currentDepth;
	private int depthIncrease;
	private int maxDepth;
	private List<Object> result;
	private Queue<Object> queue;
	private Set<Object> nodes;

	public QueryRelatedObjects(IfcRoot ifcRootObject, String type, int maxDepth) {
		this.ifcRootObject = ifcRootObject;
		this.type = type + "Impl";
		this.all = type.equals("all") ? true : false;
		this.noMaxDepth = maxDepth == -1 ? true : false;
		this.currentDepth = 0;
		this.depthIncrease = 1;
		this.maxDepth = maxDepth;
		this.nodes = new HashSet<Object>();
		this.result = new ArrayList<Object>();
		this.queue = new LinkedList<Object>();
	}

	public void visit(Object object) {

		depthIncrease--;
		if (!nodes.add(object))
			return;

		if (all || object.getClass().getSimpleName().equals(type)) {
			result.add(object);
			if (noMaxDepth) {
				maxDepth = currentDepth;
				noMaxDepth = false;
			}
		}

		if (noMaxDepth || currentDepth < maxDepth) {

			try {
				Class<?> newClass = Class.forName(object.getClass().getCanonicalName());
				Object newObject = newClass.cast(object);
				Method[] methods = newClass.getMethods();
				List<Method> methodlist = Arrays.asList(methods);

				methodlist.forEach(method -> {
					if (method.getName().startsWith("get")
							&& (method.getReturnType().getSimpleName().startsWith("Ifc")
									|| method.getReturnType().getSimpleName().startsWith("EList"))
							&& method.getParameterCount() == 0 && !method.getName().equals("getModel")) {
						
						try {
							Object ret = method.invoke(newObject);
							if (ret != null) {
								if (ret instanceof EList<?>)
									queue.addAll((EList<?>) ret);
								else
									queue.add(ret);
							}
						} catch (IllegalAccessException e) {
							// Empty Block
						} catch (InvocationTargetException e) {
							// Empty Block
						}
					}
				});
				
			} catch (ClassNotFoundException e) {
				return;
			}
		}

	}

	public List<Object> getResult() {

		visit(ifcRootObject);
		while (!queue.isEmpty()) {
			if (depthIncrease == 0) {
				depthIncrease = queue.size();
				currentDepth++;
			}
			if (noMaxDepth || currentDepth <= maxDepth)
				visit(queue.poll());
			else
				break;
		}
		return result;

	}

}

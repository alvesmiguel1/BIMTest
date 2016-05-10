package bimsl.bimserver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bimserver.client.json.JsonBimServerClientFactory;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.emf.MetaDataManager;
import org.bimserver.interfaces.objects.SDeserializerPluginConfiguration;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.plugins.PluginException;
import org.bimserver.plugins.PluginManager;
import org.bimserver.plugins.services.BimServerClientException;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.shared.BimServerClientFactory;
import org.bimserver.shared.ChannelConnectionException;
import org.bimserver.shared.PublicInterfaceNotFoundException;
import org.bimserver.shared.UsernamePasswordAuthenticationInfo;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.ServiceException;
import org.bimserver.shared.exceptions.UserException;

import bimsl.bimserver.operators.AndOperator;
import bimsl.bimserver.operators.EqualOperator;
import bimsl.bimserver.operators.GreaterEqualOperator;
import bimsl.bimserver.operators.GreaterOperator;
import bimsl.bimserver.operators.LessEqualOperator;
import bimsl.bimserver.operators.LessOperator;
import bimsl.bimserver.operators.NotEqualOperator;
import bimsl.bimserver.operators.OrOperator;

public class BIMQueryEngine {

	private BimServerClientInterface client;
	private IfcModelInterface model;
	private Set<IfcRoot> allObjects;
	private long poid;
	private SProject project;

	public BIMQueryEngine() {
		System.out.println("Connecting to BIMserver...");
		try {
			// Home directory definition
			File home = new File("home");
			if (!home.exists()) {
				home.mkdir();
			}

			// Plugin Manager initialization
			PluginManager pluginManager = new PluginManager(new File(home, "tmp"),
					System.getProperty("java.class.path"), null, null, null);

			// Load all plugins available on the classpath
			pluginManager.loadPluginsFromCurrentClassloader();

			// Initialize all loaded plugins
			pluginManager.initAllLoadedPlugins();

			// Metadata Manager initialization
			MetaDataManager metaDataManager = new MetaDataManager(pluginManager);
			pluginManager.setMetaDataManager(metaDataManager);
			metaDataManager.init();

			// Create a factory for BimServerClients, connect via JSON
			BimServerClientFactory factory = new JsonBimServerClientFactory(metaDataManager, "http://localhost:8080");

			// Create a new client, with given authorization
			client = factory.create(new UsernamePasswordAuthenticationInfo("admin@bimserver.org", "admin"));

		} catch (PluginException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (ChannelConnectionException e) {
			e.printStackTrace();
		}
	}

	public boolean addNewProject(String projectname) {
		System.out.println("Adding New Project...");
		try {
			project = client.getBimsie1ServiceInterface().addProject(projectname, "ifc2x3tc1");
			poid = project.getOid();
			return true;
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (UserException e) {
			e.printStackTrace();
		} catch (PublicInterfaceNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean checkIfcFile(String fullpath) {
		System.out.println("Checking IFC File...");
		try {
			// Look for a deserializer
			SDeserializerPluginConfiguration deserializer = client.getBimsie1ServiceInterface()
					.getSuggestedDeserializerForExtension("ifc", poid);
			// Checking IFC file
			client.checkin(poid, project.getName(), deserializer.getOid(), false, true, new File(fullpath));
			return refreshProject();
		} catch (UserException e) {
			e.printStackTrace();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PublicInterfaceNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Set<IfcRoot> conditionAnd(Set<IfcRoot> leftOperand, Set<IfcRoot> rightOperand) {
		AndOperator andOperator = new AndOperator(leftOperand, rightOperand);
		return andOperator.getResult();
	}

	public Set<IfcRoot> conditionEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		EqualOperator equalOperator = new EqualOperator(leftOperand, rightOperand);
		return equalOperator.getResult();
	}

	public Set<IfcRoot> conditionGreaterEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		GreaterEqualOperator greaterEqualOperator = new GreaterEqualOperator(leftOperand, rightOperand);
		return greaterEqualOperator.getResult();
	}

	public Set<IfcRoot> conditionGreater(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		GreaterOperator greaterOperator = new GreaterOperator(leftOperand, rightOperand);
		return greaterOperator.getResult();
	}

	public Set<IfcRoot> conditionLessEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		LessEqualOperator lessEqualOperator = new LessEqualOperator(leftOperand, rightOperand);
		return lessEqualOperator.getResult();
	}

	public Set<IfcRoot> conditionLess(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		LessOperator lessOperator = new LessOperator(leftOperand, rightOperand);
		return lessOperator.getResult();
	}

	public Set<IfcRoot> conditionNotEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		NotEqualOperator notEqualOperator = new NotEqualOperator(leftOperand, rightOperand);
		return notEqualOperator.getResult();
	}

	public Set<IfcRoot> conditionOr(Set<IfcRoot> leftOperand, Set<IfcRoot> rightOperand) {
		OrOperator orOperator = new OrOperator(leftOperand, rightOperand);
		return orOperator.getResult();
	}

	public Set<IfcRoot> getAllObjects() {
		return allObjects;
	}

	public List<SProject> getAllProjects() {
		try {
			return client.getBimsie1ServiceInterface().getAllProjects(true, true);
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (UserException e) {
			e.printStackTrace();
		} catch (PublicInterfaceNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public IfcModelInterface getModel() {
		return model;
	}

	public SProject getProject() {
		return project;
	}

	public SProject getProjectByName(String name) {
		try {
			List<SProject> projects = client.getBimsie1ServiceInterface().getProjectsByName(name);
			if (projects.size() == 0)
				return null;
			return projects.get(0);
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (UserException e) {
			e.printStackTrace();
		} catch (PublicInterfaceNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SProject getProjectByPoid(Long poid) {
		try {
			return client.getBimsie1ServiceInterface().getProjectByPoid(poid);
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (UserException e) {
			e.printStackTrace();
		} catch (PublicInterfaceNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getProjectObjectID() {
		return poid;
	}

	public boolean initializeProject(String projectname) {
		System.out.println("Initializing Project...");
		project = getProjectByName(projectname);
		poid = project.getOid();
		return true;
	}

	public Map<IfcRoot, List<Object>> queryAttribute(Set<IfcRoot> objects, String attribute) {
		QueryAttribute query = new QueryAttribute(objects, attribute);
		return query.getResult();
	}

	public Map<IfcRoot, List<Object>> queryAttributeSingle(IfcRoot object, String attribute) {
		Set<IfcRoot> objects = new HashSet<IfcRoot>();
		objects.add(object);
		QueryAttribute query = new QueryAttribute(objects, attribute);
		return query.getResult();
	}

	@SuppressWarnings("unchecked")
	public Set<IfcRoot> queryEntity(String entity) {
		Set<IfcRoot> result = new HashSet<IfcRoot>();
		try {
			Class<IfcRoot> cls = IfcRoot.class;
			Class<?> entityClass = Class.forName("org.bimserver.models.ifc2x3tc1." + entity);
			if (cls.isAssignableFrom(entityClass))
				result.addAll(model.getAllWithSubTypes((Class<? extends IfcRoot>) entityClass));
		} catch (ClassNotFoundException e) {
			// Empty Block
		}
		return result;
	}

	public Map<IfcRoot, List<Object>> queryEntityType(Set<IfcRoot> entities) {
		QueryEntityType query = new QueryEntityType(entities);
		return query.getResult();
	}

	public Map<IfcRoot, List<Object>> queryEntityTypeSingle(IfcRoot entity) {
		Set<IfcRoot> entities = new HashSet<IfcRoot>();
		entities.add(entity);
		QueryEntityType query = new QueryEntityType(entities);
		return query.getResult();
	}

	public Map<IfcRoot, List<Object>> queryGlobalID(Set<IfcRoot> objects) {
		QueryGlobalID query = new QueryGlobalID(objects);
		return query.getResult();
	}

	public Map<IfcRoot, List<Object>> queryGlobalIDSingle(IfcRoot object) {
		Set<IfcRoot> objects = new HashSet<IfcRoot>();
		objects.add(object);
		QueryGlobalID query = new QueryGlobalID(objects);
		return query.getResult();
	}

	public Map<IfcRoot, List<Object>> queryProperty(Set<IfcRoot> objects, String property) {
		QueryProperty query = new QueryProperty(objects, property);
		return query.getResult();
	}

	public Map<IfcRoot, List<Object>> queryPropertySingle(IfcRoot object, String property) {
		Set<IfcRoot> objects = new HashSet<IfcRoot>();
		objects.add(object);
		QueryProperty query = new QueryProperty(objects, property);
		return query.getResult();
	}

	public Map<IfcRoot, List<Object>> queryObject(Set<String> globalIDs) {
		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		globalIDs.forEach(globalID -> {
			IfcRoot object = model.getByGuid(globalID);
			if (object != null)
				result.put(object, Arrays.asList(globalID));
		});
		return result;
	}

	public Map<IfcRoot, List<Object>> queryObjectSingle(String globalID) {
		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		IfcRoot object = model.getByGuid(globalID);
		if (object != null)
			result.put(object, Arrays.asList(globalID));
		return result;
	}

	public Set<IfcRoot> queryRelatedObjects(IfcRoot object, String type) {
		QueryRelatedObjects query = new QueryRelatedObjects(object, type, -1);
		return query.getResult();
	}

	public Set<IfcRoot> queryRelatedObjectsWithDepth(IfcRoot object, String type, int depth) {
		QueryRelatedObjects query = new QueryRelatedObjects(object, type, depth);
		return query.getResult();
	}

	public boolean refreshProject() {
		System.out.println("Refreshing Project...");
		try {
			project = client.getBimsie1ServiceInterface().getProjectByPoid(poid);
			return refreshModel();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (UserException e) {
			e.printStackTrace();
		} catch (PublicInterfaceNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean refreshModel() {
		System.out.println("Refreshing Model...");
		try {
			// Load model without lazy loading (complete model at once)
			model = client.getModel(project, project.getLastRevisionId(), true, false);
			allObjects = new HashSet<IfcRoot>(model.getAllWithSubTypes(IfcRoot.class));
			return true;
		} catch (UserException e) {
			e.printStackTrace();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (BimServerClientException e) {
			e.printStackTrace();
		} catch (PublicInterfaceNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Map<IfcRoot, List<Object>> relateObjects(List<Object> objects, Set<IfcRoot> related) {
		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		related.forEach(key -> result.put(key, objects));
		return result;
	}

}

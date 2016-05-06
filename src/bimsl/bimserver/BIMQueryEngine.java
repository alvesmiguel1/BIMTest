package bimsl.bimserver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private List<IfcRoot> allObjects;
	private long poid;
	private SProject project;

	public BIMQueryEngine() {
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
	
	public List<Object> conditionAnd(Map<IfcRoot, Object> leftOperand, Map<IfcRoot, Object> rightOperand) {
		AndOperator andOperator = new AndOperator(leftOperand, rightOperand);
		return andOperator.getResult();
	}
	
	public Map<IfcRoot, Object> conditionEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		EqualOperator equalOperator = new EqualOperator(leftOperand, rightOperand);
		return equalOperator.getResult();
	}
	
	public Map<IfcRoot, Object> conditionGreaterEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		GreaterEqualOperator greaterEqualOperator = new GreaterEqualOperator(leftOperand, rightOperand);
		return greaterEqualOperator.getResult();
	}
	
	public Map<IfcRoot, Object> conditionGreater(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		GreaterOperator greaterOperator = new GreaterOperator(leftOperand, rightOperand);
		return greaterOperator.getResult();
	}
	
	public Map<IfcRoot, Object> conditionLessEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		LessEqualOperator lessEqualOperator = new LessEqualOperator(leftOperand, rightOperand);
		return lessEqualOperator.getResult();
	}
	
	public Map<IfcRoot, Object> conditionLess(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		LessOperator lessOperator = new LessOperator(leftOperand, rightOperand);
		return lessOperator.getResult();
	}
	
	public Map<IfcRoot, Object> conditionNotEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		NotEqualOperator notEqualOperator = new NotEqualOperator(leftOperand, rightOperand);
		return notEqualOperator.getResult();
	}
	
	public List<Object> conditionOr(Map<IfcRoot, Object> leftOperand, Map<IfcRoot, Object> rightOperand) {
		OrOperator orOperator = new OrOperator(leftOperand, rightOperand);
		return orOperator.getResult();
	}

	public List<IfcRoot> getAllObjects() {
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
		project = getProjectByName(projectname);
		poid = project.getOid();
		return true;
	}
	
	public Map<IfcRoot, List<Object>> queryAttribute(List<IfcRoot> objects, String attribute) {
		QueryAttribute query = new QueryAttribute(objects, attribute);
		return query.getResult();
	}
	
	public List<String> queryEntityType(List<IfcRoot> entities) {
		QueryEntityType query = new QueryEntityType(entities);
		return query.getResult();
	}
	
	public Map<IfcRoot, List<Object>> queryProperty(List<IfcRoot> objects, String property) {
		QueryProperty query = new QueryProperty(objects, property);
		return query.getResult();
	}
	
	public Map<IfcRoot, List<Object>> queryRelatedObjects(List<IfcRoot> objects, String type) {
		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		for (IfcRoot object : objects) {
			QueryRelatedObjects query = new QueryRelatedObjects(object, type, -1);
			result.put(object, query.getResult());
		}
		return result;
	}
	
	public Map<IfcRoot, List<Object>> queryRelatedObjectsWithDepth(List<IfcRoot> objects, String type, int depth) {
		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		for (IfcRoot object : objects) {
			QueryRelatedObjects query = new QueryRelatedObjects(object, type, depth);
			result.put(object, query.getResult());
		}
		return result;
	}
	
	public String querySingleEntityType(IfcRoot entity) {
		QueryEntityType query = new QueryEntityType(entity);
		return query.getResult().get(0);
	}

	public boolean refreshProject() {
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
		try {
			// Load model without lazy loading (complete model at once)
			model = client.getModel(project, project.getLastRevisionId(), true, false);
			allObjects = model.getAllWithSubTypes(IfcRoot.class);
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

}

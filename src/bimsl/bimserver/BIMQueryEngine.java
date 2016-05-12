package bimsl.bimserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.IntPredicate;

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
import org.eclipse.emf.ecore.EClass;

import bimsl.bimserver.exception.BIMServerConnectionException;
import bimsl.bimserver.exception.ModelRefreshException;
import bimsl.bimserver.exception.ProjectAddException;
import bimsl.bimserver.exception.ProjectAlreadyExistsException;
import bimsl.bimserver.exception.ProjectCheckException;
import bimsl.bimserver.exception.ProjectGetException;
import bimsl.bimserver.exception.ProjectNotFoundException;
import bimsl.bimserver.exception.ProjectRefreshException;
import bimsl.bimserver.exception.ProjectRemoveException;
import bimsl.bimserver.exception.PropertiesLoadingException;
import bimsl.bimserver.operators.AndOperator;
import bimsl.bimserver.operators.EqualityOperator;
import bimsl.bimserver.operators.OrOperator;
import bimsl.bimserver.operators.RelationalOperator;

public class BIMQueryEngine {

	private BimServerClientInterface client;
	private IfcModelInterface model;
	private Set<IfcRoot> allObjects;
	private long poid;
	private SProject project;

	public BIMQueryEngine() throws BIMServerConnectionException, PropertiesLoadingException {

		try {
			System.out.println("Loading properties...");

			// Property loading process
			Properties props = new Properties();
			InputStream input = new FileInputStream("conf/bimserver.properties");
			props.load(input);
			input.close();

			System.out.println("Connecting to BIMserver...");

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
			BimServerClientFactory factory = new JsonBimServerClientFactory(metaDataManager,
					"http://localhost:" + props.getProperty("port"));

			// Create a new client, with given authorization
			client = factory.create(new UsernamePasswordAuthenticationInfo(props.getProperty("username"),
					props.getProperty("password")));

		} catch (PluginException e) {
			throw new BIMServerConnectionException();
		} catch (ServiceException e) {
			throw new BIMServerConnectionException();
		} catch (ChannelConnectionException e) {
			throw new BIMServerConnectionException();
		} catch (FileNotFoundException e) {
			throw new PropertiesLoadingException();
		} catch (IOException e) {
			throw new PropertiesLoadingException();
		}
	}

	public void addNewProject(String projectname) throws ProjectAddException, ProjectAlreadyExistsException {
		System.out.println("Adding a new project...");
		try {
			project = client.getBimsie1ServiceInterface().addProject(projectname, "ifc2x3tc1");
			poid = project.getOid();
		} catch (ServerException e) {
			throw new ProjectAddException(projectname);
		} catch (UserException e) {
			throw new ProjectAlreadyExistsException(projectname);
		} catch (PublicInterfaceNotFoundException e) {
			throw new ProjectAddException(projectname);
		}
	}

	public void checkIfcFile(String filename) throws ProjectCheckException, ProjectRefreshException, ModelRefreshException {
		System.out.println("Checking IFC file...");
		try {
			// Look for a deserializer
			SDeserializerPluginConfiguration deserializer = client.getBimsie1ServiceInterface()
					.getSuggestedDeserializerForExtension("ifc", poid);
			// Checking IFC file
			client.checkin(poid, project.getName(), deserializer.getOid(), false, true, new File("conf/" + filename));
			refreshProject();
		} catch (UserException e) {
			throw new ProjectCheckException(filename);
		} catch (ServerException e) {
			throw new ProjectCheckException(filename);
		} catch (IOException e) {
			throw new ProjectCheckException(filename);
		} catch (PublicInterfaceNotFoundException e) {
			throw new ProjectCheckException(filename);
		}
	}

	public Set<IfcRoot> conditionAnd(Set<IfcRoot> leftOperand, Set<IfcRoot> rightOperand) {
		AndOperator andOperator = new AndOperator(leftOperand, rightOperand);
		return andOperator.getResult();
	}

	public Set<IfcRoot> conditionEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		EqualityOperator equalOperator = new EqualityOperator(leftOperand, rightOperand, true);
		return equalOperator.getResult();
	}

	public Set<IfcRoot> conditionGreaterEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		IntPredicate predicate = (i) -> i >= 0;
		RelationalOperator greaterEqualOperator = new RelationalOperator(leftOperand, rightOperand, predicate);
		return greaterEqualOperator.getResult();
	}

	public Set<IfcRoot> conditionGreater(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		IntPredicate predicate = (i) -> i > 0;
		RelationalOperator greaterOperator = new RelationalOperator(leftOperand, rightOperand, predicate);
		return greaterOperator.getResult();
	}

	public Set<IfcRoot> conditionLessEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		IntPredicate predicate = (i) -> i <= 0;
		RelationalOperator lessEqualOperator = new RelationalOperator(leftOperand, rightOperand, predicate);
		return lessEqualOperator.getResult();
	}

	public Set<IfcRoot> conditionLess(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		IntPredicate predicate = (i) -> i < 0;
		RelationalOperator lessOperator = new RelationalOperator(leftOperand, rightOperand, predicate);
		return lessOperator.getResult();
	}

	public Set<IfcRoot> conditionNotEqual(Map<IfcRoot, List<Object>> leftOperand, String rightOperand) {
		EqualityOperator notEqualOperator = new EqualityOperator(leftOperand, rightOperand, false);
		return notEqualOperator.getResult();
	}

	public Set<IfcRoot> conditionOr(Set<IfcRoot> leftOperand, Set<IfcRoot> rightOperand) {
		OrOperator orOperator = new OrOperator(leftOperand, rightOperand);
		return orOperator.getResult();
	}

	public Set<IfcRoot> getAllObjects() {
		return allObjects;
	}

	public List<String> getAllProjects() throws ProjectGetException {
		try {
			List<String> projects = new ArrayList<String>();
			client.getBimsie1ServiceInterface().getAllProjects(true, true)
					.forEach(project -> projects.add(project.getName()));
			return projects;
		} catch (ServerException e) {
			throw new ProjectGetException();
		} catch (UserException e) {
			throw new ProjectGetException();
		} catch (PublicInterfaceNotFoundException e) {
			throw new ProjectGetException();
		}
	}

	public IfcModelInterface getModel() {
		return model;
	}

	public SProject getProject() {
		return project;
	}

	public SProject getProjectByName(String name) throws ProjectGetException, ProjectNotFoundException {
		try {
			List<SProject> projects = client.getBimsie1ServiceInterface().getProjectsByName(name);
			if (projects.isEmpty())
				throw new ProjectNotFoundException();
			return projects.get(0);
		} catch (ServerException e) {
			throw new ProjectGetException();
		} catch (UserException e) {
			throw new ProjectGetException();
		} catch (PublicInterfaceNotFoundException e) {
			throw new ProjectGetException();
		}
	}

	public SProject getProjectByPoid(Long poid) throws ProjectGetException {
		try {
			return client.getBimsie1ServiceInterface().getProjectByPoid(poid);
		} catch (ServerException e) {
			throw new ProjectGetException();
		} catch (UserException e) {
			throw new ProjectGetException();
		} catch (PublicInterfaceNotFoundException e) {
			throw new ProjectGetException();
		}
	}

	public long getProjectObjectID() {
		return poid;
	}

	public void initializeProject(String projectname) throws ProjectGetException, ProjectNotFoundException {
		System.out.println("Initializing project...");
		project = getProjectByName(projectname);
		poid = project.getOid();
	}

	public Map<IfcRoot, List<Object>> join(List<Object> objects, Set<IfcRoot> related) {
		Map<IfcRoot, List<Object>> result = new HashMap<IfcRoot, List<Object>>();
		related.forEach(key -> result.put(key, objects));
		return result;
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

	public Set<IfcRoot> queryEntity(String entity) {
		Set<IfcRoot> result = new HashSet<IfcRoot>();
		EClass clazz = model.getPackageMetaData().getEClass(entity);
		if (clazz != null)
			result.addAll(model.getAllWithSubTypes(clazz));
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

	public void refreshProject() throws ProjectRefreshException, ModelRefreshException {
		try {
			project = getProjectByPoid(poid);
			refreshModel();
		} catch (ProjectGetException e) {
			throw new ProjectRefreshException();
		}
	}

	public void refreshModel() throws ModelRefreshException {
		System.out.println("Refreshing model...");
		try {
			// Load model without lazy loading (complete model at once)
			model = client.getModel(project, project.getLastRevisionId(), true, false);
			allObjects = new HashSet<IfcRoot>(model.getAllWithSubTypes(IfcRoot.class));
		} catch (UserException e) {
			throw new ModelRefreshException();
		} catch (ServerException e) {
			throw new ModelRefreshException();
		} catch (BimServerClientException e) {
			throw new ModelRefreshException();
		} catch (PublicInterfaceNotFoundException e) {
			throw new ModelRefreshException();
		}
	}

	public void removeProject() throws ProjectRemoveException {
		System.out.println("Removing the current working project...");
		try {
			client.getBimsie1ServiceInterface().deleteProject(poid);
		} catch (ServerException e) {
			throw new ProjectRemoveException();
		} catch (UserException e) {
			throw new ProjectRemoveException();
		} catch (PublicInterfaceNotFoundException e) {
			throw new ProjectRemoveException();
		}
	}

}

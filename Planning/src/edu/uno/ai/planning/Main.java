package edu.uno.ai.planning;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.uno.ai.planning.io.Parser;

/**
 * Processes command line arguments, runs the planner, or prints usage
 * information.
 * 
 * @author Stephen G. Ware
 */
public class Main {

	/**
	 * Processes command line arguments, runs the planner, or prints usage
	 * information.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an exception occurs
	 */
	public static void main(String[] args) throws Exception {
		Parser parser = new Parser();
		if(args.length < 5 || !args[0].endsWith(".jar") || !hasKey("-d", args) || !hasKey("-p", args))
			fail();
		Planner<?> planner = loadPlanner(args[0]);
		System.out.println("Planner:  " + planner.name);
		Domain domain = parser.parse(new File(getValue("-d", args)), Domain.class);
		System.out.println("Domain:   " + domain.name);
		Problem problem = parser.parse(new File(getValue("-p", args)), Problem.class);
		System.out.println("Problem:  " + problem.name);
		int nodeLimit = Planner.NO_NODE_LIMIT;
		if(hasKey("-nl", args))
			nodeLimit = Integer.parseInt(getValue("-nl", args));
		long timeLimit = Planner.NO_TIME_LIMIT;
		if(hasKey("-tl", args))
			timeLimit = Long.parseLong(getValue("-tl", args));
		Result result = planner.findSolutuion(problem, nodeLimit, timeLimit);
		System.out.print("Result:   ");
		if(result.success)
			System.out.println("success");
		else if(result.reason != null)
			System.out.println(result.reason);
		else
			System.out.println("failure");
		System.out.println("Visited:  " + result.visited);
		System.out.println("Expanded: " + result.expanded);
		System.out.println("Time:     " + result.getTime());
		if(result.success) {
			System.out.println("Solution: ");
			for(Step step : result.solution)
				System.out.println("  " + step);
		}
	}
	
	private static final String USAGE = "Usage: java -jar Planning.jar <plugin> -d <domain> -p <problem> [-nl <nodes>] [-tl <millis>]\n" +
			"Where:\n" + 
			"  <plugin> is a jar file containing one subclass of " + Planner.class.getName() + "\n" +
			"  <domain> and <problem> are files in PDDL-like format\n" +
			"  <nodes> is the optional max number of nodes to visit\n" +
			"  <millis> is the optional max number of milliseconds to search";
	
	private static final void fail() {
		System.out.println(USAGE);
		System.exit(1);
	}
	
	private static final boolean hasKey(String arg, String[] args) {
		for(int i=0; i<args.length; i++) {
			if(args[i].equals(arg))
				return true;
		}
		return false;
	}
	
	private static final String getValue(String arg, String[] args) {
		for(int i=0; i<args.length; i++) {
			if(args[i].equals(arg) && args.length >= i)
				return args[i + 1];
		}
		throw new RuntimeException("Expected value after " + arg);
	}
	
	@SuppressWarnings("unchecked")
	private static final Planner<?> loadPlanner(String jarURL) throws Exception {
		ClassLoader loader = URLClassLoader.newInstance(new URL[]{ new File(jarURL).toURI().toURL() }, Main.class.getClassLoader());
		ZipInputStream zip = null;
		try {
			zip = new ZipInputStream(new FileInputStream(jarURL));
			for(ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
				if(entry.getName().endsWith(".class") && !entry.isDirectory()) {
					StringBuilder className = new StringBuilder();
			    	for(String part : entry.getName().split("/")) {
			        	if(className.length() != 0)
			        		className.append(".");
			        	className.append(part);
			            if(part.endsWith(".class"))
			            	className.setLength(className.length() - ".class".length());
			        }
			        Class<?> c = Class.forName(className.toString(), true, loader);
			        if(Planner.class.isAssignableFrom(c)) {
						Constructor<? extends Planner<?>> constructor = ((Class<? extends Planner<?>>) c).getConstructor();
						zip.close();
						return constructor.newInstance();
			        }
			    }
			}
			throw new RuntimeException("No subclass of " + Planner.class.getName() + " found");
		}
		finally {
			if(zip != null)
				zip.close();
		}
	}
}

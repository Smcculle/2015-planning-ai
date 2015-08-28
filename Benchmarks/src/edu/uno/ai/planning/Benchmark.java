package edu.uno.ai.planning;

import java.io.File;
import java.io.IOException;

import edu.uno.ai.planning.Domain;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.io.Parser;

public class Benchmark {

	public final String domain;
	public final String problem;
	
	public Benchmark(String domain, String problem) {
		this.domain = domain;
		this.problem = problem;
	}
	
	public Problem getProblem() throws IOException {
		Parser parser = new Parser();
		parser.parse(new File("benchmarks/" + domain + ".pddl"), Domain.class);
		return parser.parse(new File("benchmarks/" + problem + ".pddl"), Problem.class);
	}
}

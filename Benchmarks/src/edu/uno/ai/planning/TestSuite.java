package edu.uno.ai.planning;

import edu.uno.ai.planning.Planner;

public class TestSuite {

	public static final int NODE_LIMIT = 10000;
	public static final long TIME_LIMIT = Planner.NO_TIME_LIMIT;

	public static final Planner<?>[] PLANNERS = new Planner[]{
		new edu.uno.ai.planning.blackbox.BlackboxPlan(),
		// new edu.uno.ai.planning.gp.GraphPlan(),
		/*
		new edu.uno.ai.planning.bfs.BFSPlanner(),
		new edu.uno.ai.planning.iw.IteratedWidthPlanner(),
		new edu.uno.ai.planning.pop.PartialOrderPlanner(),
		new edu.uno.ai.planning.spop.PartialOrderPlanner(),
		new edu.uno.ai.planning.graphplan.Graphplan(),
		new edu.uno.ai.planning.hsp.HSPlanner(),
		new edu.uno.ai.planning.shsp.HeuristicSearchPlanner(),
		new edu.uno.ai.planning.ff.FastForwardPlanner(),
		*/
	};
	
	public static final Benchmark[] BENCHMARKS = new Benchmark[]{
		// new Benchmark("blocks", "do_nothing"),
		// new Benchmark("blocks", "easy_stack"),
		new Benchmark("blocks", "easy_unstack"),
		// new Benchmark("blocks", "sussman"),
		/*
		new Benchmark("cake", "have_eat_cake"),
		new Benchmark("cake", "have_eat_cake"),
		new Benchmark("blocks", "reverse_2"),
		new Benchmark("blocks", "reverse_4"),
		new Benchmark("blocks", "reverse_6"),
		new Benchmark("blocks", "reverse_8"),
		new Benchmark("blocks", "reverse_10"),
		new Benchmark("blocks", "reverse_12"),
		new Benchmark("cargo", "deliver_1"),
		new Benchmark("cargo", "deliver_2"),
		new Benchmark("cargo", "deliver_3"),
		new Benchmark("cargo", "deliver_4"),
		new Benchmark("cargo", "deliver_return_1"),
		new Benchmark("cargo", "deliver_return_2"),
		new Benchmark("cargo", "deliver_return_3"),
		new Benchmark("cargo", "deliver_return_4"),
		new Benchmark("wumpus", "easy_wumpus"),
		new Benchmark("wumpus", "medium_wumpus"),
		new Benchmark("wumpus", "hard_wumpus"),
		*/
	};
}

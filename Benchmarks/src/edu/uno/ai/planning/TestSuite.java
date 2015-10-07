package edu.uno.ai.planning;

import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.bfs.BFSPlanner;
import edu.uno.ai.planning.pop.PartialOrderPlanner;

public class TestSuite {

	public static final int NODE_LIMIT = 10000;
	public static final long TIME_LIMIT = Planner.NO_TIME_LIMIT;
	
	public static final Planner<?>[] PLANNERS = new Planner[]{
		new BFSPlanner(),
		new PartialOrderPlanner(),
	};
	
	public static final Benchmark[] BENCHMARKS = new Benchmark[]{
		new Benchmark("blocks", "do_nothing"),
		new Benchmark("blocks", "easy_stack"),
		new Benchmark("blocks", "easy_unstack"),
		new Benchmark("blocks", "sussman"),
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
	};
}

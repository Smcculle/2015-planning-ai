package edu.uno.ai.planning.SATPlan;

import java.util.List;

public interface ISATSolver {
	List<BooleanVariable> getModel(SATProblem problem);
}

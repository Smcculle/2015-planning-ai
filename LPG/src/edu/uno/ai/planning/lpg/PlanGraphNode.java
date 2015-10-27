package edu.uno.ai.planning.lpg;

import java.util.List;

public interface PlanGraphNode {

	public int getInitialLevel();

	void setInitialLevel(int initialLevel);
	
	boolean existsAtLevel(int level);
	
	public List<?> getParentNodes();
	
	public List<?> getChildNodes();
	
}

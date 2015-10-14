package edu.uno.ai.planning.graphplan;

import java.util.List;

public interface PlanGraphNode {

	public int getInitialLevel();

	void setInitialLevel(int initialLevel);
	
	public List<?> getParentNodes();
	
	public List<?> getChildNodes();
	
}

package edu.uno.ai.planning.ff;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Planner;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.ss.StateSpaceNode;
import edu.uno.ai.planning.ss.StateSpaceProblem;
import edu.uno.ai.planning.ss.StateSpaceSearch;

public class FastForwardSearch extends StateSpaceSearch {
	
	private int visited   = 0;
	private int expanded  = 0;
	private int nodeLimit = Planner.NO_NODE_LIMIT;

	public FastForwardSearch(StateSpaceProblem problem) {
		super(problem);
	}

	@Override
	public int countVisited() {
		return visited;
	}

	@Override
	public int countExpanded() {
		return expanded;
	}

	@Override
	public void setNodeLimit(int limit) {
		nodeLimit = limit;
		
	}

	@Override
	public Plan findNextSolution() {
		StateSpaceNode bestNode = root;
		int hValue = Integer.MAX_VALUE;
		int nextValue;
		List<StateSpaceNode> nextLevel = new ArrayList<StateSpaceNode>(1);
		nextLevel.add(root);		

		List<StateSpaceNode> helpful   = new LinkedList<StateSpaceNode>();
		List<StateSpaceNode> unhelpful = new LinkedList<StateSpaceNode>();
		
		boolean foundBetter;
		while (!nextLevel.isEmpty()) {
			foundBetter = false;
			helpful.clear();
			unhelpful.clear();
			//separate helpful states from unhelpful states
			for (StateSpaceNode stateNode : nextLevel) {
				Util.separateHelpful(problem, stateNode, helpful, unhelpful);
			}
			//evaluate helpful actions
			for (StateSpaceNode stateNode : helpful) {
				nextValue = getHeuristicValue(stateNode);
				if (problem.isSolution(stateNode.plan)) {
					return stateNode.plan;
				} else {
					if (nextValue < hValue) {
						hValue = nextValue;
						bestNode = stateNode;
						foundBetter = true;
					}
				}
			}
			//if helpful actions yield nothing, weighted A* search on everything
			if (!foundBetter) {
				for (StateSpaceNode stateNode : unhelpful) {
					nextValue = getHeuristicValue(stateNode);
					if (problem.isSolution(stateNode.plan)) {
						return stateNode.plan;
					} else {
						if (nextValue < hValue) {
							hValue = nextValue;
							bestNode = stateNode;
							foundBetter = true;
						}
					}
				}
			}
			//build next layer, enforcing breadth-first search if no better node was found
			if (!foundBetter) {
				nextLevel = makeNextLevel(helpful,unhelpful);
			} else {
				nextLevel = makeNextLevel(bestNode);
			}
			
		}
		return null;
	}
	
	private int getHeuristicValue(StateSpaceNode stateNode) {
		visited++;
		if (visited >= nodeLimit) {
			throw new SearchLimitReachedException();
		}
		return new FastForwardHeuristic(problem).hValue(stateNode.state);
	}

	private List<StateSpaceNode> makeNextLevel (StateSpaceNode parent) {
		List<StateSpaceNode> nextLevel = new LinkedList<StateSpaceNode>();
		parent.expand();
		expanded++;
		for (StateSpaceNode child : parent.children) {
			nextLevel.add(child);
		}
		return nextLevel;
	}
	
	private List<StateSpaceNode> makeNextLevel (List<StateSpaceNode>... lists) {
		List<StateSpaceNode> nextLevel = new LinkedList<StateSpaceNode>();
		for (List<StateSpaceNode> list : lists) {
			for (StateSpaceNode parent : list) {
				parent.expand();
				expanded++;
				for (StateSpaceNode child : parent.children) {
					nextLevel.add(child);
				}
			}
		}
		return nextLevel;
	}
}
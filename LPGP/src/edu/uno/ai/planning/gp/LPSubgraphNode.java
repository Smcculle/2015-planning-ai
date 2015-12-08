package edu.uno.ai.planning.gp;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import edu.uno.ai.planning.Domain;
import edu.uno.ai.planning.DurativeDomain;
import edu.uno.ai.planning.SearchLimitReachedException;
import edu.uno.ai.planning.pg.DurativeStepNode;
import edu.uno.ai.planning.pg.LPPlanGraph;
import edu.uno.ai.planning.pg.LiteralNode;
import edu.uno.ai.planning.pg.StepNode;
import edu.uno.ai.planning.util.ImmutableList;

public class LPSubgraphNode extends SubgraphNode {
	
	private LpSolve solver;
	private boolean durActionAdded;
	private boolean isDurative;
	
	public LPSubgraphNode(LPPlanGraph graph) throws LpSolveException {
		super(graph);
		solver = null;
		durActionAdded = false;
		isDurative = false;
		Domain domain = graph.problem.domain;
		if(domain instanceof DurativeDomain){
			DurativeDomain dd = (DurativeDomain) domain;
			isDurative = dd.isDurativeDomain();
		}
	}
	
	public LPSubgraphNode(LPSubgraphNode parent, TotalOrderPlan plan, int level, ImmutableList<LiteralNode> goals) throws LpSolveException {
		super(parent, plan, level, goals);
		durActionAdded = parent.durActionAdded;
		isDurative = parent.isDurative();
		if(isDurative)
			solver = parent.solver.copyLp();
		else
			solver = null;
	}
	
	public boolean isDurative(){
		return isDurative;
	}
	
	@Override
	public SubgraphNode expand() {
		// If this would violate the search limit, throw an exception.
		LPSubgraphRoot root = getLPRoot();
		if(root.limit == root.descendants)
			throw new SearchLimitReachedException();
		try{
			if(isDurative())
				addConstraintMatrixColumn();
			return continueExpantion();
		}catch(LpSolveException ex){
			String err = "LP Solver Exception caught:\r\n"+ex.getMessage(); 
			throw new RuntimeException(err);
		}
	}
	
	public LPSubgraphRoot getLPRoot() {
		SubgraphNode node = this;
		while(node.parent != null)
			node = node.parent;
		return (LPSubgraphRoot) node;
	}
	
	public LPSubgraphNode continueExpantion() throws LpSolveException{
		// Loop until we generate a child node or run out of permutations.
		while(true) {
			// If this node has no more children, return null.
			if(!permutations.hasNext())
				return null;
			// Get the next permutation of steps.
			ImmutableList<StepNode> steps = permutations.next();
			// My child's plan is my plan plus all non-persistence steps.
			TotalOrderPlan childPlan = this.plan;
			int durActionCount = 0;
			for(StepNode stepNode : steps){
				if(!stepNode.persistence){
					// Handle durative actions specially
					if(stepNode instanceof DurativeStepNode){
						DurativeStepNode dStepNode = (DurativeStepNode) stepNode;
						if(dStepNode.isDurative()){
							if(dStepNode.isEnd()){
								// end action: add new row to M, set row bound to duration of 
								// action, set row constraint <=, set M[a,n] = 1
								solver.addConstraint(getNewConstraintMatrixRow(), LpSolve.LE, ((DurativeStepNode)stepNode).getDuration());
								durActionCount++;
							}else if(dStepNode.isInvariant()){
								// 	invariant action: set M[a,n] = 1
								solver.setMat(durActionCount, level - 1, 1.0);
							}else if(dStepNode.isStart()){
								// start action: set M[a,n] = 1
								solver.setMat(durActionCount, level - 1, 1.0);
								// start action: set row constraint for a ==
								solver.setConstrType(durActionCount, LpSolve.EQ);
								// start action: if(!lp_solve(M)){
								boolean solved = false;
								try{
									solver.solve();
									solved = true;
								}catch(LpSolveException ex){ 
									// start action !solve: don't add a (DO NOTHING)
									// start action !solve: set constraint row for a to <=
									solver.setConstrType(durActionCount, LpSolve.LE);
								}
								if(solved){
									// start action solve: add step
									childPlan = childPlan.add(stepNode.step);
								}
							}
						}
					}else{
						// Otherwise, just add the step
						childPlan = childPlan.add(stepNode.step);
					}
				}
			}
			// My child's level is one level earlier than mine.
			int childLevel = level - 1;
			// My child's goals are the preconditions of the steps at my level.
			ImmutableList<LiteralNode> childGoals = new ImmutableList<>();
			for(StepNode stepNode : steps)
				for(LiteralNode precondition : stepNode.getPreconditions(level))
					if(!childGoals.contains(precondition))
						childGoals = childGoals.add(precondition);
			// If any of the child's goals are mutex, try the next permutation.
			// Otherwise, return the child node.
			if(!anyMutex(childGoals, childLevel))
				return new LPSubgraphNode(this, childPlan, childLevel, childGoals);
		}
	}
	
	private void addConstraintMatrixColumn() throws LpSolveException {
		// initialize solver
		if(solver == null){
			LpSolve.makeLp(1, 1);
			solver.setMat(0, 0, 0.0);
		}else{
			// add new column to constraint matrix
			int rowCount = level;
			double[] newColumn = new double[rowCount];
			for(int i = 0; i < rowCount;i++){
				newColumn[i] = 0.0;
			}
			solver.addColumn(newColumn);
		}
	}
	
	private double[] getNewConstraintMatrixRow() throws LpSolveException {
		// set new row double in case needed
		int colCount = solver.getNcolumns();
		double[] newRow = new double[colCount];
		for(int i = 0; i < colCount; i++)
			newRow[i] = (i == colCount - 1) ? 1.0 : 0.0;
		return newRow;
	}

}

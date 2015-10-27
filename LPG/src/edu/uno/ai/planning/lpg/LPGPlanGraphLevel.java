package edu.uno.ai.planning.lpg;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all facts true at this level and the unique action step with 
 * links to the previous level and next level.  
 * 
 * @author Shane McCulley
 *
 */
public class LPGPlanGraphLevel {

		/** list of facts at this level */
		private List<PlanGraphLiteral> facts;
		
		/** The unique action at this level */
		private PlanGraphStep step;
		
		/** previous level in LPGPlanGraph */
		private LPGPlanGraphLevel parent;
		
		/** next level in LPGPlanGraph */
		private LPGPlanGraphLevel child;
		
		/**
		 * Constructor is used to create the last level at the construction of a new LPGPlanGraph,
		 * so it has no child
		 * 
		 * @param step Unique step at this level
		 * @param parent The parent graph of this level
		 */
		public LPGPlanGraphLevel(PlanGraphStep step, LPGPlanGraphLevel parent) {
			this.step = step;
			this.parent = parent;
			facts = parent.getNextLevelFacts();
		}
		
		/**
		 * Constructor to create a new level for the step being added
		 * 
		 * @param step Unique step at this level
		 * @param parent The parent graph of this level
		 * @param 
		 */
		public LPGPlanGraphLevel(PlanGraphStep step, LPGPlanGraphLevel parent, LPGPlanGraphLevel child) {
			this.step = step;
			this.parent = parent;
			this.child = child;
			facts = parent.getNextLevelFacts();
			
		}
			
		/**
		 * Get unsupported preconditions for the action at this level.  
		 * @return List of preconditions, which is empty if action is supported.  
		 * 
		 * TODO:  
		 */
		public List<PlanGraphLiteral> getUnsupportedPreconditions()
		{
			List<PlanGraphLiteral> preconditions = new ArrayList<PlanGraphLiteral>(step.getParentNodes());
			preconditions.retainAll(facts);
			return preconditions;
		}
		
		/**
		 * Returns the current facts of the level and the effects of the current step
		 * on those facts for the next level.  
		 * @return facts The list of facts true at the next level based on current facts
		 * and the effects of the current step
		 * 
		 * TODO:  Modify facts from the step
		 */
		public List<PlanGraphLiteral> getNextLevelFacts(){
			return facts;
		}
		
		/** get parent for this level */
		public LPGPlanGraphLevel getParent(){
			return this.parent;
		}
		
		/** get child for this level */
		public LPGPlanGraphLevel getChild(){
			return this.child;
		}
}

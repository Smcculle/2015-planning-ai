using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphProject
{
    /**
 * PlanGraphStep is a wrapper class that wraps a Step
 * with integer of initial level Step appeared along with a list
 * of PlanGraphLiterals that are its parents (the Literals required as
 * preconditions for this step) and a list of PlanGraphLiterals that are its
 * children (the Literals that resolve as this PlanGraphStep's
 * effects) 
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
    public class PlanGraphStep : PlanGraphNode
    {

        /** The level the Step first appeared in PlanGraph **/
        private int _initialLevel;

        /** Whether the Step wrapped is a persistent step **/
        private bool _isPersistent;

        /** The wrapped Step **/
        private Step _step;

        /** 
         * List of PlanGraphLiterals wrapping the Literals that are
         * preconditions of this PlanGraphStep's Step
         */
        private List<PlanGraphLiteral> _parents;

        /** 
         * List of PlanGraphLiterals wrapping the Literals that are
         * effects of this PlanGraphStep's Step
         */
        private List<PlanGraphLiteral> _children;

        /**
         * Factory method to create a Persistent Step wrapper
         * a Persistent Step is meant to have a single Precondition that
         * is the same as its Effect
         * @param step Persistent Step to be wrapped
         * @return persistentStep.isPersistent() == true
         */
        static public PlanGraphStep createPersistentStep(Step step)
        {
            PlanGraphStep persistentStep = new PlanGraphStep(step);
            persistentStep._isPersistent = true;
            return persistentStep;
        }

        /**
         * Creates a wrapped Step with an initialLevel at -1
         * with empty list of parents and children
         * Note: -1 meaning no level has been yet set
         * 
         * @param step Step to be wrapped
         */
        public PlanGraphStep(Step step)
        {
            _step = step;
            _initialLevel = -1;
            _parents = new List<PlanGraphLiteral>();
            _children = new List<PlanGraphLiteral>();
            _isPersistent = false;
        }

        /**
        * Creates a wrapped Step with a set initialLevel
        * with empty list of parents and children
        * 
        * @param step Step to be wrapped
        * @param initialLevel First level Literal appears in PlanGraph
        */
        public PlanGraphStep(Step step, int initialLevel)
        {
            _step = step;
            _initialLevel = initialLevel;
            _parents = new List<PlanGraphLiteral>();
            _children = new List<PlanGraphLiteral>();
            _isPersistent = false;
        }

        /**
         * Creates a wrapped Step with an initialLevel at -1
         * with a list of parents and children that are the
         * same as the given parents and children
         * Note: -1 meaning no level has been yet set
         * 
         * @param step Step to be wrapped
         */
        public PlanGraphStep(Step step, int initialLevel, List<PlanGraphLiteral> parents, List<PlanGraphLiteral> children) :
            this(step, initialLevel)
        {
            _initialLevel = initialLevel;
            _parents = new List<PlanGraphLiteral>(parents);
            _children = new List<PlanGraphLiteral>(children);
        }

        /**
         * Creates a wrapped Step with an initialLevel at -1
         * with a list of parents and children that are the
         * same as the given parents and children
         * Note: -1 meaning no level has been yet set
         * 
         * @param step Step to be wrapped
         */
        public PlanGraphStep(Step step, List<PlanGraphLiteral> parents, List<PlanGraphLiteral> children) :
            this(step, -1, parents, children)
        {
        }

        /**
         * Boolean method to return whether or not this 
         * PlanGraphStep is a persistent step 
         * @return true iff this = PlanGraphStep.createPersistenStep(step)
         */
        public bool isPersistent()
        {
            return _isPersistent;
        }

        /**
         * @return initialLevel First level Step appears in PlanGraph
         */
        public int getInitialLevel()
        {
            return _initialLevel;
        }

        /**
         * Change/Set first level Step appears in PlanGraph
         * 
         * @param initialLevel First level Step appears in PlanGraph
         */
        public void setInitialLevel(int levelNumber)
        {
            _initialLevel = levelNumber;
        }

        /**
         * Does step exist at given level of a PlanGraph
         * @return (getInitialLevel() > -1 && 
         * 			getInitialLevel() <= level) 
         */
        public bool existsAtLevel(int level)
        {
            bool hasValidInitialLevel = _initialLevel > -1;
            bool isUnderOrInLevel = _initialLevel <= level;
            return hasValidInitialLevel && isUnderOrInLevel;
        }

        /**
         * Return the list of Plan Graph Literal Parent Nodes
         * from the PlanGraph.  These nodes represent the
         * PlanGraphLiteral version of the wrapped step's
         * preconditions
         */
        public List<PlanGraphNode> getParentNodes()
        {
            List<PlanGraphNode> parentNodes = new List<PlanGraphNode>();
            foreach (PlanGraphNode parentNode in _parents)
                parentNodes.Add(parentNode);
            return parentNodes;
        }

        /**
         * Return the list of Plan Graph Literal Child Nodes
         * from the PlanGraph.  These nodes represent the
         * PlanGraphLiteral version of the wrapped step's
         * Effects
         */
        public List<PlanGraphNode> getChildNodes()
        {
            List<PlanGraphNode> childNodes = new List<PlanGraphNode>();
            foreach (PlanGraphNode childNode in _children)
                childNodes.Add(childNode);
            return childNodes;
        }

        /**
         * Protected method to populate this PlanGraphStep's Children
         * 
         * @param newLiteral new PlanGraphLiteral that is a Child of this PlanGraphStep in a PlanGraph
         * @ensure getChildNodes().contains(newLiteral)
         */
        public void addChildLiteral(PlanGraphLiteral newLiteral)
        {
            _children.Add(newLiteral);
        }

        /**
         * Protected method to populate this PlanGraphStep's parents
         * 
         * @param newLiteral new PlanGraphLiteral that is a Parent of this PlanGraphStep in a PlanGraph
         * @ensure getParentNodes().contains(newLiteral)
         */
        public void addParentLiteral(PlanGraphLiteral newLiteral)
        {
            _parents.Add(newLiteral);
        }

        /**
         * @return step Wrapped Step
         */
        public Step getStep()
        {
            return _step;
        }

        /**
         * Return whether or not two PlanGraphSteps represent the same Step
         * 
         * @param pgStep PlanGraphStep for comparison
         * @return getStep.compareTo(pgStep.getLiteral()) == 0
         */
        public bool equals(PlanGraphStep pgStep)
        {
            return getStep().CompareTo(pgStep.getStep()) == 0;
        }

        /**
         * @return String representation of this PlanGraphStep  
         */
        public override string ToString()
        {
            string output = _step.ToString();
            output += "[" + _initialLevel + "]";
            return output;
        }
    }
}

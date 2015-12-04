using Planning.Logic;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphProject
{
    /**
 * PlanGraphLiteral is a wrapper class that wraps a Literal
 * with integer of initial level Literal appeared along with a list
 * of PlanGraphSteps that are its parents (the steps that cause this
 * PlanGraphLiteral as their effects) and a list of PlanGraphSteps that are its
 * children (the steps that require this PlanGraphLiteral as its
 * precondition) 
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
    public class PlanGraphLiteral : PlanGraphNode
    {
        /** The wrapped Literal **/
        private Literal _literal;

        /** The level the Literal first appeared in PlanGraph **/
        private int _initialLevel;

        /** 
         * List of PlanGraphSteps wrapping the Steps that have this 
         * PlanGraphLiteral as an effect
         */
        private List<PlanGraphStep> _parents;

        /** 
         * List of PlanGraphSteps wrapping the Steps that have this 
         * PlanGraphLiteral as a precondition
         */
        private List<PlanGraphStep> _children;

        /**
         * Creates a wrapped Literal with a set initialLevel with the given children and parents
         * 
         * @param literal Literal to be wrapped
         * @param initialLevel First level Literal appears in PlanGraph
         * @param children Children Nodes of this PlanGraphLiteral within a PlanGraph
         * @param parents Parent Nodes of this PlanGraphLiteral within a PlanGraph
         */
        public PlanGraphLiteral(Literal literal, int initialLevel, List<PlanGraphStep> children, List<PlanGraphStep> parents)
        {
            _literal = literal;
            _initialLevel = initialLevel;
            _parents = new List<PlanGraphStep>(parents);
            _children = new List<PlanGraphStep>(children);
        }

        /**
         * Creates a wrapped Literal with an initialLevel of -1 and the given children and parents
         * Note: -1 meaning no level has been yet set
         * 
         * @param literal Literal to be wrapped
         * @param children Children Nodes of this PlanGraphLiteral within a PlanGraph
         * @param parents Parent Nodes of this PlanGraphLiteral within a PlanGraph
         */
        public PlanGraphLiteral(Literal literal, List<PlanGraphStep> children, List<PlanGraphStep> parents) :
            this(literal, -1, children, parents)
        {
        }

        /**
         * Creates a wrapped Literal with a set initialLevel and empty lists for its children and parents nodes
         * 
         * @param literal Literal to be wrapped
         * @param initialLevel First level Literal appears in PlanGraph
         */
        public PlanGraphLiteral(Literal literal, int initialLevel) :
            this(literal, initialLevel, new List<PlanGraphStep>(), new List<PlanGraphStep>())
        {
        }

        /**
         * Creates a wrapped Literal with an initialLevel of -1 and empty lists for its children and parents nodes
         * Note: -1 meaning no level has been yet set
         * 
         * @param literal Literal to be wrapped
         */
        public PlanGraphLiteral(Literal literal) :
            this(literal, -1, new List<PlanGraphStep>(), new List<PlanGraphStep>())
        {
        }

        /**
         * @return initialLevel First level Literal appears in PlanGraph
         */
        public int getInitialLevel()
        {
            return _initialLevel;
        }

        /**
         * Change/Set first level Literal appears in PlanGraph
         * 
         * @param initialLevel First level Literal appears in PlanGraph
         */
        public void setInitialLevel(int initialLevel)
        {
            _initialLevel = initialLevel;
        }

        /**
         * Return whether the PlanGraphLiteral is a valid Node in the PlanGraph it belongs to
         * 
         * @param level
         * @return getInitialLevel() > -1 && getInitialLevel() <= level
         */
        public bool existsAtLevel(int level)
        {
            bool hasValidInitialLevel = _initialLevel > -1;
            bool isUnderOrInLevel = _initialLevel <= level;
            return hasValidInitialLevel && isUnderOrInLevel;
        }

        /**
         * Return a copy of the list of PlanGraphStep nodes that wrap
         * steps that have this PlanGraphLiteral's literal as an effect
         * 
         * @return for(PlanGraphStep step : getParentNodes())
         * 				step.getStep().effects.contains(getLiteral())
         */
        public List<PlanGraphNode> getParentNodes()
        {
            List<PlanGraphNode> parentNodes = new List<PlanGraphNode>();
            foreach (PlanGraphNode parentNode in _parents)
                parentNodes.Add(parentNode);
            return parentNodes;
        }

        /**
         * Return a copy of the list of PlanGraphStep nodes that wrap
         * steps that have this PlanGraphLiteral's literal as a precondition
         * 
         * @return for(PlanGraphStep step : getChildNodes())
         * 				step.getStep().preconditions.contains(getLiteral())
         */
        public List<PlanGraphNode> getChildNodes()
        {
            List<PlanGraphNode> childNodes = new List<PlanGraphNode>();
            foreach (PlanGraphNode childNode in _children)
                childNodes.Add(childNode);
            return childNodes;
        }

        /**
         * Protected method to add a new PlanGraphStep to this PlanGraphLiteral's list of Parent Nodes
         * 
         * @param newStep new PlanGraphStep that is a parent of this PlanGraphLiteral in a PlanGraph
         * @ensure getParentNodes().contains(newStep)
         */
        public void addParentStep(PlanGraphStep newStep)
        {
            _parents.Add(newStep);
        }

        /**
         * Protected method to add a new PlanGraphStep to this PlanGraphLiteral's list of Children Nodes
         * 
         * @param newStep new PlanGraphStep that is a child of this PlanGraphLiteral in a PlanGraph
         * @ensure getChildNodes().contains(newStep)
         */
        public void addChildStep(PlanGraphStep newStep)
        {
            _children.Add(newStep);
        }

        /**
         * @return literal Wrapped Literal
         */
        public Literal getLiteral()
        {
            return _literal;
        }

        /**
         * Return whether or not two PlanGraphLiterals represent the same Literal
         * 
         * @param pgLiteral PlanGraphLiteral for comparison
         * @return getLiteral.compareTo(pgLiteral.getLiteral()) == 0
         */
        public bool equals(PlanGraphLiteral pgLiteral)
        {
            return pgLiteral != null && getLiteral().CompareTo(pgLiteral.getLiteral()) == 0;
        }

        /**
         * @return String representation of this PlanGraphLiteral  
         */
        public override string ToString()
        {
            String output = _literal.ToString();
            output += "[" + _initialLevel + "]";
            return output;
        }
    }
}

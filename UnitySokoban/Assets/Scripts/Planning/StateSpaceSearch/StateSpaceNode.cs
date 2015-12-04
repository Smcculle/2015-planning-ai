using Planning;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace StateSpaceSearchProject
{
    /**
     * Represents a node in a state space search graph.  A node is considered
     * expanded once it is created.  A node is considered visited once its
     * {@link #expand()} method has been called, at which point it expands all of
     * its possible successor nodes (i.e. all possible next states).
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public class StateSpaceNode
    {

        /** The plan taken to reach this state */
        public readonly TotalOrderPlan plan;

        /** The current state */
        public readonly State state;

        /** This node's parent node (i.e. the state before the last step) */
        public readonly StateSpaceNode parent;

        /** 
         * This node's children (i.e. each possible next state).
         * Note that this set is empty until {@link #expand()} has been called.
         */
        public readonly IEnumerable<StateSpaceNode> children = new List<StateSpaceNode>();

        /** The total number of visited nodes in this node's subtree (including this node) */
        private int visited = 0;

        /** The total number of expanded nodes in this node's subtree */
        private int expanded = 0;

        /**
         * Constructs a new node with a given parent and most recent step.
         * 
         * @param parent the previous state
         * @param step the step to take in the previous state
         */
        public StateSpaceNode(StateSpaceNode parent, Step step)
        {
            this.plan = parent.plan.addStep(step);
            this.state = parent.state.apply(step);
            this.parent = parent;
        }

        /**
         * Constructs a new root node with the given initial state.
         * 
         * @param initial the problem's initial state
         */
        public StateSpaceNode(State initial)
        {
            this.plan = new TotalOrderPlan();
            this.state = initial;
            this.parent = null;
        }

        /**
         * Returns the total number of visited nodes in this node's subtree
         * (including this node).
         * 
         * @return the number of visted nodes
         */
        public int countVisited()
        {
            return visited;
        }

        /**
         * Returns the total number of expanded nodes in this node's subtree.
         * 
         * @return the number of expanded nodes
         */
        public int countExpanded()
        {
            return expanded;
        }

        /**
         * Returns the root node of the search space.
         * 
         * @return the root
         */
        public StateSpaceNode getRoot()
        {
            StateSpaceNode current = this;
            while (current.parent != null)
                current = current.parent;
            return current;
        }

        /**
         * Marks this node as visited and expands all of its children (i.e. all
         * possible next states).
         */
        public void expand()
        {
            StateSpaceRoot root = (StateSpaceRoot)getRoot();
            List<StateSpaceNode> children = (List<StateSpaceNode>)this.children;
            if (root.limit == root.countVisited())
                throw new Exception();
            foreach (Step step in (root.search.problem as StateSpaceProblem).steps)
                if (step.precondition.IsTrue(state))
                    children.Add(new StateSpaceNode(this, step));
            StateSpaceNode ancestor = this;
            while (ancestor != null)
            {
                ancestor.visited++;
                ancestor.expanded += children.Count;
                ancestor = ancestor.parent;
            }
        }

        public override string ToString()
        {
            string planstr = "{";
            planstr += "[" + plan.Size() + "] ";
            foreach (Step step in plan) planstr += step + ", ";
            return planstr;
        }
    }

}

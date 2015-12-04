using Planning;
using Planning.Logic;
using Planning.Util;
using System;
using System.Collections.Generic;

namespace PlanGraphProject
{
    /**
 * A PlanGraphLevel is a substructure of PlanGraph
 * Each level contains facts/literals and actions/steps
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
    public class PlanGraphLevel
    {
        /** PlanGraphLevel's Parent **/
        private PlanGraphLevel _parent;

        /** List of all unique PlanGraphSteps in PlanGraph **/
        protected List<PlanGraphStep> _steps;

        /** List of all unique PlanGraphLiterals in PlanGraph **/
        protected List<PlanGraphLiteral> _effects;

        /** List of all Persistence Steps (easier record keeping) **/
        private List<PlanGraphStep> _persistenceSteps;

        /** Current level number **/
        private int _level;

        /** The PlanGraph structure containing this level **/
        protected PlanGraph _planGraph;

        /**
         * Constructs a new PlanGraphLevel
         * Does not create additional lists for facts/effect and steps/actions.
         * This constructor is specifically intended to create root level of PlanGraph
         * 
         * @param problem The StateSpaceProblem to setup PlanGraph Steps and Effects
         */
        public PlanGraphLevel(Problem problem, List<PlanGraphStep> steps, List<PlanGraphLiteral> effects,
                List<PlanGraphStep> persistenceSteps, PlanGraph planGraph)
        {
            _parent = null;
            _steps = steps;
            _effects = effects;
            _persistenceSteps = persistenceSteps;
            _planGraph = planGraph;
            _level = 0;
            setInitialEffects(problem.initial);
            setNonSpecifiedInitialEffects(problem.initial);
        }

        /**
         * Constructs a new PlanGraphLevel child
         * Does not create additional lists for facts/effect and steps/actions.
         * 
         * @param parent The parent of new PlanGraph
         */
        public PlanGraphLevel(PlanGraphLevel parent)
        {
            _parent = parent;
            _effects = parent._effects;
            _steps = parent._steps;
            _persistenceSteps = parent._persistenceSteps;
            _level = _parent._level + 1;
            _planGraph = parent._planGraph;
            setPerstitenceStepLevels();
            addAllPossibleNewSteps();
        }

        /**
         * Returns the PlanGraph's level number. Level number starts at
         * 0 for the root node.
         * 
         * @return integer Level number
         */
        public int getLevel()
        {
            return _level;
        }

        /**
         * Does this level contain goal effects/facts?
         * 
         * @param goal Goal Expression
         * @return true if goal effect/facts are within this level, false otherwise
         */
        public virtual bool containsGoal(Expression goal)
        {
            List<Literal> literals = ConversionUtil.expressionToLiterals(goal);
            foreach (Literal literal in literals)
            {
                PlanGraphLiteral pgLiteral = _planGraph.getPlanGraphLiteral(literal);
                if (!exists(pgLiteral))
                    return false;
            }
            return true;
        }

        /**
         * Previous/Parent level of this PlanGraphLevel
         * 
         * @return PlanGraphLevel Parent PlanGraphLevel or null if root
         */
        public PlanGraphLevel getParent()
        {
            return _parent;
        }

        /**
         * Is this PlanGraphLevel leveled off?
         * Determines this by checking the size of current steps and effects.
         * 
         * @return true if PlanGraph is leveled off, false otherwise
         */
        public virtual bool isLeveledOff()
        {
            if (_parent == null)
                return false;

            if (_parent.countCurrentEffects() == countCurrentEffects())
                if (_parent.countCurrentSteps() == countCurrentSteps())
                    return true;

            return false;
        }

        /**
         * Number of Effects at this level
         * 
         * @return integer Number of Effects at this level
         */
        public int countCurrentEffects()
        {
            int count = 0;
            foreach (PlanGraphLiteral effect in _effects)
                if (exists(effect))
                    count++;
            return count;
        }

        /**
         * Number of Steps at this level
         * 
         * @return integer Number of Steps at this level
         */
        public int countCurrentSteps()
        {
            int count = 0;
            foreach (PlanGraphStep step in _steps)
                if (exists(step))
                    count++;
            return count;
        }

        /**
         * Does PlanGraphStep exist at this level?
         * 
         * @param pgStep PlanGraphStep to test if exists
         * @return true if PlanGraphStep exists at this level, false otherwise
         */
        public bool exists(PlanGraphStep pgStep)
        {
            if (pgStep == null) return false;
            // return PlanGraphStep exists at this Level
            return pgStep.existsAtLevel(getLevel());
        }

        /**
         * Does PlanGraphLiteral exist at this level?
         * 
         * @param pgLiteral PlanGraphLiteral to test if exists
         * @return true if PlanGraphLiteral exists at this level, false otherwise
         */
        public bool exists(PlanGraphLiteral pgLiteral)
        {
            if (pgLiteral == null) return false;
            // return PlanGraphStep exists at this Level
            return pgLiteral.existsAtLevel(getLevel());
        }

        /**
         * Helper function to see if Step exists at this level
         * 
         * @param step Step to test if exists at this level
         * @return true if PlanGraphStep of Step exists at this level, false otherwise
         */
        public bool exists(Step step)
        {
            return exists(_planGraph.getPlanGraphStep(step));
        }

        /**
         * Helper function to see if Literal exists at this level
         * 
         * @param literal SteLiteral test if exists at this level
         * @return true if PlanGraphLiteral of Literal exists at this level, false otherwise
         */
        public bool exists(Literal literal)
        {
            return exists(_planGraph.getPlanGraphLiteral(literal));
        }

        /**
         * The String representation of the current PlanGraph
         * 
         * @return string String representation of the current PlanGraph
         */
        public override string ToString()
        {
            string str = "";

            str += "--------------------------------\n";
            str += "PlanGraph Level " + getLevel() + "\n";
            str += "--------------------------------\n";

            str += "Steps [" + countCurrentSteps() + "]:\n";
            foreach (PlanGraphStep step in _steps)
                if (exists(step))
                    str += "-" + step.ToString() + "\n";

            str += "Effects [" + countCurrentEffects() + "]:\n";
            foreach (PlanGraphLiteral effect in _effects)
                if (exists(effect))
                    str += "-" + effect.ToString() + "\n";

            return str;
        }

        // Private methods

        /**
         * Adds effect of initial state to PlanGraph root
         * 
         * @param initialState State from which to add effects
         */
        private void setInitialEffects(State initialState)
        {
            List<Literal> literals = ConversionUtil.expressionToLiterals(initialState.toExpression());
            foreach (Literal literal in literals)
                foreach (PlanGraphLiteral planGraphLiteral in _effects)
                    if (literal.Equals(planGraphLiteral.getLiteral()))
                        planGraphLiteral.setInitialLevel(getLevel());
        }

        /**
         * Any effect that was not explicitly specified is initiated
         * at root Level as NegatedLiteral
         * 
         * @param initialState Expression of the initial state
         */
        private void setNonSpecifiedInitialEffects(State initialState)
        {
            List<Literal> literals = ConversionUtil.expressionToLiterals(initialState.toExpression());
            foreach (PlanGraphLiteral planGraphLiteral in _effects)
                if (planGraphLiteral.getLiteral() is NegatedLiteral)
				if (!literals.Contains(planGraphLiteral.getLiteral().Negate()))
                planGraphLiteral.setInitialLevel(getLevel());
        }

        /**
         * Checks to see if any new persistence steps can be created at PlanGraph level.
         */
        private void setPerstitenceStepLevels()
        {
            foreach (PlanGraphStep persistenceStep in _persistenceSteps)
            {
                Literal literal = (Literal)persistenceStep.getStep().effect;
                if (!exists(persistenceStep))
                    if (_parent.exists(literal))
                        persistenceStep.setInitialLevel(getLevel());
            }
        }

        /**
         * Updates all non-persistent steps in PlanGraphLevel
         * Run at Constructor
         */
        private void addAllPossibleNewSteps()
        {
            foreach (PlanGraphStep step in _steps)
                if (!_persistenceSteps.Contains(step))
                    updateStep(step);
        }

        /**
         * Updates a PlanGraphStep on PlanGraph.
         * Does not add a step if already exist in PlanGraph.
         * Only updates a step if current all preconditions of step exist in parent's effects.
         * Also computes all new mutual exclusions introduced
         * 
         * @param PlanGraphStep Step to be added
         */
        private void updateStep(PlanGraphStep step)
        {
            if (step == null)
                return;

            if (_parent == null)
                return;

            if (!isPreconditionSatisfied(step))
                return;

            if (step.getInitialLevel() == -1)
            {
                step.setInitialLevel(getLevel());
                List<Literal> literals = ConversionUtil.expressionToLiterals(step.getStep().effect);
                foreach (Literal literal in literals)
                    if (!exists(literal))
                        _planGraph.getPlanGraphLiteral(literal).setInitialLevel(_level);
            }
        }

        /**
         * Checks effects of parent to see if preconditions are met.
         * 
         * @param planGraphStep Step to test precondition
         * @return True if preconditions exist in parent.
         */
        private bool isPreconditionSatisfied(PlanGraphStep planGraphStep)
        {
            if (_parent == null)
                return false;

            Step step = planGraphStep.getStep();
            foreach (Literal literal in ConversionUtil.expressionToLiterals(step.precondition))
            {
                bool didFindValue = false;
                foreach (PlanGraphLiteral planGraphLiteral in _effects)
                {
                    if (_parent.exists(planGraphLiteral))
                    {
                        if (literal.Equals(planGraphLiteral.getLiteral()))
                        {
                            didFindValue = true;
                            break;
                        }
                    }
                }

                if (!didFindValue)
                    return false;
            }
            return true;
        }
    }
}
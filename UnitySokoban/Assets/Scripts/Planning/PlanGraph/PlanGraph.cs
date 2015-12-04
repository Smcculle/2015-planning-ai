using Planning;
using Planning.Logic;
using Planning.Util;
using StateSpaceSearchProject;
using System.Collections.Generic;

namespace PlanGraphProject
{
    /**
 * A PlanGraph is a structure of PlanGraphLevels
 * Each level contains facts/literals and actions/steps
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
    public class PlanGraph
    {
        /** Number of Levels **/
        private int _levels;

        /** List of all currently extended PlanGraphLevels **/
        private List<PlanGraphLevel> _levelList;

        /** List of all unique PlanGraphSteps in PlanGraph **/
        private List<PlanGraphStep> _steps;

        /** List of all unique PlanGraphLiterals in PlanGraph **/
        private List<PlanGraphLiteral> _effects;

        /** List of all Persistence Steps (easier record keeping) */
        private List<PlanGraphStep> _persistenceSteps;

        /** Will this PlanGraph calculate mutual exclusions? */
        private bool _calculateMutex;

        /**
         * Constructs a new PlanGraph Structure
         * Will extend until new PlanGraphLevel contains goal or is leveled off
         * Contains 1 or more PlanGraphLevels
         * Contains 1 list of all Actions/Steps that will be used by all PlanGraphLevels
         * Contains 1 list of all Facts/Literals/Effects that will be used by all PlanGraphLevels
         * 
         * @param problem The Problem in which to setup PlanGraph
         * @param calculateMutex If true, will replace PlanGraphLevels with PlanGraphLevelMutexes
         */
        public PlanGraph(Problem problem, bool calculateMutex)
        {
            _levelList = new List<PlanGraphLevel>();
            _steps = new List<PlanGraphStep>();
            _effects = new List<PlanGraphLiteral>();
            _persistenceSteps = new List<PlanGraphStep>();
            _calculateMutex = calculateMutex;

            StateSpaceProblem ssProblem = new StateSpaceProblem(problem);
            addAllSteps(ssProblem.steps);
            addAllEffects(ssProblem.literals);
            addAllPerstitenceSteps();

            connectParentsToChildren();

            PlanGraphLevel rootLevel = _calculateMutex ?
                new PlanGraphLevelMutex(problem, _steps, _effects, _persistenceSteps, this) :
                new PlanGraphLevel(problem, _steps, _effects, _persistenceSteps, this);

            _levelList.Add(rootLevel);
            _levels = 1;

            while (!getMaxLevel().containsGoal(problem.goal) && !getMaxLevel().isLeveledOff())
                extend();
        }

        /**
         * Default value of PlanGraph is not to calculate mutual exclusions.
         * 
         * @param problem The Problem in which to setup PlanGraph
         */
        public PlanGraph(Problem problem) :
            this(problem, false)
        {
        }

        /**
         * Extends the PlanGraph by adding an additional level to PlanGraph
         */
        public void extend()
        {
            PlanGraphLevel nextLevel = _calculateMutex ?
                new PlanGraphLevelMutex((PlanGraphLevelMutex)getMaxLevel()) :
                new PlanGraphLevel(getMaxLevel());

            _levelList.Add(nextLevel);
            _levels++;
        }

        /**
         * Get the PlanGraphLevel Instance at a certain level
         * 
         * @param level The PlanGraphLevel number
         * @return planGraphLevel Return planGraphLevel at level number if exists, null otherwise
         */
        public PlanGraphLevel getLevel(int level)
        {
            if (level < _levels)
                return _levelList[level];
            else
                return null;
        }

        /**
         * Get the root PlanGraphLevel (ie PlanGraphLevel @ level 0)
         * 
         * @return planGraphLevel Returns root PlanGraphLevel
         */
        public PlanGraphLevel getRootLevel()
        {
            return _levelList[0];
        }

        /**
         * The number of levels in the PlanGraph
         * 
         * @return int Number of levels in the PlanGraph
         */
        public int CountLevels()
        {
            return _levels;
        }

        /**
        * Helper function to get PlanGraphStep from Step
        * 
        * @param step Step to get PlanGraphStep
        * @return planGraphStep Corresponding PlanGraphStep
        */
        public PlanGraphStep getPlanGraphStep(Step step)
        {
            foreach (PlanGraphStep planGraphStep in _steps)
                if (planGraphStep.getStep().CompareTo(step) == 0)
                    return planGraphStep;
            return null;
        }

        /**
         * Helper function to get PlanGraphLiteral from literal
         * 
         * @param literal Literal to get PlanGraphLiteral
         * @return planGraphLiteral Corresponding PlanGraphLiteral
         */
        public PlanGraphLiteral getPlanGraphLiteral(Literal literal)
        {
            foreach (PlanGraphLiteral planGraphLiteral in _effects)
                if (planGraphLiteral.getLiteral().Equals(literal))
                    return planGraphLiteral;
            return null;
        }

        /**
         * Does the PlanGraphStep exist at level specified?
         * 
         * @param pgStep PlanGraphStep to check if exists at certain level
         * @param level The level number to check
         * @return boolean True if planGraphStep exists at level, false otherwise
         */
        public bool existsAtLevel(PlanGraphStep pgStep, int level)
        {
            PlanGraphLevel planGraphLevel = getLevel(level);
            return planGraphLevel.exists(pgStep);
        }

        /**
         * The one and only list of all PlanGraphSteps in PlanGraph
         * 
         * @return ArrayList<PlanGraphStep> All Possible Plan Graph Steps
         */
        public List<PlanGraphStep> getAllPossiblePlanGraphSteps()
        {
            return _steps;
        }

        /**
         * The one and only list of all PlanGraphEffects in PlanGraph
         * 
         * @return ArrayList<PlanGraphEffects> All Possible Plan Graph Effects
         */
        public List<PlanGraphLiteral> getAllPossiblePlanGraphEffects()
        {
            return _effects;
        }

        /**
         * Does the PlanGraphLiteral exist at level specified?
         * 
         * @param pgLiteral PlanGraphLiteral to check if exists at certain level
         * @param level The level number to check
         * @return boolean True if planGraphLiteral exists at level, false otherwise
         */
        public bool ExistsAtLevel(PlanGraphLiteral pgLiteral, int level)
        {
            PlanGraphLevel planGraphLevel = getLevel(level);
            return planGraphLevel.exists(pgLiteral);
        }

        public bool IsMutexAtLevel(PlanGraphStep step, PlanGraphStep otherStep, int level)
        {
            PlanGraphLevel planGraphLevel = getLevel(level);
            if (planGraphLevel is PlanGraphLevelMutex)
                return (planGraphLevel as PlanGraphLevelMutex).isMutex(step, otherStep);
            return false;
        }

        /**
         * Returns the most extended/maximum PlanGraphLevel in PlanGraph
         * 
         * @return PlanGraphLevel Maximum PlanGraphLevel in PlanGraph
         */
        private PlanGraphLevel getMaxLevel()
        {
            return _levelList[_levels - 1];
        }

        /**
         * Adds all possible effects from all possible steps.
         * 
         * @param steps All possible steps
         */
        private void addAllEffects(ImmutableArray<Literal> literals)
        {
            foreach (Literal literal in literals)
                _effects.Add(new PlanGraphLiteral(literal));
        }

        /**
         * Adds all possible steps from problem.
         * 
         * @param steps All possible Steps.
         */
        private void addAllSteps(ImmutableArray<Step> steps)
        {
            foreach (Step step in steps)
                _steps.Add(new PlanGraphStep(step));
        }

        /**
         * Connect all Steps and Effects to their parents and children
         */
        private void connectParentsToChildren()
        {
            foreach (PlanGraphStep step in _steps)
            {
                // Add Step effects as Plan Graph Children
                List<Literal> effectLiterals = ConversionUtil.expressionToLiterals(step.getStep().effect);
                foreach (Literal literal in effectLiterals)
                    foreach (PlanGraphLiteral effect in _effects)
                        if (effect.equals(new PlanGraphLiteral(literal)))
                        {
                            step.addChildLiteral(effect);
                            effect.addParentStep(step);
                        }
                // Add Step Preconditions as Plan Graph Parents
                List<Literal> preconditionLiterals = ConversionUtil.expressionToLiterals(step.getStep().precondition);
                foreach (Literal literal in preconditionLiterals)
                    foreach (PlanGraphLiteral effect in _effects)
                        if (effect.equals(new PlanGraphLiteral(literal)))
                        {
                            step.addParentLiteral(effect);
                            effect.addChildStep(step);
                        }
            }
        }

        /**
         * Adds all possible persistence steps from _effects.
         */
        private void addAllPerstitenceSteps()
        {
            foreach (PlanGraphLiteral planGraphLiteral in _effects)
            {
                Literal literal = planGraphLiteral.getLiteral();
                Step step = new Step("(Noop " + literal.ToString() + ")", literal, literal);
                PlanGraphStep planGraphStep = PlanGraphStep.createPersistentStep(step);
                _steps.Add(planGraphStep);
                _persistenceSteps.Add(planGraphStep);
            }
        }

        /**
         * Does the most extended/maximum PlanGraphLevel contain goal literals/facts? 
         * 
         * @param goal Goal expression
         * @return true if max PlanGraphLevel contains all goal facts, false otherwise
         */
        public bool containsGoal(Expression goal)
        {
            return getMaxLevel().containsGoal(goal);
        }

        /**
         * Is the most extended/maximum PlanGraphLevel the same as its parent?
         * 
         * @return true if PlanGraph is leveled off, false otherwise
         */
        public bool isLeveledOff()
        {
            return getMaxLevel().isLeveledOff();
        }

        public override string ToString()
        {
            string str = "";
            foreach (PlanGraphLevel level in _levelList)
                str += level.ToString();

            return str;
        }
    }
}

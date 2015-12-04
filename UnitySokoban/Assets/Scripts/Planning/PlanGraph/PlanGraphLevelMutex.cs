using Planning;
using Planning.Logic;
using Planning.Util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphProject
{
    /**
 * An extension to PlanGraphLevel that calculates Mutual Exclusions
 * 
 * @author Edward Thomas Garcia
 * @author Christian Levi
 * 
 */
    public class PlanGraphLevelMutex : PlanGraphLevel
    {
        /** List of all mutually exclusive steps in current PlanGraph Level */
        Dictionary<PlanGraphStep, List<PlanGraphStep>> _mutexSteps;

        /** List of all mutually exclusive literals in current PlanGraph Level */
        Dictionary<PlanGraphLiteral, List<PlanGraphLiteral>> _mutexLiterals;

        /** The PlanGraph structure containing this level **/
        private PlanGraphLevelMutex _parent;

        /**
         * Constructs a new PlanGraphLevel
         * Does not create additional lists for facts/effect and steps/actions.
         * This constructor is specifically intended to create root level of PlanGraph
         * Calculates all mutual exclusions at constructor
         * 
         * @param problem The StateSpaceProblem to setup PlanGraph Steps and Effects
         */
        public PlanGraphLevelMutex(Problem problem, List<PlanGraphStep> steps, List<PlanGraphLiteral> effects,
            List<PlanGraphStep> persistenceSteps, PlanGraph planGraph) :
            base(problem, steps, effects, persistenceSteps, planGraph)
        {
            _mutexSteps = new Dictionary<PlanGraphStep, List<PlanGraphStep>>();
            _mutexLiterals = new Dictionary<PlanGraphLiteral, List<PlanGraphLiteral>>();
            checkForOpposites();
        }

        /**
         * Constructs a new PlanGraphLevelMutex child
         * Does not create additional lists for facts/effect and steps/actions.
         * Calculates all mutual exclusions at constructor
         * 
         * @param parent The parent of new PlanGraph
         */
        public PlanGraphLevelMutex(PlanGraphLevelMutex parent) :
            base(parent)
        {
            _parent = parent;
            _mutexSteps = new Dictionary<PlanGraphStep, List<PlanGraphStep>>();
            _mutexLiterals = new Dictionary<PlanGraphLiteral, List<PlanGraphLiteral>>();
            checkForInconsistentEffects();
            checkForInterference();
            checkForCompetingNeeds();
            checkForOpposites();
            checkForInconsistentSupport();
        }

        /**
         * Returns a Map of Mutually Exclusive Steps.
         * 
         * @return Map<PlanGraphStep, ArrayList<PlanGraphStep>> Mutually Exclusive Steps.
         */
        public Dictionary<PlanGraphStep, List<PlanGraphStep>> getMutuallyExclusiveSteps()
        {
            return _mutexSteps;
        }

        /**
         * Returns a Map of Mutually Exclusive Literals.
         * 
         * @return Map<PlanGraphLiteral, ArrayList<PlanGraphLiteral>> Mutually Exclusive Literals.
         */
        public Dictionary<PlanGraphLiteral, List<PlanGraphLiteral>> getMutuallyExclusiveLiterals()
        {
            return _mutexLiterals;
        }

        /**
         * Returns whether otherStep is a mutex action of step
         * 	Will return false if either step or otherStep is null
         * @param step
         * @param otherStep
         * @return
         */
        public bool isMutex(PlanGraphStep step, PlanGraphStep otherStep)
        {
            if (step == null || otherStep == null)
                return false;

            if (_mutexSteps.ContainsKey(step))
                return _mutexSteps[step].Contains(otherStep);

            return false;
        }

        /**
         * Returns whether otherStep is a mutex action of step
         * 	Will return false if either step or otherStep is null
         * @param step
         * @param otherStep
         * @return
         */
        public bool isMutex(Step step, Step otherStep)
        {
            PlanGraphStep pgStep = _planGraph.getPlanGraphStep(step);
            PlanGraphStep phOtherStep = _planGraph.getPlanGraphStep(otherStep);
            return isMutex(pgStep, phOtherStep);
        }

        /**
         * Does this level contain goal effects/facts and are they non-mutex?
         * 
         * @param goal Goal Expression
         * @return true if goal effect/facts are non-mutex and within this level, false otherwise
         */
        public override bool containsGoal(Expression goal)
        {
            if (!base.containsGoal(goal))
                return false;

            List<Literal> literals = ConversionUtil.expressionToLiterals(goal);
            foreach (Literal literal in literals)
            {
                PlanGraphLiteral pgLiteral = _planGraph.getPlanGraphLiteral(literal);
                foreach (Literal otherLiteral in literals)
                {
                    PlanGraphLiteral pgOtherLiteral = _planGraph.getPlanGraphLiteral(otherLiteral);
                    if (!pgLiteral.equals(pgOtherLiteral))
                        if (_mutexLiterals.ContainsKey(pgLiteral))
                            if (_mutexLiterals[pgLiteral].Contains(pgOtherLiteral))
                                return false;
                }
            }

            return true;
        }

        /**
         * Is this PlanGraphLevelMutex leveled off?
         * Determines this by checking the size of current steps and effects and mutexs.
         * 
         * @return true if PlanGraph is leveled off, false otherwise
         */
        public override bool isLeveledOff()
        {
            if (_parent == null)
                return false;

            if (_parent.countCurrentEffects() == countCurrentEffects())
                if (_parent.countCurrentSteps() == countCurrentSteps())
                    if (_parent._mutexLiterals.Keys.Count == _mutexLiterals.Keys.Count)
                        if (_parent._mutexSteps.Keys.Count == _mutexSteps.Keys.Count)
                        {
                            int parentSize = 0; int size = 0;
                            foreach (PlanGraphStep key in _parent._mutexSteps.Keys)
                                parentSize += _parent._mutexSteps[key].Count;
                            foreach (PlanGraphStep key in _mutexSteps.Keys)
                                size += _mutexSteps[key].Count;
                            if (parentSize != size)
                                return false;

                            parentSize = 0; size = 0;
                            foreach (PlanGraphLiteral key in _parent._mutexLiterals.Keys)
                                parentSize += _parent._mutexLiterals[key].Count;
                            foreach (PlanGraphLiteral key in _mutexLiterals.Keys)
                                size += _mutexLiterals[key].Count;
                            if (parentSize != size)
                                return false;

                            return true;
                        }

            return false;
        }

        /**
         * Checks to see if there are inconsistent effects with newly added step.
         * Inconsistent effect: One action negates an effect of the other.
         */
        private void checkForInconsistentEffects()
        {
            foreach (PlanGraphStep step in _steps)
            {
                if (exists(step))
                {
                    foreach (PlanGraphStep otherStep in _steps)
                    {
                        if (step != otherStep)
                        {
                            if (exists(otherStep))
                            {
                                List<Literal> stepEffectLiterals = ConversionUtil.expressionToLiterals(step.getStep().effect);
                                List<Literal> otherStepEffectLiterals = ConversionUtil.expressionToLiterals(otherStep.getStep().effect);
                                foreach (Expression literal in stepEffectLiterals)
                                {
                                    Literal negatedLiteral = literal.Negate() as Literal;
                                    if (otherStepEffectLiterals.Contains(negatedLiteral) && !stepEffectLiterals.Contains(negatedLiteral))
                                    {
                                        addMutexStep(step, otherStep);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Checks to see if there is interference with newly added step.
         * Interference: One action negates the precondition of the other.
         */
        private void checkForInterference()
        {
            foreach (PlanGraphStep step in _steps)
            {
                if (exists(step))
                {
                    foreach (PlanGraphStep otherStep in _steps)
                    {
                        if (step != otherStep)
                        {
                            if (exists(otherStep))
                            {
                                List<Literal> stepEffectLiterals = ConversionUtil.expressionToLiterals(step.getStep().effect);
                                List<Literal> otherStepPreconditionLiterals = ConversionUtil.expressionToLiterals(otherStep.getStep().precondition);
                                foreach (Expression literal in stepEffectLiterals)
                                {
                                    Literal negatedLiteral = literal.Negate() as Literal;
                                    if (otherStepPreconditionLiterals.Contains(negatedLiteral))
                                    {
                                        addMutexStep(step, otherStep);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Checks to see if there are competing need with newly added step.
         * Competing Needs: Actions have mutually exclusive preconditions.
         */
        private void checkForCompetingNeeds()
        {
            foreach (PlanGraphStep step in _steps)
            {
                if (exists(step))
                {
                    foreach (PlanGraphStep otherStep in _steps)
                    {
                        if (step != otherStep)
                        {
                            if (exists(otherStep))
                            {
                                List<Literal> stepPreconditionLiterals = ConversionUtil.expressionToLiterals(step.getStep().precondition);
                                List<Literal> otherStepPreconditionLiterals = ConversionUtil.expressionToLiterals(otherStep.getStep().precondition);
                                foreach (Literal literal in stepPreconditionLiterals)
                                {
                                    PlanGraphLiteral pgLiteral = _planGraph.getPlanGraphLiteral(literal);
                                    if (_parent._mutexLiterals.ContainsKey(pgLiteral))
                                    {
                                        bool isLiteralMutexWithOtherLiteral = false;
                                        foreach (Literal otherLiteral in otherStepPreconditionLiterals)
                                            if (_parent._mutexLiterals[pgLiteral].Contains(_planGraph.getPlanGraphLiteral(otherLiteral)))
                                            {
                                                isLiteralMutexWithOtherLiteral = true;
                                                break;
                                            }

                                        if (isLiteralMutexWithOtherLiteral)
                                        {
                                            addMutexStep(step, otherStep);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Checks to see if newly added effects contain opposites.
         * Opposites: One literal is a negation of another.
         */
        private void checkForOpposites()
        {
            foreach (PlanGraphLiteral effect in _effects)
            {
                if (exists(effect))
                {
                    foreach (PlanGraphLiteral otherEffect in _effects)
                    {
                        if (!effect.equals(otherEffect))
                        {
                            if (exists(otherEffect))
                            {
                                PlanGraphLiteral negatedEffect = _planGraph.getPlanGraphLiteral(effect.getLiteral().Negate());
                                if (negatedEffect.equals(otherEffect))
                                    addMutexLiteral(effect, otherEffect);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Checks to see if newly added effects contain inconsistent support.
         * Inconsistent Support: Every possible pair of actions that could achieve the literals
         * are mutually exclusive.
         */
        private void checkForInconsistentSupport()
        {
            foreach (PlanGraphLiteral effect in _effects)
            {
                if (exists(effect))
                {
                    foreach (PlanGraphLiteral otherEffect in _effects)
                    {
                        if (effect != otherEffect)
                        {
                            if (exists(otherEffect))
                            {
                                List<PlanGraphStep> stepsWithEffect = new List<PlanGraphStep>();
                                List<PlanGraphStep> stepsWithOtherEffect = new List<PlanGraphStep>();

                                // Get steps containing effect
                                foreach (PlanGraphStep step in _steps)
                                    if (exists(step))
                                        if (ConversionUtil.expressionToLiterals(step.getStep().effect).Contains(effect.getLiteral()))
                                            stepsWithEffect.Add(step);

                                // Get steps containing otherEffect
                                foreach (PlanGraphStep step in _steps)
                                    if (exists(step))
                                        if (ConversionUtil.expressionToLiterals(step.getStep().effect).Contains(otherEffect.getLiteral()))
                                            stepsWithOtherEffect.Add(step);

                                bool allSupportingStepsAreMutex = true;
                                foreach (PlanGraphStep step in stepsWithEffect)
                                {
                                    // If Step with Effects is not in list of Mutex Steps, this pair of Steps cannot be mutually exclusive
                                    if (!_mutexSteps.ContainsKey(step))
                                    {
                                        allSupportingStepsAreMutex = false;
                                        break;
                                    }
                                    else
                                    {
                                        foreach (PlanGraphStep otherStep in stepsWithOtherEffect)
                                        {
                                            if (step != otherStep)
                                            {
                                                if (!_mutexSteps[step].Contains(otherStep))
                                                {
                                                    allSupportingStepsAreMutex = false;
                                                    break;
                                                }
                                            }
                                            else
                                            {
                                                allSupportingStepsAreMutex = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (!allSupportingStepsAreMutex)
                                        break;
                                }

                                if (allSupportingStepsAreMutex)
                                    addMutexLiteral(effect, otherEffect);
                            }
                        }
                    }
                }
            }
        }


        /**
         * Helper method to add Mutex Steps to PlanGraph Level
         * If step was already in Mutex Steps and otherStep was not in list of steps
         * 		Add otherStep to list of Mutex Steps for step
         * Else
         * 		Add step to Mutex Step list with otherStep as its only Mutex Step 
         * 
         * @param step
         * @param otherStep
         */
        private void addMutexStep(PlanGraphStep step, PlanGraphStep otherStep)
        {
            if (_mutexSteps.ContainsKey(step))
            {
                List<PlanGraphStep> steps = _mutexSteps[step];
                if (!steps.Contains(otherStep))
                {
                    steps.Add(otherStep);
                    addMutexStep(otherStep, step);
                }
            }
            else
            {
                List<PlanGraphStep> steps = new List<PlanGraphStep>();
                steps.Add(otherStep);
                _mutexSteps.Add(step, steps);
                addMutexStep(otherStep, step);
            }
        }

        /**
         * Add mutual exclusion to list
         * 
         * @param effect
         * @param otherEffect
         */
        private void addMutexLiteral(PlanGraphLiteral effect, PlanGraphLiteral otherEffect)
        {
            if (_mutexLiterals.ContainsKey(effect))
            {
                List<PlanGraphLiteral> literals = _mutexLiterals[effect];
                if (!literals.Contains(otherEffect))
                {
                    literals.Add(otherEffect);
                    addMutexLiteral(otherEffect, effect);
                }
            }
            else
            {
                List<PlanGraphLiteral> literals = new List<PlanGraphLiteral>();
                literals.Add(otherEffect);
                _mutexLiterals.Add(effect, literals);
                addMutexLiteral(otherEffect, effect);
            }
        }


        /**
         * The String representation of the current PlanGraph
         * 
         * @return string String representation of the current PlanGraph
         */
        public override string ToString()
        {
            String str = base.ToString();

            str += "Mutex Steps [" + _mutexSteps.Count + "]:\n";
            foreach (PlanGraphStep step in _mutexSteps.Keys)
            {
                str += "-" + step.ToString() + "\n";
                foreach (PlanGraphStep mutexLiteral in _mutexSteps[step])
                    str += "    -" + mutexLiteral.ToString() + "\n";
            }

            str += "Mutex Literals [" + _mutexLiterals.Count + "]:\n";
            foreach (PlanGraphLiteral literal in _mutexLiterals.Keys)
            {
                str += "-" + literal.ToString() + "\n";
                foreach (PlanGraphLiteral mutexLiteral in _mutexLiterals[literal])
                    str += "    -" + mutexLiteral.ToString() + "\n";
            }
            return str;
        }
    }
}

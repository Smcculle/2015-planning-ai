using Planning;
using Planning.Logic;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphSGW
{
    public class PlanGraph
    {

        public readonly StateSpaceProblem problem;
        public readonly IEnumerable<LiteralNode> goals;
        public readonly bool mutexes;
        protected readonly Dictionary<Literal, LiteralNode> literalMap = new Dictionary<Literal, LiteralNode>();
        public readonly LiteralNode[] literals;
        protected readonly Dictionary<Step, StepNode> stepMap = new Dictionary<Step, StepNode>();
        protected readonly StepNode[] steps;
        internal readonly List<Node> toReset = new List<Node>();
        internal readonly List<StepNode> nextSteps = new List<StepNode>();
        private readonly List<Level> levels = new List<Level>();
        private int size = 0;
        private bool leveledOff = false;

        public PlanGraph(StateSpaceProblem problem, bool mutexes)
        {
            this.problem = problem;
            this.mutexes = mutexes;
            foreach (Step step in problem.steps)
                addEdgesForStep(new StepNode(this, step));
            List<Literal> literals = new List<Literal>(this.literalMap.Count);
            literals.AddRange(this.literalMap.Keys);
            foreach (Literal literal in literals)
                addEdgesForStep(new StepNode(this, literal));
            List<LiteralNode> goals = new List<LiteralNode>();
            forEachLiteral(problem.goal.ToDNF(), (literal) =>
            {
                goals.Add(getLiteralNode(literal));
            });
            this.goals = goals;
            this.levels.Add(new Level(this, 0));
            this.literals = this.literalMap.Values.ToArray();
            this.steps = this.stepMap.Values.ToArray();
            if (mutexes)
                computeStaticMutexes();
        }

        public PlanGraph(Problem problem, bool mutexes = false)
            : this(new StateSpaceProblem(problem), mutexes)
        {
        }

        private void addEdgesForStep(StepNode stepNode)
        {
            stepMap[stepNode.step] = stepNode;
            forEachLiteral(stepNode.step.precondition.ToDNF(), (literal) =>
            {
                LiteralNode literalNode = getLiteralNode(literal);
                literalNode.consumers.Add(stepNode);
                stepNode.preconditions.Add(literalNode);
            });
            forEachLiteral(stepNode.step.effect.ToDNF(), (literal) =>
            {
                LiteralNode literalNode = getLiteralNode(literal);
                stepNode.effects.Add(literalNode);
                literalNode.producers.Add(stepNode);
            });
        }

        private void forEachLiteral(Expression expression, Action<Literal> action)
        {
            if (expression is Literal)
                action.Invoke((Literal)expression);
            else
                foreach (Expression argument in ((NAryBooleanExpression)expression).arguments)
                    forEachLiteral(argument, action);
        }

        private LiteralNode getLiteralNode(Literal literal)
        {
            if (literalMap.ContainsKey(literal))
                return literalMap[literal];
            else
            {
                LiteralNode literalNode = new LiteralNode(this, literal);
                literalMap[literal] = literalNode;
                return literalNode;
            }
        }

        private void computeStaticMutexes()
        {
            // A literal is always mutex with its negation.
            foreach (LiteralNode literalNode in literals)
            {
                LiteralNode negation = get(literalNode.literal.Negate());
                if (negation != null)
                    literalNode.mutexes.add(negation, Mutexes.ALWAYS);
            }
            // Compute static mutexes for all pairs of steps.
            for (int i = 0; i < steps.Length; i++)
            {
                for (int j = i; j < steps.Length; j++)
                {
                    if (alwaysMutex(steps[i], steps[j]))
                    {
                        steps[i].mutexes.add(steps[j], Mutexes.ALWAYS);
                        steps[j].mutexes.add(steps[i], Mutexes.ALWAYS);
                    }
                }
            }
        }

        private bool alwaysMutex(StepNode s1, StepNode s2)
        {
            // Inconsistent effects: steps which undo each others' effects are always mutex.
            foreach (LiteralNode s1Effect in s1.effects)
            {
                Literal negation = s1Effect.literal.Negate();
                foreach (LiteralNode s2Effect in s2.effects)
                    if (s2Effect.literal.Equals(negation))
                        return true;
            }
            // Interference: steps which undoe each other's preconditions are always mutex.
            if (s1 == s2)
                return false;
            if (interference(s1, s2) || interference(s2, s1))
                return true;
            return false;
        }

        private bool interference(StepNode s1, StepNode s2)
        {
            foreach (LiteralNode s1Effect in s1.effects)
            {
                Literal negation = s1Effect.literal.Negate();
                foreach (LiteralNode s2Precondition in s2.preconditions)
                    if (s2Precondition.literal.Equals(negation))
                        return true;
            }
            return false;
        }

        public LiteralNode get(Literal literal)
        {
            if (literalMap.ContainsKey(literal))
                return literalMap[literal];
            else
                return null;
        }

        public StepNode get(Step step)
        {
            if (stepMap.ContainsKey(step))
                return stepMap[step];
            else
                return null;
        }

        public void initialize(State initial)
        {
            size = 1;
            foreach (Node node in toReset)
                node.Reset();
            toReset.Clear();
            foreach (LiteralNode node in literalMap.Values)
                if (initial.isTrue(node.literal))
                    node.setLevel(0);
            leveledOff = nextSteps.Count == 0;
        }

        public void extend()
        {
            Level level = new Level(this, size);
            if (levels.Count == size)
                levels.Add(level);
            size++;
            addStep(0);
            if (mutexes)
                level.computeMutexes();
            if (nextSteps.Count == 0)
                leveledOff = true;
        }

        private void addStep(int index)
        {
            if (index == nextSteps.Count)
                nextSteps.Clear();
            else
            {
                StepNode step = nextSteps[index];
                addStep(index + 1);
                step.setLevel(size - 1);
            }
        }

        public int Size()
        {
            return size;
        }

        public Level getLevel(int number)
        {
            if (number < 0 || number >= size)
                throw new IndexOutOfRangeException("Level " + number + " does not exist.");
            return levels[number];
        }

        public bool goalAchieved()
        {
            foreach (LiteralNode literal in goals)
                if (literal.getLevel() == -1)
                    return false;
            return true;
        }

        public bool hasLeveledOff()
        {
            return leveledOff;
        }
    }
}

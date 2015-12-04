using Planning;
using Planning.Logic;
using Planning.Util;
using StateSpaceSearchProject;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace HeuristicSearchPlannerSGW
{
    public abstract class HSPHeuristic : StateHeuristic
    {

        public static readonly int INFINITY = int.MaxValue;
        private readonly ImmutableArray<Literal> literals;
        private readonly Dictionary<Literal, int> costMap = new Dictionary<Literal, int>();

        public HSPHeuristic(StateSpaceProblem problem)
            : base(problem)
        {
            List<Literal> literals = new List<Literal>();
            foreach (Step step in problem.steps)
            {
                everyLiteral(step.precondition, (literal)=> { literals.Add(literal); });
                everyLiteral(step.effect, (literal)=> { literals.Add(literal); });
            }
            this.literals = new ImmutableArray<Literal>(literals.ToArray());
        }

        private static void everyLiteral(Expression expression, Action<Literal> consumer) {
            if (expression is Literal)
			consumer.Invoke((Literal)expression);
		else if (expression is Conjunction)
			foreach (Expression argument in ((Conjunction)expression).arguments)
                everyLiteral(argument, consumer);
		else
			throw new InvalidOperationException(expression.GetType() + " not supported.");
        }

        protected virtual int cost(Literal literal) {
            if (costMap.ContainsKey(literal))
                return costMap[literal];
            else
                return INFINITY;
        }

        protected abstract int cost(Expression expression);

        public override int evaluate(State current)
        {
            costMap.Clear();
            foreach (Literal literal in literals)
            {
                if (current.isTrue(literal))
                    costMap[literal] = 0;
            }
            List<Step> steps = new List<Step>();
            foreach (Step step in problem.steps)
                steps.Add(step);
            bool again = true;
            while (again && cost(problem.goal) == INFINITY)
            {
                again = false;
                IEnumerator<Step> iterator = steps.GetEnumerator();
                while (iterator.MoveNext())
                {
                    Step step = iterator.Current;
                    int costValue = cost(step.precondition);
                    if (costValue != INFINITY)
                    {
                        again = setCost(step.effect, costValue + 1) || again;
                        //iterator.remove();
                    }
                }
            }
            return cost(problem.goal);
        }

        private bool setCost(Expression expression, int value) {
            if (expression is Literal) {
                Literal literal = (Literal)expression;
                int current = cost(literal);
                if (value < current)
                {
                    costMap[literal] = value;
                    return true;
                }
                else
                    return false;
            }
		else if (expression is Conjunction) {
                bool result = false;
                foreach (Expression argument in ((Conjunction)expression).arguments)
                    result = setCost(argument, value) || result;
                return result;
            }
		else
			throw new InvalidOperationException(expression.GetType() + " not supported.");
        }
    }

}

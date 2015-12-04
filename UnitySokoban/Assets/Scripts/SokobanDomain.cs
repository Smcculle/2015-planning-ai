using System;
using Planning;
using UnityEngine;
using Planning.Logic;
using Planning.Util;

public class SokobanDomain
{
    public static string GetDirectionFromStep(Step step)
    {
        if (step.effect is NAryBooleanExpression)
        {
            ImmutableArray<Expression> arguments = ((NAryBooleanExpression)step.effect).arguments;
            Point to = GetPlayerPostionTo(arguments);
            Point from = GetPlayerPositionFrom(arguments);

            if (from.x < to.x)
                return "Right";
            else if (from.x > to.x)
                return "Left";
            else if (from.y < to.y)
                return "Up";
            else if (from.y > to.y)
                return "Down";
        }
        return "N/A";
    }

    public static Point GetPlayerPosition(Step step)
    {
        if (step.effect is NAryBooleanExpression)
        {
            ImmutableArray<Expression> arguments = ((NAryBooleanExpression)step.effect).arguments;
            return GetPlayerPositionFrom(arguments);
        }
        return null;
    }

    private static Point GetPlayerPostionTo(ImmutableArray<Expression> arguments)
    {
        Point to = new Point();
        foreach (Expression argument in arguments)
        {
            if (argument is Predication)
            {
                Predication predication = (Predication)argument;
                if (predication.predicate == "has_player")
                {
                    string[] cell = predication.terms.get(0).name.Split('_');
                    to.x = Convert.ToInt32(cell[1]);
                    to.y = Convert.ToInt32(cell[2]);
                    return to;
                }
            }
        }
        return to;
    }

    private static Point GetPlayerPositionFrom(ImmutableArray<Expression> arguments)
    {
        Point from = new Point();
        foreach (Expression argument in arguments)
        {
            if (argument is Negation)
            {
                if (((Negation)argument).argument is Predication)
                {
                    Predication predication = (Predication)((Negation)argument).argument;
                    if (predication.predicate == "has_player")
                    {
                        string[] cell = predication.terms.get(0).name.Split('_');
                        from.x = Convert.ToInt32(cell[1]);
                        from.y = Convert.ToInt32(cell[2]);
                        return from;
                    }
                }
            }
        }
        return from;
    }
}
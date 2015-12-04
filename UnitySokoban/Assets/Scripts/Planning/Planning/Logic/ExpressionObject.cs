using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Logic
{
    /**
     * An implementation of
     * {@link edu.uno.ai.planning.logic.Expression} which provides
     * a default implementation of {@link java.lang.Object#equals(Object)}.
     * 
     * @author Stephen G. Ware
     * @ported Edward Thomas Garcia
     */
    public abstract class ExpressionObject : Expression
    {
        public virtual bool Equals(Formula other, Substitution substitution)
        {
            if (other is Formula)
                return Equals((Formula)other, Substitution.EMPTY);
            else
                return false;
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        public override bool Equals(object obj)
        {
            if (obj is Formula)
                return Equals(obj as Formula, Substitution.EMPTY);
            else
                return false;
        }

        public virtual int CompareTo(Formula other)
        {
            return ToString().CompareTo(other.ToString());
        }

        public abstract void Impose(MutableState state);
        public abstract bool IsGround();
        public abstract bool IsImposable();
        public abstract bool IsTestable();
        public abstract bool IsTrue(State state);
        public abstract Expression Negate();
        public abstract Expression Simplify();
        public abstract Expression Substitute(Substitution substitution);
        public abstract Expression ToCNF();
        public abstract Expression ToDNF();
        public abstract Bindings Unify(Formula other, Bindings bindings);

        Formula Formula.Substitute(Substitution substitution)
        {
            throw new NotImplementedException();
        }
    }
}


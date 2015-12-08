package edu.uno.ai.planning.jjsatplan;

import java.util.*;

/**
 * This class defines the edu.uno.ai.planning.BlackboxJJ.SATClause
 */
public class SATClause {
    /*Defines a list of clause in the given disjunction*/
    public Set<BooleanVariable> clause;
    public Set<BooleanVariable> positiveLiterals = new HashSet<BooleanVariable>();
    public Set<BooleanVariable> negativeLiterals = new HashSet<BooleanVariable>();

    public SATClause(){
        this.clause = new HashSet<>();
    }

    public SATClause(Set<BooleanVariable> clauses){
        this.clause = new HashSet<>();
        Iterator<BooleanVariable> iterator = clauses.iterator();
        while(iterator.hasNext())
            this.add((BooleanVariable)iterator.next());
    }

    public SATClause(BooleanVariable literal){
        this.clause = new HashSet<>();
        this.add(literal);
    }

    /**
     * Adds this clause to this disjunction
     * @param literal the clause to be added
     */
    public void add(BooleanVariable literal){
        this.clause.add(literal);
        if (literal.negation == true) {
            this.negativeLiterals.add(literal.negate());
        }
        else {
            this.positiveLiterals.add(literal);
        }
    }

    /**
     * Removes this clause from this disjunction
     * @param clause the clause to be added
     */
    public void remove(BooleanVariable clause){
        this.clause.remove(clause);
    }

    public void removeOpposite(BooleanVariable clause){
        BooleanVariable toRemove = new BooleanVariable(clause.name, clause.value, !clause.negation);
        remove(toRemove);
        System.out.println("Removed " + toRemove + " from " + this);
    }

    /**
     * Checks if the given clause contains the clause
     * @param clause the clause to be checked
     * @return the boolean value whether the clause is in this disjunction
     */
    public boolean contains(BooleanVariable clause){
        return this.clause.contains(clause);
    }

    /**
     * Checks if this disjunction contains the opposite clause
     * @param clause the clause to be removed
     * @return the boolean value whether the opposite clause is in this disjunction
     */
    public boolean containsOpposite(BooleanVariable clause){
        return this.contains(new BooleanVariable(clause.name, clause.value, !clause.negation));
    }

    /**
     * Returns the disjunction as the ArrayList<edu.uno.ai.planning.BlackboxJJ.BooleanVariable>
     * @return returns the list of booleanVariable
     */
    public ArrayList<BooleanVariable> convert(){
        return new ArrayList<>(this.clause);
    }

    /**
     * Gets the number of clause in this disjunction
     * @return the size of the disjunction
     */
    public int size(){
        return this.clause.size();
    }

    /**
     * Gets the next clause in this disjunction
     * @return the next clause in the disjunction or null if no clause
     */
    public BooleanVariable getNextClause() {
        if (size() == 0) return null;
        Iterator<BooleanVariable> iterator = this.clause.iterator();
        return (BooleanVariable)iterator.next();
    }

    public String toString(){
        String result = "";
        for (BooleanVariable bv : clause){
            result += (bv.negation ? "~" : "") + (bv.name + " V ");
        }
        return result.length() > 0 ? result.substring(0, result.length() -3) : result;
    }

    public HashSet<BooleanVariable> getSymbols(){
        HashSet<BooleanVariable> result = new HashSet<>();
        Iterator<BooleanVariable> iterator = this.clause.iterator();
        while(iterator.hasNext()){
            result.add((BooleanVariable)iterator.next());
        }
        return result;
    }

    @Override
    public boolean equals(Object other){
        if (!(other instanceof SATClause))
            return false;
        SATClause otherDisjunction = (SATClause) other;
        if (otherDisjunction.clause.size() != this.clause.size())
            return false;
        for (BooleanVariable bv : otherDisjunction.clause){
            if (!this.clause.contains(bv))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        String temp = "";
        for (BooleanVariable bv : this.clause){
            temp += bv.name + bv.negation;
        }
        return temp.hashCode();
    }

    public Boolean isUnitClause(){
        return this.clause.size() == 1;
    }
}

package edu.uno.ai.planning.jjsatplan;

import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Predication;

import java.util.*;

public class SATConjunction {
    /* this stores the list of disjunctions for this conjunction */
    Set<SATClause> disjunctions;
    List<BooleanVariable> symbols;

    /**
     * Instantiate this edu.uno.ai.planning.BlackboxJJ.SATConjunction
     */
    public SATConjunction(){
        this.disjunctions = new HashSet<>();
    }

//    public edu.uno.ai.planning.BlackboxJJ.SATConjunction(ArrayList<ArrayList<edu.uno.ai.planning.BlackboxJJ.BooleanVariable>> cnf){
//        this.disjunctions = new HashSet<>();
//        for (ArrayList<edu.uno.ai.planning.BlackboxJJ.BooleanVariable> disjunction : cnf){
//            this.disjunctions.add(new edu.uno.ai.planning.BlackboxJJ.SATClause((HashSet<edu.uno.ai.planning.BlackboxJJ.BooleanVariable>)disjunction));
//        }
//    }

    @SuppressWarnings("unchecked")
	public SATConjunction(SATConjunction cnf, SATClause disjunction){
        this.disjunctions = new HashSet<>();
        this.disjunctions = (HashSet<SATClause>)((HashSet<SATClause>)cnf.disjunctions).clone();
        this.disjunctions.add(disjunction);
    }

//    public edu.uno.ai.planning.BlackboxJJ.SATConjunction(ArrayList<edu.uno.ai.planning.BlackboxJJ.SATClause> disjunctions, boolean placeholder){
//        this.disjunctions = (HashSet<edu.uno.ai.planning.BlackboxJJ.SATClause>)((HashSet<edu.uno.ai.planning.BlackboxJJ.SATClause>)disjunctions).clone();
//    }

    /**
     * Adds this disjunction to this conjunction
     * @param disjunction the disjunction to be added to this conjunction
     */
    public void add(SATClause disjunction){
        this.disjunctions.add(disjunction);
    }

    public void add(SATConjunction conjunction){
        this.disjunctions.addAll(conjunction.disjunctions);
    }

    /**
     * Removes this disjunction from this conjunction
     * @param disjunction the disjunction to be removed from this conjunction
     */
    public void remove(SATClause disjunction){
        this.disjunctions.remove(disjunction);
    }

    /**
     * Checks if the given disjunction is in this conjunction
     * @param disjunction disjunction to check if it is in this conjunction
     */
    public boolean contains(SATClause disjunction){
        return this.disjunctions.contains(disjunction);
    }

    /**
     * Converts the given conjunction to ArrayList<ArrayList<edu.uno.ai.planning.BlackboxJJ.BooleanVariable>>
     * @return returns this conjuc
     */
    public ArrayList<ArrayList<BooleanVariable>> convert(){
        ArrayList<ArrayList<BooleanVariable>> result = new ArrayList<>();
        for (SATClause disjunction: disjunctions){
            if (disjunctions.size() > 0)
                result.add(disjunction.convert());
        }
        return result;
    }

    /**
     * Goes through all the disjunctions and removes the unit from each
     * @param unit the unit if contained by a disjunction is removed
     */
    public void removeDisjunctionContainigThisUnit(BooleanVariable unit){
        for (Iterator<SATClause> iterator = disjunctions.iterator(); iterator.hasNext();){
            SATClause nextDisjunction = iterator.next();
            if (nextDisjunction.contains(unit)) {
                System.out.println("Removing " + nextDisjunction);
                iterator.remove();
            }
        }
    }


     

    /**
     * Removes the opposite unit from each disjunction of this conjunction
     * @param unit the unit of which opposite is to be removed
     */
    public void removeOppositeUnitFromDisjunctions(BooleanVariable unit) {
        for (Iterator<SATClause> iterator = disjunctions.iterator(); iterator.hasNext();){
            SATClause nextDisjunction = iterator.next();
            if (nextDisjunction.containsOpposite(unit))
                nextDisjunction.removeOpposite(unit);
        }
    }

    /**
     * Gets the next disjunction from this conjunction which has only one clause in it
     * @return the next one clause disjunction
     */
    public BooleanVariable getUnitClause() {
        BooleanVariable result = null;
        for(SATClause disjunction: disjunctions){
            if (disjunction.clause.size() == 1) {
                result = disjunction.clause.iterator().next();
                break;
            }
        }
        return result;
    }

    public BooleanVariable getRandomLiteral(){
        for (SATClause disjunction : disjunctions){
            for(BooleanVariable literal : disjunction.clause){
                return literal;
            }
        }
        return null;
    }

    public int size(){
        return  this.disjunctions.size();
    }

    public boolean hasNullClause() {
        for (SATClause disjunction: disjunctions){
            if (disjunction.size() == 0)
                return true;
        }
        return false;
    }

    public String toString(){
        String result = "";
        for (SATClause disjunction : this.disjunctions){
            for (BooleanVariable BV : disjunction.clause){
                result += (BV.negation ? "~" : "") + BV.name + " V ";
            }
            if (!result.isEmpty() && result.endsWith(" V "))
                result = result.substring(0, result.length() - 3);
            result += "\n";
        }
        return result;
    }

    /**
     * Converts the given expression to the disjunction and adds it to the CNF
     * @param expression the expression to be converted to the disjunction
     */
    public void add(Expression expression){

            if (expression instanceof Predication){
        }
    }

    private boolean isPure(BooleanVariable literal){
        for (SATClause disjunction : this.disjunctions){
            if (disjunction == null) return  false;
            if (disjunction.contains(literal.negate()))
                return false;
        }
        return true;
    }

    /**
     * Get next pure literal
     */
    public BooleanVariable getNextPureLiteral(){
        for(SATClause disjunction : this.disjunctions){
            for(BooleanVariable literal : disjunction.clause){
                if(literal == null) return null;
                if (isPure(literal)) return literal;
            }
        }
        return null;
    }

//    public edu.uno.ai.planning.BlackboxJJ.SATConjunction copy(){
//        return new edu.uno.ai.planning.BlackboxJJ.SATConjunction(disjunctions, true);
//    }

    public ArrayList<BooleanVariable> getSymbols(){
        HashSet<BooleanVariable> result = new HashSet<>();
        Iterator<SATClause> iterator = this.disjunctions.iterator();
        while(iterator.hasNext()){
            HashSet<BooleanVariable> symbolsFromDisjunction = ((SATClause)iterator.next()).getSymbols();

            Iterator<BooleanVariable> iterator2 = symbolsFromDisjunction.iterator();
            while (iterator2.hasNext()){
                BooleanVariable tobeadded = (BooleanVariable)iterator2.next();
                if (tobeadded.negation == true)
                    tobeadded = tobeadded.negate();
                if (!result.contains(tobeadded))
                    result.add(tobeadded);
            }
        }
        return new ArrayList<>(result);
    }
}

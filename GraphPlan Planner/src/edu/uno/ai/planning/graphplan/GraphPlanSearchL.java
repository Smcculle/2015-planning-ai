package edu.uno.ai.planning.graphplan;
 
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
 
import edu.uno.ai.planning.Plan;
import edu.uno.ai.planning.Problem;
import edu.uno.ai.planning.Search;
import edu.uno.ai.planning.Step;
import edu.uno.ai.planning.logic.Conjunction;
import edu.uno.ai.planning.logic.Disjunction;
import edu.uno.ai.planning.logic.Expression;
import edu.uno.ai.planning.logic.Literal;
import edu.uno.ai.planning.logic.NegatedLiteral;
import edu.uno.ai.planning.ss.TotalOrderPlan;
 
public class GraphPlanSearchL extends Search{
       
        public class NullOutputStream extends OutputStream {
                  @Override
                  public void write(int b) throws IOException {
                  }
                }      
       
        /** PlanGraph used in solving the problem **/
        private PlanGraph pg;
       
        /** Actual problem to solve. **/
        public final Problem problem;
       
        private ArrayList<GraphPlanNode> nodeList = new ArrayList<GraphPlanNode>();
       
        private GraphPlanNode goal;
       
        private HashMap<Integer, GraphPlanNode> nodes = new HashMap<Integer, GraphPlanNode>();
       
        private HashMap<Integer, ArrayList<PlanGraphStep>> mutexLists = new HashMap<Integer, ArrayList<PlanGraphStep>>();
       
        private int extendedNodes;
       
        private int visitedNodes;
       
        private int currentMaxLevel = -1;
       
        private ArrayList<PlanGraphLiteral> allStart = new ArrayList<PlanGraphLiteral>();
 
        private ArrayList<PlanGraphLiteral> firstLits = new ArrayList<PlanGraphLiteral>();
       
        private int currentLevel = -1;
       
        private TotalOrderPlan solution1;
       
        private int old1 = -1;
        private int old2 = -2;
        private boolean flag = false;
       
        private boolean finished = false;
       
        private boolean success = false;
       
        private boolean failure = false;
       
        Scanner sc;
       
        private final ArrayList<PlanGraphLiteral> initialLiterals = new ArrayList<PlanGraphLiteral>();
        private ArrayList<PlanGraphLiteral> goalLiterals = new ArrayList<PlanGraphLiteral>();
       
        private int failedat = -1;
       
        int limit = -1;
       
        //Used to determine if we are creating the highest level node.
        boolean firstCall = true;
       
        public GraphPlanSearchL(Problem problem) throws FileNotFoundException{
               
                super(problem);
//              System.setOut(new PrintStream(new NullOutputStream()));
 
               
               
//              System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("/home/ichi1504/eclipseout.txt")), true));
               
                this.problem = problem;
                this.extendedNodes = 0;
                this.visitedNodes = 0;
                pg = new PlanGraph(this.problem, true);
                currentMaxLevel = pg.countLevels() - 1;
               
                for (Literal l : expressionToLiterals(problem.initial.toExpression())){
                        initialLiterals.add(new PlanGraphLiteral(l));
                }
               
                /**System.out.print("hi the initial literals are ");
                for (PlanGraphLiteral l : initialLiterals){
                        System.out.print(l.toString() + "; ");
                }
                System.out.println("");
                */
               
                allStart.addAll(initialLiterals);
                allStart.addAll(getNegations(initialLiterals));
                goal = new GraphPlanNode();
                for (Literal l : expressionToLiterals(problem.goal)){
                        goal.addLiterals(new PlanGraphLiteral(l));
                        goalLiterals.add(new PlanGraphLiteral(l));
                }
                //if (true) System.exit(0);
                sc = new Scanner(System.in);
               
        }
 
       
        /**
         * Extract meaningful words from the steps to check against one another later.
         * Spaces, (, ) aren't useful, we just want the verbs.
         * Nouns from the literals will be removed.
         * This will cause the effect of "load cargo NOLA" to be mutually exclusive against "load cargo London",
         * because they will both contain the relevant word load.
         * @param step to get relevant words from
         * @return an ArrayList of all the important words
         */
        public ArrayList<String> getStepRelevantWords(PlanGraphStep step){
                String p = step.getStep().precondition.toString();
                String e = step.getStep().effect.toString();
                String s = step.toString();
//              String temp = "";
//              int rm = -1;
//              char c = p.charAt(1);
                int i = 2;
//              int j = i;
               
                ArrayList<String> nouns = new ArrayList<String>();
               
                String[] words = s.split(" ");
                //System.out.print("\nWords in step: ");
 
                for (String str : words){
                        if (str.charAt(0) == '('){
                                str = str.substring(1);
                        }
                        for (int k = 1; k < str.length(); k++){
                                //System.out.println(str.charAt(k));
                                if (str.charAt(k) == ')'){
                                        str = str.substring(0, k);
                                }
                        }
                        //System.out.print(str + ", ");
                }
 
                words = e.split(" ");
                //System.out.print("\nWords in effect: ");
                for (String str : words){
                        if (str.charAt(0) == '('){
                                str = str.substring(1);
                        }
                        for (int k = 1; k < str.length(); k++){
                                //System.out.println(str.charAt(k));
                                if (str.charAt(k) == ')'){
                                        str = str.substring(0, k);
                                }
                        }
                        if (str.equals("at") || str.equals("and") || str.equals("not") || str.equals("in")){
                                continue;
                        }
                        nouns.add(str);
                        //System.out.print(str + ", ");
                }              
                System.out.print("\n");
 
                words = p.split(" ");
                //System.out.print("\nWords in prec: ");
                for (String str : words){
                        if (str.charAt(0) == '('){
                                str = str.substring(1);
                        //      System.out.println("( found and deleted from " + str);
                        }
                        for (int k = 1; k < str.length(); k++){
                                //System.out.println(str.charAt(k));
                                if (str.charAt(k) == ')'){
                                        str = str.substring(0, k);
                                }
                        }
                        if (str.equals("at") || str.equals("and") || str.equals("not") || str.equals("in")){
                                continue;
                        }
                        nouns.add(str);
                //      System.out.print(str + ", ");
                }                      
                //System.out.println("\n");
 
                words = s.split(" ");
               
               
               
                ArrayList<String> r = new ArrayList<String>(Arrays.asList(words));
        //      ArrayList<String> rdup = new ArrayList<String>(Arrays.asList(words));
               
        //      System.out.println("Strings in step :" );
//              for (String ttt : r){
                //      System.out.print(ttt + "; ");
//              }
        //      System.out.println("\n");
 
 
                for (int q = 0; q < r.size(); q++){
                        if (r.get(q).charAt(0) == '('){
                                r.set(q, r.get(q).substring(1));
                        }
                        for (int w = 0; w < r.get(q).length(); w++){
                                if (r.get(q).charAt(w) == ')'){
                                        r.set(q,  r.get(q).substring(0, w));
                                }
                        }
                }
               
                int o = -1;
                for (String sstr : nouns){
                //      System.out.println("sstr " + sstr);
                        o = r.indexOf(sstr);
                        if (o != -1){
                                r.remove(o);
                        }
                }
 
 
                //System.out.println("Strings in step now :" );
                for (String ttt : r){
                //      System.out.print(ttt + "; ");
                }
        //      System.out.println("\n");              
               
                return r;
 
        }
 
       
        public Plan search(){
               
                /**System.out.print("Steps and their children by their method vs mine.\n");
                for (PlanGraphStep s : pg.getAllPossiblePlanGraphSteps()){
                        System.out.println("Step: " + s.toString());
                        for (PlanGraphLiteral l : s.getChildNodes()){
                                System.out.print(l.toString() + "; ");
                        }
                        System.out.println("\n");
                        for (PlanGraphLiteral l : getEffects(s)){
                                System.out.print(l.toString());
                        }
                        System.out.println("\n");
                }
                if (true) return null;
                */
                if (true) return search3();
               
                TotalOrderPlan solution = null;
                //System.out.println(pg);
               
/**             for (PlanGraphStep step : pg.getAllPossiblePlanGraphSteps()){
                        System.out.print("\nStep originally: " + step.toString() + " and after removing nouns: " );
                        for (String s : getStepRelevantWords(step)){
                                System.out.print(s + " ");
                        }
                }
                */
               
                /**
                System.out.println("Checking mutexes.........\n\n");
                for (PlanGraphStep s1 : pg.getAllPossiblePlanGraphSteps()){
                        for (PlanGraphStep s2 : pg.getAllPossiblePlanGraphSteps()){
                                isMutex(s1, s2);
                        }
                }
*/
               
               
/**             for (PlanGraphStep step1 : pg.getAllPossiblePlanGraphSteps()){
                        for (PlanGraphStep step2 : pg.getAllPossiblePlanGraphSteps()){
                                //System.out.println(step1.toString() + " and " + step2.toString() + " are mutex with each other: " + isMutex(step1, step2));
                                int stop = 0;
                                int start = 0;
                               
                                for (int i = 0; i < step1.toString().length() && i < step2.toString().length(); i++){
                                        if (step1.toString().charAt(i) == step2.toString().charAt(i)){
                                                start = i;
                                                break;
                                        }
                                }
                               
                                if (start == 0){
                                       
                                }
                               
                        }
                }
                */
               
               
//              print();
                //tryMRemovalSteps();
                //tryApplicableSteps();
                /**
                for (PlanGraphStep step : pg.getAllPossiblePlanGraphSteps()){
                        System.out.print("Step " + step.toString() + " parents: ");
                        for (PlanGraphLiteral lit : step.getParentNodes()){
                                System.out.print(lit.toString() + ", ");
                        }
                        System.out.print("\n");
                        System.out.print("\nchildren: ");
                        for (PlanGraphLiteral lit : step.getChildNodes()){
                                System.out.print(lit.toString() + ", ");
                        }
                        System.out.print("\n\n");
                }
                System.out.print("\n\n");
                System.out.print("\n\n");
                System.out.print("\n\n");*/
 
                for (PlanGraphLiteral lit : pg.getAllPossiblePlanGraphEffects())  {
                        System.out.print("Lit " + lit.toString() + " parents: ");
                        for (PlanGraphStep step : lit.getParentNodes()){
                                System.out.print(step.toString() + ", ");
                        }
                        System.out.print("\n");
                        System.out.print("\nchildren: ");
                        for (PlanGraphStep step : lit.getChildNodes()){
                                System.out.print(step.toString() + ", ");
                        }
                        System.out.print("\n\n");
                }              
//              */
                //tryApplicableSteps();
        //      tryApplicable2();
//              search2();
/**             for (PlanGraphLiteral l : goal.getLiterals()){
                        System.out.println(l.toString());
                }
                System.out.println("dfkdhgkjdjg\n\n\n\n\n");
                for (PlanGraphLiteral l : initialLiterals){
                        System.out.println(l.toString());
                }
                */
               
                System.out.println("here");
                goDown();
                if (true) System.exit(0);
                //search2();
                 
                if (firstCall == true)
                {
                        finished = checkGoalInInitial();
                        firstCall = false;
                        createGoalNode();
//                      goalReached(nodes.get(currentLevel).getLiterals());
//                      System.out.println(nodes.get(currentLevel).getLiterals());
 
                }
                       
                searchAux();
 
                if (finished == true){
                        solution = createTotalOrderPlan();
                        return solution;
                }
 
//              repeatPreviousLevel();
//              if (finished = false){
//     
//                     
//                      repeatPreviousLevel();
//              }
 
                return solution;
        }
       
        public void tryApplicableSteps(){
                ArrayList<PlanGraphLiteral> ls = new ArrayList<PlanGraphLiteral>();
                for (PlanGraphLiteral l : pg.getAllPossiblePlanGraphEffects()){
                        ls.add(l);
                        System.out.println("Applicable steps for the literal " + l.toString() + " are: ");
                        for (PlanGraphStep s : getApplicableSteps(ls)){
                                System.out.print(s.toString() + ", ");
                        }
                        System.out.println("\nxxxx\nxxxx\nxxxx\nxxxx\n");
                        ls.clear();
                }
               
        }
       
        public void tryApplicable2(){
                System.out.println("Testing mutex removal on steps...\n");
               
                ArrayList<PlanGraphLiteral> sl1 = new ArrayList<PlanGraphLiteral>();
                ArrayList<PlanGraphLiteral> sl2 = new ArrayList<PlanGraphLiteral>();
                ArrayList<PlanGraphLiteral> sl3 = new ArrayList<PlanGraphLiteral>();
                ArrayList<PlanGraphLiteral> sl4 = new ArrayList<PlanGraphLiteral>();
 
                int i = 0;
                for (PlanGraphLiteral s : pg.getAllPossiblePlanGraphEffects()  ){
                        if (i % 3 == 0){
                                sl1.add(s);
                               
                        }
                        else if (i % 3 == 1){
                                sl2.add(s);
                        }
                        else if (i % 3 == 2){
                                sl3.add(s);
                        }
                        i++;
                }
               
                for (int j = 1; j < 4; j++){
                        System.out.print("List " + j + " literals are: ");
                        if (j == 1){
                                for (PlanGraphLiteral s : sl1){
                                        System.out.print(s.toString() + ", ");
                                }
                                System.out.println("After removing mutexes:");
                                sl1 = removeMutex(sl1);
                                for (PlanGraphLiteral s : sl1){
                                        System.out.print(s.toString() + ", ");
                                }
                                System.out.println("\nAnd now, the applicable steps: ");
                                for (PlanGraphStep s : getApplicableSteps(sl1)){
                                        System.out.print(s.toString() + ", ");                         
                                }
                        }
                        if (j == 2){
                                for (PlanGraphLiteral s : sl2){
                                        System.out.print(s.toString() + ", ");
                                }
                                System.out.println("After removing mutexes:");
                                sl2 = removeMutex(sl2);
                                for (PlanGraphLiteral s : sl2){
                                        System.out.print(s.toString() + ", ");
                                }
                                System.out.println("\nAnd now, the applicable steps: ");
                                for (PlanGraphStep s : getApplicableSteps(sl2)){
                                        System.out.print(s.toString() + ", ");                         
                                }
                        }
                        if (j == 3){
                                for (PlanGraphLiteral s : sl3){
                                        System.out.print(s.toString() + ", ");
                                }
                                System.out.println("After removing mutexes:");
                                sl3 = removeMutex(sl3);
       
                                for (PlanGraphLiteral s : sl3){
                                        System.out.print(s.toString() + ", ");
                                }
                                System.out.println("\nAnd now, the applicable steps: ");
                                for (PlanGraphStep s : getApplicableSteps(sl3)){
                                        System.out.print(s.toString() + ", ");                         
                                }
                        }
                        System.out.println("\nContinuing...");
                }
        }
       
        public void tryMRemovalSteps(){
                System.out.println("Testing mutex removal on steps...\n");
               
                ArrayList<PlanGraphStep> sl1 = new ArrayList<PlanGraphStep>();
                ArrayList<PlanGraphStep> sl2 = new ArrayList<PlanGraphStep>();
                ArrayList<PlanGraphStep> sl3 = new ArrayList<PlanGraphStep>();
                ArrayList<PlanGraphStep> sl4 = new ArrayList<PlanGraphStep>();
 
                int i = 0;
                for (PlanGraphStep s : pg.getAllPossiblePlanGraphSteps()){
                        if (i % 3 == 0){
                                sl1.add(s);
                               
                        }
                        else if (i % 3 == 1){
                                sl2.add(s);
                        }
                        else if (i % 3 == 2){
                                sl3.add(s);
                        }
                        i++;
                }
               
                for (int j = 1; j < 4; j++){
                        System.out.print("List " + j + " steps are: ");
                        if (j == 1){
                                for (PlanGraphStep s : sl1){
                                        System.out.print(s.toString() + ", ");
                                }
                                System.out.println("\nAnd now, after removing mutexes: ");
                                for (PlanGraphStep s : checkMutexSteps2(sl1)){
                                        System.out.print(s.toString() + ", ");                         
                                }
                        }
                        if (j == 2){
                                for (PlanGraphStep s : sl2){
                                        System.out.print(s.toString() + ", ");
                                }
                                System.out.println("\nAnd now, after removing mutexes: ");
                                for (PlanGraphStep s : checkMutexSteps2(sl2)){
                                        System.out.print(s.toString() + ", ");                         
                                }
                        }
                        if (j == 3){
                                for (PlanGraphStep s : sl3){
                                        System.out.print(s.toString() + ", ");
                                }
                                System.out.println("\nAnd now, after removing mutexes: ");
                                for (PlanGraphStep s : checkMutexSteps2(sl3)){
                                        System.out.print(s.toString() + ", ");                         
                                }
                        }
                        System.out.println("\nContinuing...");
                }
               
        }
       
        public void print(){
                System.out.println("Printing...\n");
                for (PlanGraphStep s : pg.getAllPossiblePlanGraphSteps()){
                        System.out.print("\n\nStep name: " + s.toString() + " and its child literals: ");
                        ArrayList<PlanGraphStep> sl = new ArrayList<PlanGraphStep>();
                        sl.add(s);
                        for (PlanGraphLiteral l : getFX(sl)){
                                System.out.print(l.toString() + "; ");
                        }
                        System.out.println("\n");
                }
        }
       
        public void start(){
                GraphPlanNode st = new GraphPlanNode(0);
               
                ArrayList<PlanGraphLiteral> tempGoalList = new ArrayList<PlanGraphLiteral>();
                //ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
                Set<PlanGraphStep> deleteRepeatedSteps = new HashSet<PlanGraphStep>();
                for (Literal lit: expressionToLiterals(problem.goal)){
                        tempGoalList.add(pg.getPlanGraphLiteral(lit));
                }
       
 
        /*      ArrayList<PlanGraphStep> newSteps = new ArrayList<PlanGraphStep>();
                for (PlanGraphStep x: pg.getAllPossiblePlanGraphSteps()){
                        if (x.existsAtLevel(currentLevel)){
                                newSteps.add(x);
                        }
                }
                */
/**
                for (PlanGraphLiteral goal: tempGoalList){
                        for (PlanGraphStep step: goal.getParentNodes()){
                                deleteRepeatedSteps.add(step);
                                if (!step.existsAtLevel(currentLevel)){
                                        deleteRepeatedSteps.remove(step);
                                }
                        }
                }
                */
               
                //tempSteps.addAll(deleteRepeatedSteps);
                st.setLits(tempGoalList);
                //defineNode(checkMutexSteps(tempSteps), tempGoalList, currentMaxLevel);
                nodeList.add(st);
                System.out.println("bye");
        }
       
        public void search2(){
                System.out.println("hi");
                if (nodeList.size() == 0){
                        start();
                }
                System.out.println("HI");
                int temp = 0;
                int x = 0;
                while (x == 0){
                        System.out.println("node size iwsss " + nodeList.size());
//              for (x = 0; x < 20; x++){
                /**ArrayList<PlanGraphLiteral> litsnow = nodeList.get(0).getLiterals();
                if (goalReached(litsnow)){
                        System.out.println("\n\n\nsdfnsd\ndghfghf\nhf\nh\nfh\n\n");
                }
                */
                for ( int i = nodeList.size() - 2; i > 0; i--){
                        System.out.println("sdffffffff");
                        ArrayList<PlanGraphStep> steps = getApplicableSteps(nodeList.get(i + 1).getLiterals());
                        steps = removeMutexes(steps);
                        System.out.print("Applicable steps");
                        for (PlanGraphStep step : steps){
                                System.out.print(step.toString() + " ");
                        }
                        System.out.println("\nAt level " + i + " the steps are " + steps.size() + " items long.");
                        temp = steps.size();
                        if (steps.size() == 0){
                                x++;
                                throw new ArithmeticException("you lost");
                        }
                        steps = removeMutexes(steps);
                        steps = removeNonGoalSteps(steps);
                        //if (steps.size() == 0 || old1 == old2){
                        if (false){
                                //throw new ArithmeticException("you lost");
                                System.out.println("YOU LOST");
                                System.exit(0);
                        }
                        ArrayList<PlanGraphLiteral> lits = new ArrayList<PlanGraphLiteral>();
                        lits.addAll(getFX(steps));
                        System.out.print("Literals length " + lits.size() + " and contents ");
                        for (PlanGraphLiteral l : lits){
                                System.out.print(l.toString() + ", ");
                        }
                //      lits.addAll(nodeList.get(i+1).getLiterals());
                        lits = removeMutex(lits);
                        HashSet<PlanGraphLiteral> sl = new HashSet<PlanGraphLiteral>();
                        for (PlanGraphLiteral l : lits){
                                sl.add(l);
                        }
                        lits.clear();
                        lits.addAll(sl);
                        System.out.print("\nNow, Literals length " + lits.size() + " and contents ");
                        for (PlanGraphLiteral l : lits){
                                System.out.print(l.toString() + ", ");
                        }
                        for (PlanGraphLiteral l : nodeList.get(i + 1).getLiterals()){
                                lits = addPrevLits(lits, l);
                        }
                        HashSet<PlanGraphLiteral> sl1 = new HashSet<PlanGraphLiteral>();
                        for (PlanGraphLiteral l : lits){
                                sl1.add(l);
                        }
                        lits.clear();
                        lits.addAll(sl1);
                        System.out.print("\nAgain, Literals length " + lits.size() + " and contents ");
                        for (PlanGraphLiteral l : lits){
                                System.out.print(l.toString() + ", ");
                        }              
                        nodeList.get(i + 1).setSteps(steps);
                //      nodeList.get(i).setLits(getFX(steps));
                        nodeList.get(i).setLits(lits);
                }
                if (!flag){
                        old1 = temp;
                        flag = true;
                }
                else{
                        old2 = temp;
                }
               
 
                ArrayList<PlanGraphLiteral> litsnow = nodeList.get(0).getLiterals();
                if (goalReached2(litsnow)){
                        x++;
                        System.out.println("\n\n\nsdfnsd\ndghfghf\nhf\nh\nfh\n\n");
                        throw new ArithmeticException("you won");
                }
                //System.gc();
                newNodes();
                }
        }
       
        public ArrayList<PlanGraphLiteral> addPrevLits(ArrayList<PlanGraphLiteral> lits, PlanGraphLiteral l){
                for (PlanGraphLiteral lit : lits){
                        if (isMutex(lit, l)){
                                return lits;
                        }
                }
                lits.add(l);
                return lits;
        }
       
        public void newNodes(){
                GraphPlanNode n = nodeList.get(nodeList.size() - 1);    // goal node
                int size = nodeList.size();                                                            
                nodeList.clear();
                n.clearSteps();
                for (int i = 0; i < size; i++){
                        nodeList.add(new GraphPlanNode());
                }
                nodeList.add(n);
        }
       
        public void searchAux(){
 
                while (currentLevel > 0){
                        createNewNode();
                //      finished = goalReached(nodes.get(currentLevel).getLiterals());
                        if (currentLevel == 0){
                                if (!pg.isLeveledOff() && finished == false){
                                        pg.extend();
                                        currentMaxLevel++;
                                        createGoalNode();
                                }
                        }
                }
        }
       
        public ArrayList<PlanGraphStep> removeNonGoalSteps(ArrayList<PlanGraphStep> in){
                for (PlanGraphStep s : in){
                        for (PlanGraphLiteral lit : goal.getLiterals()){
                                if (!isStepApplicableEffect(s, lit)){
                                        in.remove(s);
                                        return removeNonGoalSteps(in);
                                }
                        }
                }
                return in;
        }
       
//      public void navigateGraph(){
//
//              int tempCurrentLevel = 0;
//              goalReached(nodes.get(currentLevel).getLiterals());
//              if (currentMaxLevel == 0){
//                      goalReached(nodes.get(currentLevel).getLiterals());
//                      if (finished == false){
//                              pg.extend();
//                              currentMaxLevel++;
//                              currentLevel = currentMaxLevel;
//                              createGoalNode();
//                      }
//              }
//              if (currentLevel == 0) {
//                      if (finished == false){
//                              if (!pg.isLeveledOff()){
//                                      pg.extend();
//                                      currentMaxLevel++;
//                                      currentLevel = currentMaxLevel;
//                                      createGoalNode();
//                              }
//                             
//                              tempCurrentLevel = tempCurrentLevel + 1;
//             
//                      }
//              }
//              if (currentLevel == 0){
//                      if (finished == false){
//                             
//                      }
//              }
//      }
       
        //Create TotalOrderPlan which contains correct steps for solution
        public TotalOrderPlan createTotalOrderPlan(){
                TotalOrderPlan solution = new TotalOrderPlan();
                ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
                for (int i = 0; i <= currentMaxLevel; i++){
                        for (PlanGraphStep step: nodes.get(i).getSteps()){
//                              solution = solution.addStep(step.getStep());
                                steps.add(step);
                        }
                }
                steps = removePersistence(steps);
                for (PlanGraphStep s : steps){
                        solution = solution.addStep(s.getStep());
                }
                Iterator<Step> x = solution.iterator();
                while(x.hasNext()){
                //      System.out.println(x.next());
                }
                return solution;
               
        }
       
        public ArrayList<PlanGraphStep> removePersistence(ArrayList<PlanGraphStep> steps) {
                ArrayList<PlanGraphStep> newSteps = new ArrayList<PlanGraphStep>();
                while (!steps.isEmpty()) {
                        PlanGraphStep candidateStep = steps.remove(0);
                        boolean shouldAdd = true;
                        for (int i = 0; i < newSteps.size(); i++) {
                                if (candidateStep.toString().contains("Persistence")){
                                        shouldAdd = false;
                                        break; //No need to keep looking
                                }
                        }
                        if (shouldAdd) {
                                newSteps.add(candidateStep);
                        }
                }
                return newSteps;
        }
       
        /**
         * Creates a node based upon the goal literals
         */
       
        public void createGoalNode(){
                nodes.clear();
                currentLevel = currentMaxLevel;
                ArrayList<PlanGraphLiteral> tempGoalList = new ArrayList<PlanGraphLiteral>();
                ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
                Set<PlanGraphStep> deleteRepeatedSteps = new HashSet<PlanGraphStep>();
                for (Literal lit: expressionToLiterals(problem.goal)){
                        tempGoalList.add(pg.getPlanGraphLiteral(lit));
                }
       
 
                ArrayList<PlanGraphStep> newSteps = new ArrayList<PlanGraphStep>();
                for (PlanGraphStep x: pg.getAllPossiblePlanGraphSteps()){
                        if (x.existsAtLevel(currentLevel)){
                                newSteps.add(x);
                        }
                }
               
//              for (PlanGraph x: newSteps){
//                      expressionToLiterals(x.getStep().effect)
//              }
               
                //System.out.println(newSteps);
               
               
//             
//                              for (PlanGraphLiteral y: tempGoalList){
//                                      System.out.println(y);;
//                                      for (Literal z: ){
//                                              if (z == y){
//                                                     
//                                              }
//                                      }      
//                              }
                       
               
       
               
 
                for (PlanGraphLiteral goal: tempGoalList){
                        for (PlanGraphStep step: goal.getParentNodes()){
                                deleteRepeatedSteps.add(step);
                                if (!step.existsAtLevel(currentLevel)){
                                        deleteRepeatedSteps.remove(step);
                                }
                        }
                }
               
                tempSteps.addAll(deleteRepeatedSteps);
                defineNode(checkMutexSteps(tempSteps), tempGoalList, currentMaxLevel);
                //System.out.println(nodes.get(currentMaxLevel).getLiterals());
                //System.out.println(nodes.get(currentMaxLevel).getSteps());
 
                extendedNodes++;
                visitedNodes++;
        }
       
        public void createNewNode(){
                GraphPlanNode tempNode = nodes.get(currentLevel);
                currentLevel--;
                ArrayList<PlanGraphLiteral> tempLiterals = new ArrayList<PlanGraphLiteral>();
                ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
                Set<PlanGraphLiteral> deleteRepeats = new HashSet<PlanGraphLiteral>();
                Set<PlanGraphStep> deleteRepeatedSteps = new HashSet<PlanGraphStep>();
               
                for (PlanGraphStep step: tempNode.getSteps()){
                        for (PlanGraphLiteral lit: step.getParentNodes()){
                                deleteRepeats.add(lit);
                                if (!lit.existsAtLevel(currentLevel)){
                                        deleteRepeats.remove(lit);
                                }
                        }
                }
               
               
                tempLiterals.addAll(deleteRepeats);
                tempLiterals = checkForFailure(tempLiterals);
               
                for (PlanGraphLiteral goal: tempLiterals){
                        for (PlanGraphStep step: goal.getParentNodes()){
                                deleteRepeatedSteps.add(step);
                                if (!step.existsAtLevel(currentLevel)){
                                        deleteRepeatedSteps.remove(step);
                                }
                        }
                }
               
                tempSteps.addAll(deleteRepeatedSteps);
 
                defineNode(checkMutexSteps(tempSteps),tempLiterals,currentLevel);
                extendedNodes++;
                visitedNodes++;
                //System.out.println(nodes.get(currentLevel).getLiterals() + " Level: " + currentLevel);
                //System.out.println(nodes.get(currentLevel).getSteps());
        }
       
        public ArrayList<PlanGraphLiteral> checkForFailure(ArrayList<PlanGraphLiteral> list){  
                for (int i = 0; i < list.size(); i++){
                        for (int j = i +1; j < list.size(); j++){
                                if (isMutex(list.get(i), list.get(j))){
                                        list.remove(i);
                                        checkForFailure(list);
                                }
                        }
                }
               
                return list;
               
        }
       
        public ArrayList<PlanGraphStep> checkMutexSteps(ArrayList<PlanGraphStep> list){
                for (int i = 0; i < list.size(); i++){
                        for (int j = i +1; j < list.size(); j++){
                                if (isMutex(list.get(i), list.get(j))){
//                                      System.out.println(list.get(i));
                                        list.remove(i);
                                        checkMutexSteps(list);
                                }
                        }
                }
               
                return list;
               
        }
       
        public ArrayList<PlanGraphStep> checkMutexSteps2(ArrayList<PlanGraphStep> list){       
                CopyOnWriteArrayList<PlanGraphStep> steps = new CopyOnWriteArrayList<PlanGraphStep>();
                steps.addAll(list);
                for (int i = 0; i < steps.size(); i++){
                        for (int j = i +1; j < steps.size(); j++){
                                if (isMutex(steps.get(i), steps.get(j))){
//                                      System.out.println(list.get(i));
                                        steps.remove(i);
                                        //checkMutexSteps(list);
                                }
                        }
                }
                list.clear();
                list.addAll(steps);
                return list;
               
        }      
       
        public ArrayList<PlanGraphLiteral> getFX(ArrayList<PlanGraphStep> steps){
                ArrayList<PlanGraphLiteral> lits = new ArrayList<PlanGraphLiteral>();
                for (PlanGraphStep s : steps){
                        lits.addAll(s.getChildNodes());
                }
                return lits;
        }
       
        public ArrayList<PlanGraphStep> getStepsFromAllPreconditions(ArrayList<PlanGraphLiteral> lits){
                ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
                for (PlanGraphLiteral l : lits){
                        steps.addAll(getStepsFromPrecondition(l));
                }
               
//              Set<PlanGraphStep> stepSet = new HashSet<PlanGraphStep>();
//              stepSet.addAll(steps);
//              steps.clear();
//              steps.addAll(stepSet);
               
//              steps = checkMutexSteps(steps);
                return steps;
        }
       
        public ArrayList<PlanGraphStep> getStepsFromPrecondition(PlanGraphLiteral p){
                ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
                for (PlanGraphStep s : pg.getAllPossiblePlanGraphSteps()){
                        if (isStepApplicablePrecon(s, p)) steps.add(s);
                }
                return steps;
        }
 
        public boolean isStepApplicableEffect(PlanGraphStep step, PlanGraphLiteral literal){
                        return ( (literal.getLiteral().equals(step.getStep().effect)) );
        }
 
        public boolean isStepApplicablePrecon(PlanGraphStep step, PlanGraphLiteral literal){
                return ( (literal.getLiteral().equals(step.getStep().precondition)) );
}      
       
       
        /**
        public void repeatPreviousLevel(){
                currentLevel++;
                GraphPlanNode tempNode = nodes.get(currentLevel);
                ArrayList<PlanGraphLiteral> tempLiterals = new ArrayList<PlanGraphLiteral>();
                ArrayList<PlanGraphStep> tempSteps = new ArrayList<PlanGraphStep>();
                Set<PlanGraphLiteral> deleteRepeats = new HashSet<PlanGraphLiteral>();
               
               
                for (PlanGraphStep step: tempNode.getSteps()){
                        for (PlanGraphLiteral lit: step.getParentNodes()){
                                deleteRepeats.add(lit);
                        }
                }
               
                tempLiterals.addAll(nodes.get(currentLevel).getLiterals());
               
                for (PlanGraphLiteral goal: tempLiterals){
                        for (PlanGraphStep step: goal.getParentNodes()){
                               
                                        tempSteps.add(step);
               
                        }
                }
               
 
                currentLevel--;
                goalReached(tempLiterals);
                defineNode(dealWithMutex(mutexLists.get(currentLevel)),tempLiterals,currentLevel);
       
               
                extendedNodes++;
                visitedNodes++;
        }
        */
       
       
        /**
        public boolean goalReached(ArrayList<PlanGraphLiteral> tempLiterals){
                ArrayList<Literal> nonPGLiterals = new ArrayList<Literal>();
        //      ArrayList<Literal> initialLiterals = new ArrayList<Literal>();
               
                for(PlanGraphLiteral lit:tempLiterals){
                        nonPGLiterals.add(lit.getLiteral());
                }
               
                for (Literal lit : initialLiterals){
                        if (!nonPGLiterals.contains(lit)) return false;
                }
                return true;
               
        }
        */
       
        public boolean goalReached2(ArrayList<PlanGraphLiteral> currentLits){
                for (PlanGraphLiteral l : initialLiterals){
                        if (!currentLits.contains(l)) return false;
                }
                return true;
        }
       
       
        public boolean checkGoalInInitial(){
               
                ArrayList<Literal> initialLiterals2 = new ArrayList<Literal>();
                initialLiterals2 = expressionToLiterals(problem.initial.toExpression());
               
                for (Literal lit: expressionToLiterals(problem.goal)){
                        if (!initialLiterals2.contains(lit)) return false;
                }
                return true;
               
        }
       
        public boolean isMutex(PlanGraphLiteral lit1, PlanGraphLiteral lit2){
 
                if (lit2.equals(lit1)) return true;
                //NegatedLiteral n = new NegatedLiteral(
//              PlanGraphLiteral litNegation1 = new PlanGraphLiteral((new NegatedLiteral(lit1.getLiteral())).negate());
                PlanGraphLiteral litNegation1 = new PlanGraphLiteral(lit1.getLiteral().negate());
 
        //      System.out.println("Literal " + lit1.toString() + " and its negation " + litNegation1.toString());
                //System.out.print("Literal " + lit1.toString() + " is mutex with " + lit2.toString() + ": ");
                if (lit2.equals(litNegation1)){
                        //System.out.println(lit2.toString() +  " is mutex with " + lit1.toString() + " because its negation is " + litNegation1.toString());
        //              //      System.out.print("true\n");
                        return true;
                }
                PlanGraphLiteral litNegation2 = new PlanGraphLiteral(lit2.getLiteral().negate());
                //System.out.print("Literal " + lit1.toString() + " is mutex with " + lit2.toString() + ": ");
                if (lit1.equals(litNegation2)){
        //              System.out.println(lit1.toString() +  " is mutex with " + lit2.toString() + " because its negation is " + litNegation2.toString());
                        //      System.out.print("true\n");
                        return true;
                }
               
        //      System.out.println(lit2.toString() +  " isn't mutex with " + lit1.toString() + " because its negation is " + litNegation1.toString());
        //      System.out.println(lit1.toString() +  " isn't mutex with " + lit2.toString() + " because its negation is " + litNegation2.toString());
 
                //      System.out.print("false\n");
                return false;
        }
 
        public ArrayList<PlanGraphLiteral> removeMutex(ArrayList<PlanGraphLiteral> lits){
                for (int i = 0; i < lits.size(); i++){
                        for (int j = 0; j < lits.size(); j++){
                                if (j != i){
                                        //System.out.print("Literal " + lits.get(i) + " and literal " + lits.get(j) + " + are mutex: ");
                                        if ( isMutex(lits.get(i), lits.get(j)) ){
                                //              System.out.println(true);
                                                lits.remove(j);
                                                return removeMutex(lits);
                                        }
                                //      System.out.println(false);
                                }
                        }
                }
                return lits;
        }
       
        public PlanGraphLiteral removeNegation(PlanGraphLiteral lit1, PlanGraphLiteral lit2){
                PlanGraphLiteral litNegation1 = new PlanGraphLiteral(lit1.getLiteral().negate());
                if (lit2.equals(litNegation1)){
                        return lit1;
                }
                return lit2;
        }
       
        /**
         * Checking if two steps are mutually exclusive
         * @param step1 first step to be checked
         * @param step2 second step to be checked
         * @return if mutually exclusive
         */
        public boolean isMutex(PlanGraphStep step1, PlanGraphStep step2){
                       
                //System.out.println("Step " + step1.toString() + " and step " + step2.toString() + " are mutex: ");
                /** Get an ArrayList of the important words from teh step. */
                ArrayList<String> s1 = getStepRelevantWords(step1);
                ArrayList<String> s2 = getStepRelevantWords(step2);
                boolean flag = true;
                while (flag){
                        for (String q : s1){    // for each important word in step 1
                                if (q.equals("Persistence") || q.equals("Step") || q.equals("at")) break;       // if it's a persistence step, we'll check by literals instead
                                for (String w : s2){
                                        if (w.equals("Persistence") || w.equals("Step") || w.equals("at")) break;
                                        //System.out.print("\nstep1's shit: [" + q + "] and step2's shit: [" + w + "]\n");
                                        if(q.equals(w)){
                                        //      System.out.print("Step " + step1.toString() + " and " + step2.toString() + " are mutex!!!!!!!\n");
                                                return true;
                                        }
                                }
                        }
                        flag = false;
                }
               
                //System.out.print("not by step method.");
 
        //      System.out.println("Checking " + step1.toString() + " versus " + step2.toString() + " by prec\n");
                /** Check each of the Parent nodes for mutual exclusion, when they require conflicting preconditions. */
                for (PlanGraphLiteral lit1: step1.getParentNodes()){
                        for (PlanGraphLiteral lit2: step2.getParentNodes()){
                                //System.out.println(" Literal " + lit1.toString() + " and literal " + lit2.toString());
                                if (isMutex(lit1, lit2) || isMutex(lit2, lit1)){
                                //      System.out.print(" lits mutex \n");
                                        return true;
                                }
                                //System.out.print(" lits not mutex \n");
                        }
                }
                //System.out.println("Checking " + step1.toString() + " versus " + step2.toString() + " by effect\n");
                /** Check each of the Child nodes for mutual exclusions, when they cause conflicting effects. */
                for (PlanGraphLiteral lit1: step1.getChildNodes()){
                        for (PlanGraphLiteral lit2: step2.getChildNodes()){
                        //      System.out.print(" Literal " + lit1.toString() + " and literal " + lit2.toString());
 
                                if (isMutex(lit1, lit2) || isMutex(lit2, lit1)){
                        //              System.out.print(" lits mutex \n");
                                        return true;
                                }
                        //      System.out.print(" lits not mutex \n");
 
                        }
                }
               
 
                //System.out.print(false + "\n");      
                //System.out.println(step1.toString() + " and " + step2.toString() + " are not mutex");
                return false;
 
        }
       
        /**
         * See if a step is applicable to the given literals
         * @param step to be checked
         * @param lits to be checked
         * @return if the step can be applied
         */
        public boolean isStepApplicable(PlanGraphStep step, ArrayList<PlanGraphLiteral> lits){
                //System.out.print("Testing for step " + step.toString() + " upon the current list of goal literals. ");
                for (PlanGraphLiteral literal1 : lits){ // for each literal in the specified level's list      
                        //for (PlanGraphLiteral literal2 : step.getChildNodes()){
                        //for (PlanGraphLiteral literal2 : step.getChildNodes()){
                        for (PlanGraphLiteral literal2 : pg.getAllPossiblePlanGraphEffects()){
                                if (isStepApplicablePrecon(step, literal2)){
                                //      System.out.println("Step name is " + step.toString() + " and this literal is " + literal2.toString());
                                        if (isMutex(literal1, literal2)){
                                //              System.out.println("Step is mutex with the literal");
                                                return false;
                                        }
                                       
                                }
                        }
                }
                //System.out.println("Step is compatible");
                return true;
        }
       
        /**
         * Method to return all the effects of a given step.
         * This is useful mainly because of the steps that have conflicting results, e.g. fly nola nola
         * @param step to get the effects from
         * @return the list of effects
         * eeeeeeeeee
         * changed to precons w/o changing name
         * um back again idk
         * why is it removing things
         * just return the stuff itself
         */
        public ArrayList<PlanGraphLiteral> getEffects(PlanGraphStep step){
                ArrayList<PlanGraphLiteral> lits = new ArrayList<PlanGraphLiteral>();
                //System.out.print("\nFor step " + step.toString() + " literals: ");
                //for (Literal l : expressionToLiterals(step.getStep().effect)){
                for (Literal l : expressionToLiterals(step.getStep().precondition)){
                        lits.add(new PlanGraphLiteral(l));
//                      System.out.print(l.toString() + ", ");
                }
        //      System.out.println("\n");
                return lits;
                /**
                ArrayList<PlanGraphLiteral> newLits = new ArrayList<PlanGraphLiteral>();
                while (!lits.isEmpty()) {
                        PlanGraphLiteral tempLit = lits.remove(0);
                        boolean shouldAdd = true;
                        for (int i = 0; i < newLits.size(); i++) {
                                if (newLits.get(i).equals(tempLit)){
                                        shouldAdd = false;
                                        break;
                                }
                                if(isMutex(newLits.get(i), tempLit)){
                                        tempLit = removeNegation(newLits.get(i), tempLit);
                                        newLits.set(i, tempLit);
                                        shouldAdd = false;
                                        break;
                                }
                        }
                        if (shouldAdd) {
                                newLits.add(tempLit);
                        }
                }
                */
//              return removeMutex(lits);
//              return newLits;        
        //      return lits;
               
               
        }
 
        /**
         * Helper function to get the preconditions of a step.
         * @param step input step to get precondition from
         * @return list of preconditions
         */
        public ArrayList<PlanGraphLiteral> getPrecon(PlanGraphStep step){
                ArrayList<PlanGraphLiteral> lits = new ArrayList<PlanGraphLiteral>();
                for (PlanGraphLiteral l : pg.getAllPossiblePlanGraphEffects()){
                        if( (l.getLiteral().equals(step.getStep().precondition)) ){
                                lits.add(l);
                        }
                }
                return lits;
        }      
 
        public ArrayList<PlanGraphLiteral> expressionToLiterals(ArrayList<Literal> lits){
                ArrayList<PlanGraphLiteral> result = new ArrayList<PlanGraphLiteral>();
                for (Literal l : lits){
                        result.add(new PlanGraphLiteral(l));
                }
                return result;
        }
       
        public ArrayList<PlanGraphLiteral> addNonMutexPrecon(ArrayList<PlanGraphLiteral> lits, PlanGraphStep s){
                ArrayList<PlanGraphLiteral> res = new ArrayList<PlanGraphLiteral>();
                res.addAll(lits);
                for (PlanGraphLiteral l : expressionToLiterals(expressionToLiterals(s.getStep().precondition))){
                        for (PlanGraphLiteral l2 : lits){
                                if (isMutex(l, l2) || l.equals(l2)) return res;
                        }
                        res.add(l);
                }
                return res;
        }
       
        public ArrayList<PlanGraphLiteral> addNonMutexPrecon(PlanGraphStep step){
                ArrayList<PlanGraphLiteral> result = expressionToLiterals(expressionToLiterals(step.getStep().effect));
        //      for (PlanGraphLiteral l1 : result){
                //      boolean flag = true;
                        for (PlanGraphLiteral l : expressionToLiterals(expressionToLiterals(step.getStep().precondition))){
                //              if (isMutex(l, l1));
                                boolean flag = true;
                                for (PlanGraphLiteral l1 : result){
                                        if (isMutex(l, l1)) flag = false;
                                }
                                if (flag) result.add(l);
                        }
                        return result;
        }
       
        /**
         * Further helper function
         * @param lits
         * @param step
         * @return
         */
        public ArrayList<PlanGraphStep> getApplicableSteps(ArrayList<PlanGraphLiteral> lits, PlanGraphStep step){
        //      ArrayList<PlanGraphLiteral> l = expressionToLiterals(expressionToLiterals(step.getStep().precondition));
                ArrayList<PlanGraphLiteral> l = expressionToLiterals(expressionToLiterals(step.getStep().effect));
 
                ArrayList<PlanGraphLiteral> l2 = expressionToLiterals(expressionToLiterals(step.getStep().effect));
 
                ArrayList<PlanGraphStep> result = new ArrayList<PlanGraphStep>();
//              outer:
                for (int i = 0; i < l2.size(); i++){
                        for (int j = i + 1; j < l2.size(); j++){
                                // if any of the step's effects are mutex with each other
                                if (isMutex(l2.get(i), l2.get(j))) return result;
                        }
                }
//              lits = addNonMutexPrecon(lits, step);
        //      System.out.print("\n first i have ");
                for (PlanGraphLiteral lo : lits){
                //      System.out.print(lo.toString() + ", ");
                }
//              System.out.println("");
                // experimental - add step's preconditions whne they aren't mutex with effects.
//              lits = addNonMutexPrecon(lits, step);
//              l.addAll(addNonMutexPrecon(step));
                l = addNonMutexPrecon(step);
        //      System.out.print("\n now i have ");
                for (PlanGraphLiteral lo : lits){
                //      System.out.print(lo.toString() + ", ");
                }
//              System.out.println("");
        //      for (PlanGraphLiteral pc : expressionToLiterals(expressionToLiterals(step.getStep().precondition))){
//                      for
                //}
                if (step.toString().contains("Persistence")) return result;
                for (PlanGraphLiteral lit : l){
                        boolean doesntExist = true;
                        for (PlanGraphLiteral ll : lits){
                                //System.out.print("\nWith step " + step.toString() + " Checking steps' effct " + lit.toString() + " against my lit " + ll.toString());
 
                                if (ll.equals(lit)) doesntExist = false;
                        //      System.out.println(doesntExist);
                        }
//                      return result;
                        if (doesntExist) return result;
                        //if (!lits.contains(lit)) return result;
                }
                result.add(step);
                return result;
        }
       
       
        /**
         * Helper function to get an arraylist of applciable steps from an arraylist of current Literals,
         * likely more useful/practical than the other applicable steps function.
         * It goes through all the possible steps and adds any who have all their preconditions in the literals list.
         * @param lits the PlanGraphLiterals to get steps from
         * @return the list of PlanGraphSteps
         * ///////
         *  ////// changed to effects
         */
        public ArrayList<PlanGraphStep> getApplicableSteps(ArrayList<PlanGraphLiteral> lits){
                ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
                HashSet<PlanGraphStep> stepsSet = new HashSet<PlanGraphStep>();
                //System.out.print("Literals: ");
                for (PlanGraphLiteral l : lits){
                //      System.out.print(l.toString() + ", ");
                }
                for (PlanGraphStep s : pg.getAllPossiblePlanGraphSteps()){
                        if (!s.toString().contains("Persistence")) {
                        //      System.out.print("\nChecking step " + s.toString());
                                ArrayList<PlanGraphStep> temp = getApplicableSteps(lits, s);
                                if (temp.size() != 0){
                        //              System.out.println(" is applicable.");
                                        stepsSet.add(s);
                                }
                                else if (temp.size() == 0){
                                //      System.out.println(" is not applicable.");
                                }
                        //boolean willAdd = false;
//                      for (Literal z : expressionToLiterals(s.getStep().precondition)){
                        //for (Literal z : expressionToLiterals(s.getStep().effect)){
/**                     ArrayList<Literal> arr = expressionToLiterals(s.getStep().effect);
                        ArrayList<PlanGraphLiteral> arrp = new ArrayList<PlanGraphLiteral>();
                        for (Literal l : arr){
                                arrp.add(new PlanGraphLiteral(l));
                        }
                        for (PlanGraphLiteral l : arrp){
                                System.out.print(" with literal " + l.toString());
                                if (!lits.contains(l)) goto inner;
                                for (PlanGraphLiteral l2 : lits){
                                        if (isMutex(l, l2)) goto inner;
                                }
                                System.out.print(" contains.\n");
                        }
                        */
//                      steps.add(s);
                       
//                      inner:
        //                      if (false) System.out.println("a");
/**                             inner:
                                for (PlanGraphLiteral l : lits){
                                        if (l.equals((new PlanGraphLiteral(z)))){
                                                System.out.println("Effect " + z.toString() + " of step " + s.toString() + " was found in the current literals at " + l.toString());
                                                willAdd = true;
//                                              stepsSet.add(s);
                                                break inner;
                                               
                                        }
                                        else {
                                                willAdd = false;
                                                System.out.println("Effect " + z.toString() + " of step " + s.toString() +  " was not found in the current literals at " + l.toString());
                                                //break inner;
                                        }
                                }
                                if (!willAdd) break;
                               
                        }
                        if (willAdd){
                                stepsSet.add(s);
                                System.out.println("Step " + s.toString() + " was added.");
                        }
                        */
                        }
                }
                steps.addAll(stepsSet);
                steps = removeP(steps);
                //System.out.print("\n\nFor literals ");
                for (PlanGraphLiteral l : lits){
                        //System.out.print(l.toString() + ", ");
                }
//              System.out.print(" there exist previous steps of ");
                for (PlanGraphStep s : steps){
        //              System.out.print(s.toString() + ", ");
                }
                //System.out.println("\n\n");
//              if (true) System.exit(0);
                return steps;
        }
       
        /**
         * Helper function to get applicable steps from a single literal.
         * It uses hash sets to ensure duplicates won't be present.
         * @param lits the literal to get the steps from.
         * @return an arraylist of steps that have that literal as their precondition.
         * eeeeeeee
         * changed to effect
         */
        public ArrayList<PlanGraphStep> getApplicableSteps(PlanGraphLiteral lits){
                ArrayList<PlanGraphStep> steps = new ArrayList<PlanGraphStep>();
                 
                for (PlanGraphStep s : pg.getAllPossiblePlanGraphSteps()){
                //      System.out.print("\nStep " + s.toString());
//                      for (Literal a : expressionToLiterals(s.getStep().precondition)){
                        for (Literal a : expressionToLiterals(s.getStep().effect)){
                                if (lits.equals(new PlanGraphLiteral(a))){
                        //              System.out.print(" true\n");
                                        steps.add(s);
                                }
                        }
                }
                HashSet<PlanGraphStep> hs = new HashSet<PlanGraphStep>();
                hs.addAll(steps);
                steps.clear();
                steps.addAll(hs);
                return steps;
        }
 
        /**
         * Helper function to remove persistence steps from lists of steps.
         * This is used since parent literals are copied to the children when they don't conflict (i.e. any non-affected literals are persistence).
         * @param arr list of steps that may contain persistence steps
         * @return list of steps without the persistence steps
         */
        public ArrayList<PlanGraphStep> removeP(ArrayList<PlanGraphStep> arr){
                ArrayList<PlanGraphStep> result = new ArrayList<PlanGraphStep>();
                for (PlanGraphStep s : arr){
                        //System.out.print("\nStep " + s.toString());
                        if (!(s.toString().contains("Persistence"))){
                                //System.out.println(" added");
                                result.add(s);
                        }
//                      else System.out.println(" not added");
                }
                return result;
        }
 
        /**
         * Function to remove mutex steps from the steps provided.
         * If a mutex is found, it removes it and returns recursively so that deleted one won't falsely flag others, like it would without
         * the modification to the original list.
         * @param steps the ArrayList of steps to check for mutexes
         * @return      the ArrayList of steps that have had their mutexes removed
         */
        public ArrayList<PlanGraphStep> removeMutexes(ArrayList<PlanGraphStep> steps){
                HashSet<PlanGraphStep> hs = new HashSet<PlanGraphStep>();
                for (PlanGraphStep s : steps){
                        hs.add(s);
                }
                steps.clear();
                steps.addAll(hs);
                //System.out.println("removing mutexes from steps.....\n\n\n\n\n\n");
                for (int i = 0; i < steps.size(); i++){
                        for (int j = i + 1; j < steps.size(); j++){
                                if (j != i){
                                        if ( isMutex(steps.get(i), steps.get(j)) ){
                                                steps.remove(j);
                                                return removeMutexes(steps);
                                        }
                                }
                        }
                }
                return steps;
        }
       
 
       
       
        /**
         * Create a GraphPlanNode which models the steps and literals at a certain level of the PlanGraph.
         */
       
        public void defineNode(ArrayList<PlanGraphStep> steps, ArrayList<PlanGraphLiteral> literals, int level){
                GraphPlanNode node = new GraphPlanNode();
                for (PlanGraphStep step: steps ){
                        node.addSteps(step);
                }
               
                for (PlanGraphLiteral literal: literals ){
                        node.addLiterals(literal);
                }
               
                node.setLevel(level);
                nodes.put(currentLevel,node);
 
        }
       
        /**
         * Helper function to get all the literals from an Expression
         *
         * @param expression The Expression to convert to list
         * @return ArrayList<Literal> List of literals in expression
         */
        public ArrayList<Literal> expressionToLiterals(Expression expression)
        {
                ArrayList<Literal> literals = new ArrayList<Literal>();
                if (expression instanceof Literal)
                        literals.add((Literal)expression);
                else
                {
                        Conjunction cnf = (Conjunction)expression.toCNF();
                        for (Expression disjunction : cnf.arguments)
                                if (((Disjunction) disjunction).arguments.length == 1)
                                        literals.add((Literal)((Disjunction) disjunction).arguments.get(0));
                                // else -- Do Nothing!
                }
                return literals;
        }
 
       
       
        @Override
        public int countVisited() {
                return this.visitedNodes;
        }
 
        @Override
        public int countExpanded() {
                return this.extendedNodes;
        }
 
        @Override
        public void setNodeLimit(int limit) {
                this.limit = limit;
        }
       
        @Override
        //Function used to actually solve problem.
        public Plan findNextSolution() {
                return search();
        }
       
       
        /**
         * Extract the negations from a list of literals. This is used in the goal node adding method,
         * to get all the negations only to add to the current literals, but only if they're non mutex.
         * @param lits  the list of literals that we want negations only from
         * @return lits minus non-negations
         */
        public ArrayList<PlanGraphLiteral> getNegations(ArrayList<PlanGraphLiteral> lits){
                //System.out.print("literals before deleting non negations: ");
                //for (PlanGraphLiteral t : lits){
                        //System.out.print(t.toString() + ", ");
                //}
                //System.out.println("\n");
                ArrayList<PlanGraphLiteral> temp = new ArrayList<PlanGraphLiteral>();
                /** For each literal in lits, iterate through and find the ones with "not" inside them. We'll then add those to our result array.*/
                for (PlanGraphLiteral p : pg.getAllPossiblePlanGraphEffects()){
                                //System.out.println(p.toString());
                                if (p.toString().contains("not")){
                                        temp.add(p);
                                }
                }
                /** Delete possible duplicates using a set. */
                lits.addAll(temp);
                HashSet<PlanGraphLiteral> hs = new HashSet<PlanGraphLiteral>();
                hs.addAll(lits);
                lits.clear();
                lits.addAll(hs);
                //System.out.print("\nLiterals after deleting non- negations: ");
                //for (PlanGraphLiteral l : temp){
                        //System.out.print(l.toString() + ", ");
                //}
                return temp;
        }
       
        public Plan search3(){
/**             for (PlanGraphLiteral l : pg.getAllPossiblePlanGraphEffects()){
                        System.out.print("\n\nFor literal " + l.toString() + " there are previous steps " );
                        ArrayList<PlanGraphLiteral> ls = new ArrayList<PlanGraphLiteral>();
                        ls.add(l);
                        for (PlanGraphStep s : getApplicableSteps(ls)){
                                System.out.print(s.toString() + ", ");
                        }
                        System.out.println("\n\n");
                }
        */     
/**             for (PlanGraphStep s : pg.getAllPossiblePlanGraphSteps()){
                        System.out.print("For step " + s.toString() + " there are these possible effects ");
                        ArrayList<PlanGraphLiteral> ls = new ArrayList<PlanGraphLiteral>();
                        for (Literal l : expressionToLiterals(s.getStep().effect)){
                                ls.add(new PlanGraphLiteral(l));
                        }
                        for (PlanGraphLiteral l : ls){
                                System.out.print(l.toString() + ", ");
                        }
                        System.out.println("\n\n");
                }
                */
                return goDown();
 
        }
        /**
         * Method that goes down.
         * @return TotalOrderSolution once the solution has been found, or null if it hasn't.
         * TODO actually return null
         */
        public Plan goDown(){
                int i = 1;
                while (!finished && !failure && i < 20){
                        System.out.print(i);
                        goDown(i);
                        i++;
                        pg.extend();
                }
                if (finished && success){
                        return solution1;
                }
//              if (finished && failure){
                        return null;
        //      }
        }
       
        /**
         * Method to go down teh PlanGraph.
         * @param level the int level to start at, which determines how many sub levels it can have.
         */
        public void goDown(int level){
        //      System.setOut(new PrintStream(new NullOutputStream()));
 
                // grow it down
                ArrayList<LevelNode> lvls = new ArrayList<LevelNode>();
                //ArrayList<PlanGraphLiteral> firstLits = new ArrayList<PlanGraphLiteral>();
                firstLits.addAll(goalLiterals);
                firstLits = addParentsLits(firstLits);
//                      for (PlanGraphStep s : getApplicableSteps(goalLiterals)){
                        for (PlanGraphStep s : getApplicableSteps(firstLits)){
                                System.out.print("\nInitial Goal Literals ");
                                for (PlanGraphLiteral l : goalLiterals){
                                        System.out.print(l.toString() + ", ");
                                }
                                System.out.print("\nInitials after adding irrelevant lits ");
                                for (PlanGraphLiteral l : firstLits){
                                        System.out.print(l.toString() + ", ");
                                }
                                System.out.println("");
                               
                                System.out.println(" applicable step " + s.toString());
                                ArrayList<PlanGraphLiteral> ll = getEffects(s);
                                //for (PlanGraphLiteral le : ll){
        //                              System.out.print(le.toString() + " ");
                        //      }
                                lvls.add(new LevelNode(s, level));                     
                //              System.out.println("Level added to goals");
                        }
        //      }
                for (LevelNode l : lvls){
                //      System.out.println("Growing...");
                        grow(l);
                }
        //      System.out.println("Grown");
                for ( int ii = 0; ii < lvls.size(); ii++){
                        //System.out.print("Traversing goal node #" + ii);
        //              System.out.println(" out of " + lvls.size());
                        LevelNode l = lvls.get(ii);
                        dfs(l);
                        if (success){
                                finished = true;
                        //      System.out.println("solution found at level " +
                                return;
                        }
                }
                /**for (LevelNode l : lvls){
                        System.out.println("Traversing goal level " + l.toString());
                //      traverse(l);
                        dfs(l);
                }
                */
        }
       
        /**
         * Method to create a solution based on the goal node found in dfs (depth first search).
         * @param m LevelNode that the goal was found on.
         * @return      TotalOrderPlan representation of the solution.
         */
        public TotalOrderPlan createTotalOrderPlanNode(LevelNode m){
                System.out.println("\naaaaaaaaaaaaaaaaaaaaaaaaaaa goal found ");
                TotalOrderPlan solution2 = new TotalOrderPlan();
                System.out.println("m level " + m.level);
                //for (int i = 0; i <= currentMaxLevel; i++){
                ArrayList<Step> solutionSteps = new ArrayList<Step>();
                LevelNode n = m;
                //for (int i = m.level; i >= 0; i++){
                        while (!n.isGoal){
                                System.out.println("\n" + n.toString() + " and my step is " + n.step.toString());
                                //solution2 = solution2.addStep(n.step.getStep());
                        //      solutionSteps.add(0, n.step.getStep());
                                solutionSteps.add(n.step.getStep());
                                //n = n.parent;
                                LevelNode t = n.parent;
                                n = t;
                        }
//                      solution2 = solution2.addStep(n.step.getStep());
                        solutionSteps.add(n.step.getStep());
                        //System.out.println(n.toString() + " and my step is " + n.step.toString());
 
                        //for (int i = solutionSteps.size() - 1; i >= 0; i--){
                        for (int i = 0; i < solutionSteps.size(); i++){
                                solution2 = solution2.addStep(solutionSteps.get(i));
                        }
                       
                        //      }
                        System.out.println("\n\n");
                Iterator<Step> x = solution2.iterator();
                while(x.hasNext()){
                        System.out.println(x.next());
                }
        //      if (true) throw new ArithmeticException("la la");
                return solution2;
               
        }
       
        public void grow(LevelNode l){
                if (l.end) return;
                l.makeChildren();
                for (LevelNode c : l.children){
                        //System.out.println("Growing children...");
                        grow(c);
                }
        }
       
        /**
         * Depth-first search function starting from the specified node.
         * The function creates a stack and pushes to it as it traverses the plan graph.
         * It checks for the goal state as it searches through teh nodes.
         * @param l the Level node to start from.
         */
        public void dfs(LevelNode l) {
                Stack<LevelNode> s = new Stack<LevelNode>();
                s.push(l);
                while (!s.isEmpty()){
                        LevelNode n = s.pop();
                     System.out.println("Node n " + n.toString());
                        for (LevelNode c : n.children){
                        s.push(c);
                    }
                    if (s.isEmpty()) return;
                    LevelNode m = s.peek();
                    //TODO
                    if (m.isLeveledOff){
                                }
                //    System.out.println("At level " + m.level + " is " + m.toString());
                    if (m.containsGoal()){
                   //   System.out.println("Solution found at level " + m.level + " " + m.toString());
                        success = true;
                        solution1 = createTotalOrderPlanNode(m);
                        return;
                    }
                }
        }
 
        private ArrayList<PlanGraphLiteral> addParentsLits(ArrayList<PlanGraphLiteral> myLits){
                ArrayList<PlanGraphLiteral> temp = new ArrayList<PlanGraphLiteral>();
                for (PlanGraphLiteral p : getNegations(pg.getAllPossiblePlanGraphEffects())){
                        boolean flag = true;
                        for (PlanGraphLiteral l : myLits){
                                        //System.out.printf("Parent lit %s is mutex with my lit %s %b\n", p.toString(), l.toString(), isMutex(l, p));
                                if (isMutex(l, p) || p.equals(l)) flag = false;
                        }
                        if (flag) temp.add(p);
                        //if (flag) System.out.printf("\n\nAdding %s to my array, I'm level %d with step %s.\n", p.toString(), level, this.toString());
                        //if (!flag) System.out.printf("\n\nNOT adding %s to my array! I'm level %d with step %s.\n", p.toString(), level, this.toString());
                }
                //System.out.print("Current literals: ");
                //for (PlanGraphLiteral l : lits){
                        //System.out.print(l.toString() + ", ");
                //}
                /** Add all to a set to remove possible duplicates */
                myLits.addAll(temp);
                HashSet<PlanGraphLiteral> hs = new HashSet<PlanGraphLiteral>();
                hs.addAll(myLits);
                myLits.clear();
                myLits.addAll(hs);
        /**     System.out.printf("\nLiterals now at level %d and step %s and I'm a goal node%b: ", level, step.toString(), isGoal);
                for (PlanGraphLiteral l : lits){
                        System.out.print(l.toString() + ", ");
                }
                System.out.println("");
                */
                return myLits;
        }      
       
       
        private class LevelNode{
                private boolean isGoal;
                private boolean hasChildren;
                private boolean visited;
                private boolean end;
                private ArrayList<LevelNode> children;
                private LevelNode parent;
                private ArrayList<PlanGraphLiteral> lits;
                private PlanGraphStep step;
                private int level;
                private boolean isLeveledOff;
               
                /**
                 * Goal node constructor.
                 * Goal nodes have no parents,
                 * @param step
                 * @param level
                 */
                public LevelNode(PlanGraphStep step, int level){
                        isGoal = true;
                        visited = false;
                        hasChildren = false;
                        this.level = level;
                        this.step = step;
                        lits = new ArrayList<PlanGraphLiteral>();
                        lits.addAll(getEffects(step));
                        System.out.println("\nCreating goal literal...");
                //      System.out.println("\nhi, here's me and my lits " + this.toString());
                        //addParentsLits(firstLits);
                        addParentsLits(goalLiterals);
                //      System.out.println("\nhi, here's me and my lits after adding goal literals that aren't mutex " + this.toString());
                //      addParentsLits(getNegations(pg.getAllPossiblePlanGraphEffects()));
                        addAllNegations();
                //      System.out.println("\nhi, here's me and my lits after adding negations of all " + this.toString());
                        children = new ArrayList<LevelNode>();
                        end = false;
                        isLeveledOff = false;
                }
               
                /**
                 * Second constructor for LevelNode, this one is invoked from a parent Node.
                 * @param step  the PlanGraphStep that will be here
                 * @param level the current level in the PlanGraph
                 * @param parent        the Node who is the parent of this
                 */
                public LevelNode(PlanGraphStep step, int level, LevelNode parent){
                        this.parent = parent;
                        isGoal = false;
                        visited = false;
                        hasChildren = false;
                        this.level = level;
                        this.step = step;                      
                        lits = new ArrayList<PlanGraphLiteral>();
                        //lits.addAll(step.getChildNodes());
                        lits.addAll(getEffects(step));
                //      System.out.println("\nhi, here's me and my lits " + this.toString());
 
                        ArrayList<PlanGraphLiteral> parentLits = new ArrayList<PlanGraphLiteral>();
                        parentLits.addAll(parent.lits);
                //      System.out.println("\nand here are my parent's lits " + parentLits.toString());
 
                        addParentsLits();
                //      System.out.println("\nhi, here's me and my lits 3rd round " + this.toString());
 
                        children = new ArrayList<LevelNode>();
                        end = false;
                        isLeveledOff = false;
                }
               
                private void addAllNegations(){
                        for (PlanGraphLiteral l : getNegations(pg.getAllPossiblePlanGraphEffects())){
                                boolean flag = true;
                                for (PlanGraphLiteral l2 : lits){
                                if (isMutex(l, l2)) flag = false;
                                }
                                if (flag) lits.add(l);
                        }
                }
               
                /**
                 * addParentsLits, an attempt on resolving an issue in finding mutexes;
                 * if the parent's literals haven't changed (haven't become mutex), they're still true, so retain them.
                 * This one is for the goal nodes (who don't have parents), the below for any children.
                 * The below one relies on the goal node having the same done with the overall literals.
                 * @param parentLits the literals of the parent
                 */
                private void addParentsLits(ArrayList<PlanGraphLiteral> parentLits){
                        ArrayList<PlanGraphLiteral> temp = new ArrayList<PlanGraphLiteral>();
                        for (PlanGraphLiteral p : parentLits){
                                boolean flag = true;
                                //System.out.printf("Parent lit %s is applicable with my lits ", p.toString());
                       
                                for (PlanGraphLiteral l : lits){
//                                      System.out.printf("Parent lit %s is applicable with my lit %s ", p.toString(), l.toString());
                                        if (isMutex(l, p) || p.equals(l)){
                                                flag = false;
                                        //      System.out.print(" mutex lit @ " + l.toString() + " ");
                                        }
                                }
                                for (PlanGraphLiteral l : getEffects(step)){
                                        if (isMutex(l, p) || p.equals(l)){
                                                flag = false;
                                        //      System.out.print(" mutex step precon @ " + l.toString() + " ");
                                        }
                                }
                                for (PlanGraphLiteral l : expressionToLiterals(expressionToLiterals(step.getStep().effect))){
                                        if (isMutex(l, p) || (p.equals(l) )){
                                                flag = false;
                                                //System.out.print(" mutex step fx @ " + l.toString() + " ");
                                        }
                                }
                                System.out.println(flag);
                                if (flag) temp.add(p);
                                //if (flag) System.out.printf("\n\nAdding %s to my array, I'm level %d with step %s.\n", p.toString(), level, this.toString());
                                //if (!flag) System.out.printf("\n\nNOT adding %s to my array! I'm level %d with step %s.\n", p.toString(), level, this.toString());
                        }
                        //System.out.print("Current literals: ");
                        //for (PlanGraphLiteral l : lits){
                                //System.out.print(l.toString() + ", ");
                        //}
                        /** Add all to a set to remove possible duplicates */
                        lits.addAll(temp);
                        HashSet<PlanGraphLiteral> hs = new HashSet<PlanGraphLiteral>();
                        hs.addAll(lits);
                        lits.clear();
                        lits.addAll(hs);
                /**     System.out.printf("\nLiterals now at level %d and step %s and I'm a goal node%b: ", level, step.toString(), isGoal);
                        for (PlanGraphLiteral l : lits){
                                System.out.print(l.toString() + ", ");
                        }
                        System.out.println("");
                        */
                }
               
                /**
                 * Function for adding parent literals,
                 * used in the case of the non-goal nodes which do indeed have parents.
                 */
                private void addParentsLits(){
                        if (isGoal) return;
/**
                        ArrayList<PlanGraphLiteral> temp = new ArrayList<PlanGraphLiteral>();
                        for (PlanGraphLiteral p : parent.lits){
                                boolean flag = true;
                                for (PlanGraphLiteral l : this.lits){
                                        if (isMutex(l, p)) flag = false;
                                }
                                if (flag) temp.add(p);
                        }
        //              System.out.print("\nCurrent literals: ");
                        for (PlanGraphLiteral l : this.lits){
                        //      System.out.print(l.toString() + ", ");
                        }
                       
                        this.lits.addAll(temp);
                        HashSet<PlanGraphLiteral> hs = new HashSet<PlanGraphLiteral>();
                        hs.addAll(this.lits);
                        this.lits.clear();
                        this.lits.addAll(hs);
                        //System.out.print("\nLiterals now: ");
                        for (PlanGraphLiteral l : this.lits){
                                //System.out.print(l.toString() + ", ");
                        }
                        //System.out.println("");
                         *
                         */
                        ArrayList<PlanGraphLiteral> temp = new ArrayList<PlanGraphLiteral>();
 
                        for (PlanGraphLiteral p : parent.lits){
                                boolean flag = true;
                                //System.out.printf("PPPParent lit %s is applicable with my lits ", p.toString());
                       
                                for (PlanGraphLiteral l : lits){
//                                      System.out.printf("Parent lit %s is applicable with my lit %s ", p.toString(), l.toString());
                                        if (isMutex(l, p) || p.equals(l)){
                                                flag = false;
                                        //      System.out.print(" mutex lit @ " + l.toString() + " ");
                                        }
                                }
                                for (PlanGraphLiteral l : getEffects(step)){
                                        if (isMutex(l, p) || p.equals(l)){
                                                flag = false;
                                                //System.out.print(" mutex step precon @ " + l.toString() + " ");
                                        }
                                }
                                for (PlanGraphLiteral l : expressionToLiterals(expressionToLiterals(step.getStep().effect))){
                                        if (isMutex(l, p) || (p.equals(l) )){
                                                flag = false;
                                        //      System.out.print(" mutex step fx @ " + l.toString() + " ");
                                        }
                                }
                                //System.out.println(flag);
                                if (flag) temp.add(p);
                                //if (flag) System.out.printf("\n\nAdding %s to my array, I'm level %d with step %s.\n", p.toString(), level, this.toString());
                                //if (!flag) System.out.printf("\n\nNOT adding %s to my array! I'm level %d with step %s.\n", p.toString(), level, this.toString());
                        }
                        //System.out.print("Current literals: ");
                        //for (PlanGraphLiteral l : lits){
                                //System.out.print(l.toString() + ", ");
                        //}
                        /** Add all to a set to remove possible duplicates */
                        lits.addAll(temp);
                        HashSet<PlanGraphLiteral> hs = new HashSet<PlanGraphLiteral>();
                        hs.addAll(lits);
                        lits.clear();
                        lits.addAll(hs);
                /**     System.out.printf("\nLiterals now at level %d and step %s and I'm a goal node%b: ", level, step.toString(), isGoal);
                        for (PlanGraphLiteral l : lits){
                                System.out.print(l.toString() + ", ");
                        }
                        System.out.println("");
                        */
                }
               
                /**
                 * Inner method to generate children.
                 * For each Step in the list of Steps possible from the current literals, add new children to the list of children,
                 * and each of the Nodes will have "this" as its parent.
                 * If we're at level 0 already, just return.
                 */
                private void makeChildren(){
                        if (level == 0){
                                if (!containsGoal()) visited = true;
                                hasChildren = false;
                                end = true;
                                //System.out.println("End");
                                return;
                        }
        //              System.out.print("I am at level " + level + " and my literals are : ");
                        for (PlanGraphLiteral l : lits){
                //              System.out.print(l.toString() + ", ");
                        }
                //     
                        System.out.print("\nAnd my applicable steps are: ");
                       
                        for (PlanGraphStep s : getApplicableSteps(lits)){
                //      System.out.print(s.toString() + ", ");
                                if (!s.equals(step)) children.add(new LevelNode(s, level - 1, this));
                        }
        //              System.out.println();
                        if (children.size() > 0) hasChildren = true;
                        /** If there's no children possible but we're not at level 0 yet, this traversal has leveled off. */
                        if (children.size() == 0 && level != 0){
                                isLeveledOff = true;
                        }
                }
               
                /**
                 * Whether or not this Node contains the goal.
                 * Get the initial literals and compare.
                 */
                public boolean containsGoal(){
                        ArrayList<Literal> inits = expressionToLiterals(problem.initial.toExpression());
                        ArrayList<PlanGraphLiteral> sol = new ArrayList<PlanGraphLiteral>();
                        for (Literal l : inits){
                                sol.add(new PlanGraphLiteral(l));
                        }
                        //System.out.println("\n**********goal****");
                //      System.out.print("my lits at level " + level);
                //      for (PlanGraphLiteral m : lits){
                //              System.out.print(m.toString() + ", ");
                //      }
                       
                //      System.out.println("\nme: " + this.toString());
                        /** Iterate through the list of literals in the initial state */
                        for (PlanGraphLiteral initlits : sol){
 
                        //      System.out.print("initial lits " + initlits.toString());
                               
                                boolean found = false;
                                /** Iterate through the current list of literals to look for the current literal from the initial state.
                                 * If it isn't found, we know we haven't found a goal yet, so return false.
                                 */
                                for (PlanGraphLiteral myLits : lits){
                                        if (initlits.equals(myLits)){
                                        //      System.out.print(" matches me at " + myLits);
                                                found = true;
                                        //      break;
                                        }
                                }
                                if (found){
                                        //System.out.print(" DOES exist here!\n");
                                        //System.out.println("Success!");
                                }
                                else if (!found){
                                        //System.out.print(" does not exist here.\n");
                                        return false;
                                }
                        }
                        //System.out.println(" FOUND");
                        //System.out.println(this.toString());
                        //if (true) throw new ArithmeticException("hue");
                        return true;
                }
               
                /**
                 * toString method that returns a String representation of this Node.
                 * Includes the Node's Step, its level, whether or not it's an end or a goal, and a concatenation of its literals.
                 * Mostly for use in testing.
                 * Uncomment the bracket and first comment to have nothing print out, or the bracket and the second comment to have Node info printed out
                 * but the literals only for end (level 0) nodes.
                 */
                public String toString(){
                        String result = "";
                        //if (end){
                        result = "\nNode of Step " + step.toString() + " at level " + level + " and is the end " + end + " and is a goal " + isGoal;
                        //if (end){
                                result = result + " literals are : ";
                                for (PlanGraphLiteral l : lits){
                                        result = result + l.toString() + " ";
                                }
                //      }
                       
                        return result;
                }
               
        /**     public boolean isLeveledOff(LevelNode n){
                        //if (isGoal) return false;
                        if (n.children.size() == this.children.size()){
                                return true;
                        }
                        return false;
                }
               
                public boolean isLeveledOff(){
                        return false;
                }
                */
               
        }
       
       
       
}
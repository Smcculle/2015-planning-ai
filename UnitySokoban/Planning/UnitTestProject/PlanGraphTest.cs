using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Planning.IO;
using Planning;
using PlanGraphProject;
using System.Collections.Generic;
using Planning.Logic;
using System.IO;

namespace UnitTestProject
{
    [TestClass]
    public class PlanGraphTest
    {
        private Problem CakeProblem()
        {
            string domainFile = "cake_domain.pddl";
            string problemFile = "cake_problem.pddl";
            string domainString, problemString;
            using (StreamReader reader = new StreamReader(domainFile))
                domainString = reader.ReadToEnd();
            using (StreamReader reader = new StreamReader(problemFile))
                problemString = reader.ReadToEnd();
            return PDDLReader.GetProblem(domainString, problemString);
        }

        private Problem RocketProblem()
        {
            string domainFile = "rocket_domain.pddl";
            string problemFile = "rocket_problem.pddl";
            string domainString, problemString;
            using (StreamReader reader = new StreamReader(domainFile))
                domainString = reader.ReadToEnd();
            using (StreamReader reader = new StreamReader(problemFile))
                problemString = reader.ReadToEnd();
            return PDDLReader.GetProblem(domainString, problemString);
        }

        private PlanGraph CreateCakeProblemPlanGraph()
        {
            return new PlanGraph(CakeProblem(), true);
        }

        private PlanGraph CreateRocketProblemPlanGraph()
        {
            return new PlanGraph(RocketProblem(), true);
        }

        private PlanGraphStep getStep(String stepName, List<PlanGraphStep> steps)
        {
            foreach (PlanGraphStep step in steps)
                if (stepName.Equals(step.getStep().name))
                    return step;
            return null;
        }

        [TestMethod]
        public void ConstructorCakeDomain()
        {
            // TEST CAKE DOMAIN
            PlanGraph cakeGraph = CreateCakeProblemPlanGraph();
            PlanGraphLevelMutex rootLevel = (PlanGraphLevelMutex)cakeGraph.getRootLevel();

            // Test existence
            Assert.IsNotNull(rootLevel);
            // Test parent and level for new Plan Graph
            Assert.IsNull(rootLevel.getParent());
            Assert.AreEqual(0, rootLevel.getLevel());
            // Test nothing should be mutually exclusive in initial state
            Assert.AreEqual(0, rootLevel.getMutuallyExclusiveSteps().Count);
            Assert.AreEqual(0, rootLevel.getMutuallyExclusiveLiterals().Count);
            // Test steps
            Assert.AreEqual(0, rootLevel.countCurrentSteps());
            // Test literals
            Assert.AreEqual(2, rootLevel.countCurrentEffects());
        }

        [TestMethod]
        public void firstStepCakeDomain()
        {
            PlanGraph cakeGraph = CreateCakeProblemPlanGraph();
            PlanGraphLevelMutex rootLevel = (PlanGraphLevelMutex)cakeGraph.getRootLevel();
            PlanGraphLevelMutex firstLevel = (PlanGraphLevelMutex)cakeGraph.getLevel(1);

            // Test existence
            Assert.IsNotNull(firstLevel);
            // Test parent and level for new Plan Graph
            Assert.IsNotNull(firstLevel.getParent());
            Assert.AreEqual(firstLevel.getParent(), rootLevel);
            Assert.AreEqual(1, firstLevel.getLevel());
            // Test steps
            Assert.AreEqual(3, firstLevel.countCurrentSteps());
            // ---------
            Assert.AreEqual(3, firstLevel.getMutuallyExclusiveSteps().Count);
            Assert.AreEqual(4, firstLevel.getMutuallyExclusiveLiterals().Count);
            // Test literals
            Assert.AreEqual(4, firstLevel.countCurrentEffects());
        }

        [TestMethod]
        public void constructorRocketDomain()
        {
            // TEST ROCKET DOMAIN
            PlanGraph rocketGraph = CreateRocketProblemPlanGraph();
            PlanGraphLevelMutex rootLevel = (PlanGraphLevelMutex)rocketGraph.getRootLevel();

            // Test existence
            Assert.IsNotNull(rootLevel);
            // Test parent and level for new Plan Graph
            Assert.IsNull(rootLevel.getParent());
            Assert.AreEqual(0, rootLevel.getLevel());
            // Test nothing should be mutually exclusive in initial state
            Assert.AreEqual(0, rootLevel.getMutuallyExclusiveSteps().Count);
            Assert.AreEqual(0, rootLevel.getMutuallyExclusiveLiterals().Count);
            // Test steps
            Assert.AreEqual(0, rootLevel.countCurrentSteps());
            // Test literals
            Assert.AreEqual(5, rootLevel.countCurrentEffects());
        }

        [TestMethod]
        public void firstStepRocketDomain()
        {
            PlanGraph rocketGraph = CreateRocketProblemPlanGraph();
            PlanGraphLevelMutex rootLevel = (PlanGraphLevelMutex)rocketGraph.getRootLevel();
            PlanGraphLevelMutex firstLevel = (PlanGraphLevelMutex)rocketGraph.getLevel(1);

            // Test existence
            Assert.IsNotNull(firstLevel);
            // Test parent and level for new Plan Graph
            Assert.IsNotNull(firstLevel.getParent());
            Assert.AreEqual(firstLevel.getParent(), rootLevel);
            Assert.AreEqual(1, firstLevel.getLevel());
            // Test steps
            Assert.AreEqual(8, firstLevel.countCurrentSteps());
            // Test mutex Literals does not contain any entries for (not (at cargo london))
            Constant cargo = new Constant("cargo", "Cargo");
            Constant rocket = new Constant("rocket", "Rocket");
            Constant nola = new Constant("location", "NOLA");
            Constant london = new Constant("location", "London");
            Predication cargoAtLondon = new Predication("at", cargo, london);
            NegatedLiteral notCargoAtLondon = cargoAtLondon.Negate() as NegatedLiteral;
            PlanGraphLiteral cargoIsNotAtLondon0 = new PlanGraphLiteral(notCargoAtLondon, 0);
            PlanGraphLiteral cargoIsNotAtLondon1 = new PlanGraphLiteral(notCargoAtLondon, 1);
            List<PlanGraphLiteral> cinalMutexLiterals = null;
            foreach (PlanGraphLiteral key in firstLevel.getMutuallyExclusiveLiterals().Keys)
            {
                if (key.equals(cargoIsNotAtLondon0) || key.equals(cargoIsNotAtLondon1))
                {
                    cinalMutexLiterals = firstLevel.getMutuallyExclusiveLiterals()[key];
                }
            }
            Assert.IsNull(cinalMutexLiterals);
            // Test mutually exclusive Steps and Literals  during this step
            Assert.AreEqual(7, firstLevel.getMutuallyExclusiveSteps().Count);
            Assert.AreEqual(8, firstLevel.getMutuallyExclusiveLiterals().Count);
            // Test literals
            Assert.AreEqual(9, firstLevel.countCurrentEffects());
            // Test mutuallyExclusiveStep specifics (fly Rocket NOLA NOLA)
            //		(fly Rocket NOLA NOLA)[1]=[(Persistence Step (at Rocket NOLA))[1], 	<----
            //		          				 (load Cargo Rocket NOLA)[1], 				<----
            //		          				 (fly Rocket NOLA London)[1] 				<----
            //		          				]
            Predication rocketAtNola = new Predication("at", rocket, nola);
            NegatedLiteral notRocketAtNola = rocketAtNola.Negate() as NegatedLiteral;
            Conjunction flyNola2Precondition = new Conjunction(rocketAtNola);
            Conjunction flyNola2Effect = new Conjunction(notRocketAtNola, rocketAtNola);
            Step flyRocketNolaNola = new Step("(fly Rocket NOLA NOLA)", flyNola2Precondition, flyNola2Effect);
            PlanGraphStep pgFlyRocketNolaNola0 = new PlanGraphStep(flyRocketNolaNola, 0);
            PlanGraphStep pgFlyRocketNolaNola1 = new PlanGraphStep(flyRocketNolaNola, 1);
            List<PlanGraphStep> mutexStepsForFlyNolaNola = new List<PlanGraphStep>();
            foreach (PlanGraphStep key in firstLevel.getMutuallyExclusiveSteps().Keys)
            {
                if ((key.getStep().CompareTo(pgFlyRocketNolaNola0.getStep()) == 0) ||
                   (key.getStep().CompareTo(pgFlyRocketNolaNola1.getStep()) == 0))
                {
                    mutexStepsForFlyNolaNola = firstLevel.getMutuallyExclusiveSteps()[key];
                    break;
                }
            }
            Assert.AreEqual(3, mutexStepsForFlyNolaNola.Count);
        }

        [TestMethod]
        public void entireCakePlanGraph()
        {
            PlanGraph cakeGraph = CreateCakeProblemPlanGraph();
            Assert.IsNotNull(cakeGraph);
            Assert.AreEqual(3, cakeGraph.CountLevels());
            cakeGraph.extend();
            Assert.AreEqual(4, cakeGraph.CountLevels());
        }

        [TestMethod]
        public void entireRocketPlanGraph()
        {
            PlanGraph rocketGraph = CreateRocketProblemPlanGraph();
            Assert.IsNotNull(rocketGraph);
        }

        [TestMethod]
        public void nullIsMutex()
        {
            PlanGraph cakeGraph = CreateCakeProblemPlanGraph();
            PlanGraphLevelMutex firstLevel = (PlanGraphLevelMutex)cakeGraph.getLevel(1);

            PlanGraphStep noopCargoNola = null;
            PlanGraphStep flyRocketNolaLondon = null;
            Assert.IsFalse(firstLevel.isMutex(noopCargoNola, flyRocketNolaLondon));
        }

        [TestMethod]
        public void isMutex()
        {
            PlanGraph cakeGraph = CreateCakeProblemPlanGraph();
            PlanGraphLevelMutex firstLevel = (PlanGraphLevelMutex)cakeGraph.getLevel(1);

            // Build Steps
            PlanGraphStep eatCake = getStep("(eat Cake)", cakeGraph.getAllPossiblePlanGraphSteps());
            PlanGraphStep noopHaveCake = getStep("(Persistence Step (have Cake))", cakeGraph.getAllPossiblePlanGraphSteps());
            PlanGraphStep noopNotEatCake = getStep("(Persistence Step (not (eaten Cake)))", cakeGraph.getAllPossiblePlanGraphSteps());
            Assert.IsTrue(firstLevel.isMutex(eatCake, noopHaveCake));
            Assert.IsTrue(firstLevel.isMutex(eatCake, noopNotEatCake));
            Assert.IsFalse(firstLevel.isMutex(noopHaveCake, noopNotEatCake));
        }

        [TestMethod]
        public void isContainsGoal()
        {
            Expression goal = CakeProblem().goal;
            PlanGraph cakeGraph = CreateCakeProblemPlanGraph();
            Assert.IsTrue(cakeGraph.containsGoal(goal));

            PlanGraphLevel rootLevel = cakeGraph.getRootLevel();
            PlanGraphLevel firstLevel = cakeGraph.getLevel(1);
            PlanGraphLevel secondLevel = cakeGraph.getLevel(2);
            Assert.IsFalse(rootLevel.containsGoal(goal));
            Assert.IsFalse(firstLevel.containsGoal(goal));
            Assert.IsTrue(secondLevel.containsGoal(goal));
        }

        [TestMethod]
        public void leveledOff()
        {
            PlanGraph cakeGraph = CreateCakeProblemPlanGraph();
            Assert.IsFalse(cakeGraph.isLeveledOff());
            cakeGraph.extend();
            Assert.IsFalse(cakeGraph.isLeveledOff());
            cakeGraph.extend();
            Assert.IsTrue(cakeGraph.isLeveledOff());
        }

        //	@Test
        //	public void planGraphToString(){
        //		assertFalse(true);
        //	}
    }
}

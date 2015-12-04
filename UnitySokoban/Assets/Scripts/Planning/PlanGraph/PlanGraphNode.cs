using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace PlanGraphProject
{
    public interface PlanGraphNode
    {
        int getInitialLevel();
        void setInitialLevel(int initialLevel);
        bool existsAtLevel(int level);
        List<PlanGraphNode> getParentNodes();
        List<PlanGraphNode> getChildNodes();
    }
}

using Planning;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace Planning.IO
{
    public class PDDLReader
    {
        public static Problem GetProblem(string domainString, string problemString)
        {
            PDDLParser parser = new PDDLParser();
            parser.parse<Domain>(domainString);
            return parser.parse<Problem>(problemString);
        }
    }
}

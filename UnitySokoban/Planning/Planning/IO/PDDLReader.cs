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
        private string _domainFilename;
        private string _problemFilename;

        public PDDLReader(string domainFilename, string problemFilename)
        {
            _domainFilename = domainFilename;
            _problemFilename = problemFilename;
        }

        public Problem GetProblem()
        {
            PDDLParser parser = new PDDLParser();
            using (StreamReader streamReader = new StreamReader(_domainFilename))
                parser.parse<Domain>(streamReader);
            using (StreamReader streamReader = new StreamReader(_problemFilename))
                return parser.parse<Problem>(streamReader);
        }
    }
}

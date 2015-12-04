using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace GraphPlanProject.Util
{
    static public class ListUtil
    {
        static public List<Object> CreateGenericList<T>(List<T> list)
        {
            List<Object> genericList = new List<Object>();
            foreach (T item in list)
                genericList.Add(item);
            return genericList;
        }

        static public List<T> TypeListFromGenericList<T>(List<Object> genericList)
        {
            List<T> list = new List<T>();
            foreach (Object item in genericList)
                list.Add((T)item);
            return list;
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Planning.Util
{
    public class PriorityQueue<T>
    {
        // The items and priorities.
        List<T> Values = new List<T>();
        List<int> Priorities = new List<int>();

        public int Count { get { return Values.Count; } }

        // Add an item to the queue.
        public void Enqueue(T new_value, int new_priority)
        {
            Values.Add(new_value);
            Priorities.Add(new_priority);
        }

        // Remove the item with the minimum priority from the queue.
        public T Dequeue()
        {
            // Find the hightest priority.
            int best_index = 0;
            int best_priority = Priorities[0];
            for (int i = 1; i < Priorities.Count; i++)
            {
                if (best_priority > Priorities[i])
                {
                    best_priority = Priorities[i];
                    best_index = i;
                }
            }

            // Return the corresponding item.
            T top_value = Values[best_index];
            int top_priority = best_priority;

            // Remove the item from the lists.
            Values.RemoveAt(best_index);
            Priorities.RemoveAt(best_index);

            return top_value;
        }

        public T Peek()
        {
            // Find the hightest priority.
            int best_index = 0;
            int best_priority = Priorities[0];
            for (int i = 1; i < Priorities.Count; i++)
            {
                if (best_priority > Priorities[i])
                {
                    best_priority = Priorities[i];
                    best_index = i;
                }
            }

            // Return the corresponding item.
            return Values[best_index];
        }

        public void Clear()
        {
            Priorities.Clear();
            Values.Clear();
        }

        public int Size()
        {
            return Values.Count;
        }
    }
}

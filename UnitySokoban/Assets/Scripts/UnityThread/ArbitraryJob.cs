using System.Threading;
using UnityEngine;

namespace UnityThread
{
    public class ArbitraryJob : ThreadJob
    {
        public Vector3[] InData;  // arbitary job data
        public Vector3[] OutData; // arbitary job data

        protected override void ThreadFunction()
        {
            try
            {
                // Do your threaded task. DON'T use the Unity API here
                for (int i = 0; i < 100000000; i++)
                    InData[i % InData.Length] += new Vector3(0, 1, 0.001f);
                //InData[i % InData.Length] += InData[(i + 1) % InData.Length];

                base.ThreadFunction();
            }
            catch (ThreadAbortException ex)
            {
                Debug.Log(ex);
            }
        }
    }
}
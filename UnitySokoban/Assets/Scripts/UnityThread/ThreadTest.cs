using UnityEngine;
using System.Collections;
using UnityThread;
using System;

public class ThreadTest : MonoBehaviour
{
    private ArbitraryJob _arbitraryJob;

    private void Start()
    {
        _arbitraryJob = new ArbitraryJob();
        _arbitraryJob.InData = new Vector3[10];
        _arbitraryJob.InData[0] = new Vector3(1, 0, 0.0000001f);
        _arbitraryJob.Start();
        OnEnable(); // Since start only runs once after OnEnable
    }

    private void OnEnable()
    {
        if (_arbitraryJob != null)
        {
            _arbitraryJob.OnThreadComplete += ThreadComplete;
            _arbitraryJob.OnThreadAbort += ThreadAbort;
        }
    }

    private void OnDisable()
    {
        if (_arbitraryJob != null)
        {
            _arbitraryJob.OnThreadComplete -= ThreadComplete;
            _arbitraryJob.OnThreadAbort -= ThreadAbort;
        }
    }

    private void ThreadComplete(ThreadJob threadJob)
    {
        if (threadJob is ArbitraryJob)
        {
            Vector3[] inData = ((ArbitraryJob)threadJob).InData;
            for (int i = 0; i < inData.Length; i++)
                Debug.Log("Results(" + i + "): " + inData[i]);
        }
        Debug.Log("Thread has been completed.");
    }

    private void ThreadAbort(ThreadJob threadJob)
    {
        Debug.Log("Thread has been aborted.");
    }

    public void Update()
    {
        if (Input.GetKeyDown(KeyCode.Space))
            if (_arbitraryJob.IsRunning())
                _arbitraryJob.Abort();
    }
}

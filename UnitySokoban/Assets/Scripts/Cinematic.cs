using UnityEngine;
using System.Collections;
using System;

public class Cinematic : MonoBehaviour
{
    // TODO: Look at AnimationCurve and look at Keyframe
    static public event Action OnCinematicFinish = delegate { };

    private float _timer;
    public float duration = 1;

    void Update()
    {
        _timer += Time.deltaTime;
        if (_timer >= duration)
        {
            OnCinematicFinish();
            Destroy(gameObject);
        }
    }
}

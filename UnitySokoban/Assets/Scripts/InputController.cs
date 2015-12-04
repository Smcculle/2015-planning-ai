using UnityEngine;
using System.Collections;
using System;

public class InputController : MonoBehaviour
{
    static public event Action OnUp = delegate { };
    static public event Action OnRight = delegate { };
    static public event Action OnDown = delegate { };
    static public event Action OnLeft = delegate { };

    // Update is called once per frame
    void Update()
    {
        if (Input.GetButtonDown("Horizontal"))
        {
            if (Input.GetAxis("Horizontal") < 0)
                OnLeft();
            if (Input.GetAxis("Horizontal") > 0)
                OnRight();
        }
        else if (Input.GetButtonDown("Vertical"))
        {
            if (Input.GetAxis("Vertical") < 0)
                OnDown();
            if (Input.GetAxis("Vertical") > 0)
                OnUp();
        }
    }
}

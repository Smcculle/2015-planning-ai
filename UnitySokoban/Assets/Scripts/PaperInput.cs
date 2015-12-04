using UnityEngine;
using System.Collections;
using System;

public class PaperInput : MonoBehaviour
{
    enum State { Down, Hold, Up}
    private Vector3 start;
    private Vector3 mouseDown;

    void Update()
    {
        if (Input.GetMouseButton(0))
        {
            if (Input.GetMouseButtonDown(0))
                MouseButton(State.Down);
            else
                MouseButton(State.Hold);
        }
        else if (Input.GetMouseButtonUp(0))
            MouseButton(State.Up);

        MouseWheel(Input.GetAxis("Mouse ScrollWheel"));
    }

    void MouseButton(State state)
    {
        switch (state)
        {
            case State.Down:
                start = transform.position;
                mouseDown = Input.mousePosition;
                break;
            case State.Hold:
                Vector3 delta = Input.mousePosition - mouseDown;
                delta *= 0.02f;
                transform.position = start + delta;
                break;
            case State.Up:
                start = Vector3.zero;
                mouseDown = Vector3.zero;
                break;
        }
    }

    private void MouseWheel(float delta)
    {
        if (delta == 0)
            return;

        delta += 1;
        Vector3 scale = transform.localScale;
        scale *= delta;
        scale.x = Mathf.Clamp(scale.x, 0, float.MaxValue);
        scale.y = Mathf.Clamp(scale.y, 0, float.MaxValue);
        scale.z = Mathf.Clamp(scale.z, 0, float.MaxValue);
        transform.localScale = scale;
    }
}

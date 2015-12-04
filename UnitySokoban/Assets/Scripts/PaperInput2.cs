using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

public class PaperInput2 : MonoBehaviour
{
    private bool isDragging;
    private Vector3 screenPoint;
    private Vector3 offset;
    private Vector3 startEuler;
    private Vector3 startMouse;

    void Start()
    {
        startEuler = transform.localEulerAngles;
    }

    void Update()
    {
        if (IsMouseOverGameObject())
        {
            if (Input.GetMouseButtonDown(0)) MouseDown();
            MouseScroll(Input.GetAxis("Mouse ScrollWheel"));
        }

        if (isDragging)
        {
            if (Input.GetMouseButtonUp(0)) MouseUp();
            else if (Input.GetMouseButton(0)) MouseDrag();
        }

        if ((transform.localEulerAngles - startEuler).magnitude > 0.5f)
            transform.localEulerAngles = Vector3.Lerp(transform.localEulerAngles, startEuler, Time.deltaTime * 5);
        else
            transform.localEulerAngles = startEuler;
    }

    void MouseDown()
    {
        isDragging = true;
        startMouse = Input.mousePosition;
        screenPoint = Camera.main.WorldToScreenPoint(gameObject.transform.position);
        Vector3 cursorPoint = new Vector3(Input.mousePosition.x, Input.mousePosition.y, screenPoint.z);
        offset = gameObject.transform.position - Camera.main.ScreenToWorldPoint(cursorPoint);
    }

    void MouseUp()
    {
        isDragging = false;
    }

    void MouseDrag()
    {
        Vector3 cursorPoint = new Vector3(Input.mousePosition.x, Input.mousePosition.y, screenPoint.z);
        Vector3 cursorPosition = Camera.main.ScreenToWorldPoint(cursorPoint) + offset;
        transform.position = cursorPosition;

        if (startMouse.x > Input.mousePosition.x)
            transform.Rotate(0, 1, 0);
        else if (startMouse.x < Input.mousePosition.x)
            transform.Rotate(0, -1, 0);

        Vector3 euler = transform.localEulerAngles;
        euler.y = Mathf.Clamp(euler.y, 160, 200);
        euler.y = Mathf.Clamp(euler.y,
            180 - Math.Abs(Input.mousePosition.x - startMouse.x) / transform.localScale.x / 15,
            180 + Math.Abs(Input.mousePosition.x - startMouse.x) / transform.localScale.x / 15);
        transform.localEulerAngles = euler;
    }

    void MouseScroll(float delta)
    {
        if (delta == 0)
            return;

        delta *= 1.5f;
        float scale = transform.localScale.x;
        scale *= Math.Abs(delta + 1);
        scale = Mathf.Clamp(scale, 1, float.MaxValue);
        transform.localScale = Vector3.one * scale;

        Vector3 screenPoint = Camera.main.WorldToScreenPoint(gameObject.transform.position);
        Vector3 cursorPoint = new Vector3(Input.mousePosition.x, Input.mousePosition.y, screenPoint.z);
        Vector3 offset = gameObject.transform.position - Camera.main.ScreenToWorldPoint(cursorPoint);
        offset *= Math.Abs(delta + 1);
        Vector3 cursorPosition = Camera.main.ScreenToWorldPoint(cursorPoint) + offset;
        transform.position = cursorPosition;
    }

    private bool IsMouseOverGameObject()
    {
        RaycastHit hit;
        Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
        if (Physics.Raycast(ray, out hit, 200))
            if (hit.transform == transform)
                return true;
        return false;
    }
}

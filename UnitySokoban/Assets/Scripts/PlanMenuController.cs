using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;

public class PlanMenuController : MonoBehaviour
{
    public delegate void PlanMenuHandler(PlanMenuController planMenu);
    static public event PlanMenuHandler OnClickCompute = delegate { };
    static public event Action OnClickCancel = delegate { };

    public GameObject NoveltyCheckbox;
    public GameObject PlannerRadioGroup;
    public GameObject PlannerFunctionGroup;

    public void ClickCancel()
    {
        OnClickCancel();
        Destroy(gameObject);
    }

    public void ClickCompute()
    {
        OnClickCompute(this);
        Destroy(gameObject);
    }

    public bool UseNovelty()
    {
        return NoveltyCheckbox.GetComponent<Toggle>().isOn;
    }

    public string GetPlanner()
    {
        foreach (Toggle toggle in PlannerRadioGroup.GetComponentsInChildren<Toggle>())
            if (toggle.isOn)
                return toggle.name;

        return null;
    }

    public string GetPlannerFunction()
    {
        foreach (Toggle toggle in PlannerFunctionGroup.GetComponentsInChildren<Toggle>())
            if (toggle.isOn)
                return toggle.name;

        return null;
    }
}

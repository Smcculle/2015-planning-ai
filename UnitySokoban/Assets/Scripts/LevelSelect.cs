using UnityEngine;
using System;
using UnityEngine.UI;

public class LevelSelect : MonoBehaviour
{
    static public event LevelSelectHandler OnClickLevel = delegate { };
    public delegate void LevelSelectHandler(int level);

    public GameObject ButtonPanel;
    public GameObject LevelButtonPrefab;

    void Start()
    {
        for (int i = 1; i <= LevelController.MAX_LEVEL; i++)
        {
            GameObject button = Instantiate(LevelButtonPrefab);
            button.name = "Level " + i;
            button.GetComponentInChildren<Text>().text = button.name;
            button.GetComponent<ButtonLevel>().level = i;
            button.transform.SetParent(ButtonPanel.transform, false);
        }
    }

    void OnEnable()
    {
        ButtonLevel.OnClickButton += ClickLevel;
    }

    void OnDisable()
    {
        ButtonLevel.OnClickButton -= ClickLevel;
    }

    public void ClickLevel(int level)
    {
        OnClickLevel(level);
        Destroy(gameObject);
    }
}
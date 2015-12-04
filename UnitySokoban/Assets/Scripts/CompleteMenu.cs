using UnityEngine;
using System.Collections;
using System;

public class CompleteMenu : MonoBehaviour
{
    static public event Action OnClickRestart = delegate { };
    static public event Action OnClickMenu = delegate { };
    static public event Action OnClickNextLevel = delegate { };

    public void ClickRestart()
    {
        OnClickRestart();
        FadeOut();
    }

    public void ClickMenu()
    {
        OnClickMenu();
        FadeOut();
    }

    public void ClickNextLevel()
    {
        OnClickNextLevel();
        FadeOut();
    }

    void FadeOut()
    {
        Destroy(gameObject);
    }
}
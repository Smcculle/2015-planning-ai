using UnityEngine;
using System;

public class PauseMenu : MonoBehaviour
{
    static public event Action OnClickResume = delegate { };

    public void ClickResume()
    {
        OnClickResume();
        FadeOut();
    }
    
    void FadeOut()
    {
        Destroy(gameObject);
    }
}

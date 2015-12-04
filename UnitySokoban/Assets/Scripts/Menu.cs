using System;
using UnityEngine;

public class Menu : MonoBehaviour
{
    static public event Action OnClickStart = delegate { };

    public void ClickStart()
    {
        OnClickStart();
        Destroy(gameObject);
    }
}

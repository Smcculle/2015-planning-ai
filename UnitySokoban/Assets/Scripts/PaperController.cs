using UnityEngine;
using System.Collections;

public class PaperController : MonoBehaviour
{
    static public event PaperHandler OnClickDone = delegate { };
    public delegate void PaperHandler();

    public void ClickDone()
    {
        OnClickDone();
        Destroy(gameObject);
    }
}
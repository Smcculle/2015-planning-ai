using UnityEngine;
using System.Collections;

public class Player : MonoBehaviour
{
    public delegate void StartHandler(GameObject player);
    static public event StartHandler OnStart = delegate { };

    void Start()
    {
        OnStart(gameObject);
    }
}

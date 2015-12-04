using UnityEngine;
using System.Collections;

public class CellManager : MonoBehaviour
{
    public int _x;
    public int _y;
    public GameObject gameObjectOnMe;

    public void SetCell(int x, int y)
    {
        _x = x;
        _y = y;
    }
}

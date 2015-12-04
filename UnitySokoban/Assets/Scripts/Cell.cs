using UnityEngine;
using System.Collections;

public class Cell : MonoBehaviour
{
    public int x;
    public int y;
    public GameObject cell;
    public bool isSolid;

    public void SetCell(GameObject cell, bool overwriteCell)
    {
        if (overwriteCell)
        {
            if (this.cell != null)
                this.cell.GetComponent<CellManager>().gameObjectOnMe = null;
            cell.GetComponent<CellManager>().gameObjectOnMe = gameObject;
        }
        this.cell = cell;
        x = cell.GetComponent<CellManager>()._x;
        y = cell.GetComponent<CellManager>()._y;
        transform.position = cell.transform.position;
    }

    public void SetCell(GameObject cell)
    {
        SetCell(cell, true);
    }
}

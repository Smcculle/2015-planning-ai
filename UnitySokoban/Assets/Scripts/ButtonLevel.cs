using UnityEngine;

public class ButtonLevel : MonoBehaviour
{
    static public event ClickButtonHandler OnClickButton = delegate { };
    public delegate void ClickButtonHandler(int level);

    public int level = -1;

    public void NotifyLevelSelectClick()
    {
        OnClickButton(level);
    }
}

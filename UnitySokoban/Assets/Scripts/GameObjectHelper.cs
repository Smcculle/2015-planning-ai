using UnityEngine;

static public class GameObjectHelper
{
    static public GameObject FindChildByName(GameObject gameObject, string name)
    {
        Transform transform = gameObject.transform;
        for (int i = 0; i < transform.childCount; i++)
            if (transform.GetChild(i).name == name)
                return transform.GetChild(i).gameObject;

        return null;
    }
}

using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class Status : MonoBehaviour
{
    private static GameObject _instance;
    private static string _text;
    private static Text _textComponent;

    void Start()
    {
        if (_instance != null)
            Destroy(_instance);

        _instance = gameObject;
        _textComponent = transform.FindChild("Canvas").FindChild("Text").GetComponent<Text>();
        _text = _textComponent.text;
    }

    public static void SetText(string text)
    {
        _text = text;
    }

    private void Update()
    {
        if (_textComponent != null)
            if (_textComponent.text != _text)
                _textComponent.text = _text;
    }
}
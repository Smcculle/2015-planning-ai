using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class DropShadow : MonoBehaviour
{
    public Vector3 offset = new Vector3(1, -1, 0);
    public Color color = Color.black;

    private GameObject _main;
    private GameObject _dropShadow;
    private Vector3 _previousOffset = Vector3.zero;
    private Text _text;
    private Text _mainText;
    private Text _dropShadowText;

    // Use this for initialization
    void Start()
    {
        _text = GetComponent<Text>();

        _main = Instantiate(gameObject);
        _main.name = "Main Text";
        _main.transform.localPosition = Vector3.zero;

        _dropShadow = Instantiate(gameObject);
        _dropShadow.name = "Drop Shadow";
        _dropShadow.transform.localPosition = Vector3.zero;
        _dropShadow.GetComponent<Text>().color = color;

        _text.enabled = false;
        Destroy(_main.GetComponent<DropShadow>());
        Destroy(_dropShadow.GetComponent<DropShadow>());

        _dropShadow.transform.SetParent(transform, false);
        _main.transform.SetParent(transform, false);

        _mainText = _main.GetComponent<Text>();
        _dropShadowText = _dropShadow.GetComponent<Text>();
    }

    // Update is called once per frame
    void Update()
    {
        if (_previousOffset != offset)
        {
            _dropShadow.transform.position -= _previousOffset;
            _dropShadow.transform.position += offset;
            _previousOffset = offset;
        }

        if (_text.text != _mainText.text)
            _mainText.text = _text.text;


        if (_mainText.text != _dropShadowText.text)
            _dropShadowText.text = _mainText.text;
    }
}

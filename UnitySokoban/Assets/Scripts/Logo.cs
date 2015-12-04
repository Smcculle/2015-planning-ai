using System;
using UnityEngine;

public class Logo : MonoBehaviour
{
    static public event Action OnLogoFinish = delegate { };

    public GameObject _sphere;
    public Rigidbody _sphereRigidBody;
    public float _timer = 0;
    public float _finishTimer = 2;

    void Start()
    {
        _sphere = GameObject.Find("Sphere");
        _sphereRigidBody = _sphere.GetComponent<Rigidbody>();
    }

    void Update()
    {
        if (_sphereRigidBody.velocity.sqrMagnitude < 0.01f)
            _timer += Time.deltaTime;

        if (_timer >= _finishTimer)
        {
            OnLogoFinish();
            Destroy(gameObject);
        }
    }
}

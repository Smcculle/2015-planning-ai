using UnityEngine;
using UnityEngine.UI;

public class Score : MonoBehaviour
{
    public int _level = 0;
    public int _moves = 0;
    public Text textUI;

    void OnEnable()
    {
        _level = LevelController.CURRENT_LEVEL;
        LevelController.OnMove += IncrementMoves;
    }

    void OnDisable()
    {
        LevelController.OnMove -= IncrementMoves;
    }

    void IncrementMoves()
    {
        _moves++;
        textUI.text = string.Format("Moves: {0}", _moves);
    }
}
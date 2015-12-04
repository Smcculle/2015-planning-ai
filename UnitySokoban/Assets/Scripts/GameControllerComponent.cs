using System;
using UnityEngine;

public class GameControllerComponent : MonoBehaviour
{
    public GameObject logoPrefab;
    public GameObject menuPrefab;
    public GameObject levelSelectPrefab;
    public GameObject levelPrefab;

    void OnEnable()
    {
        GameController.component = this;

        Logo.OnLogoFinish += StartMenu;
        Menu.OnClickStart += StartLevelSelect ;
        LevelSelect.OnClickLevel += StartGameAtLevel;
        CompleteMenu.OnClickMenu += StartMenu;
        CompleteMenu.OnClickRestart += StartGame;
        CompleteMenu.OnClickNextLevel += StartGameNextLevel;
    }

    void OnDisable()
    {
        //GameController.component = null;
        Logo.OnLogoFinish -= StartMenu;
        Menu.OnClickStart -= StartGame;
        CompleteMenu.OnClickMenu -= StartMenu;
        CompleteMenu.OnClickRestart -= StartGame;
        CompleteMenu.OnClickNextLevel -= StartGameNextLevel;
    }
    
    void Start()
    {
        StartLogo();
    }

    void StartLogo()
    {
        Instantiate(logoPrefab).name = logoPrefab.name;
        Status.SetText("Showing Logo...");
    }

    void StartMenu()
    {
        Instantiate(menuPrefab).name = menuPrefab.name;
        Status.SetText("Showing Menu...");
    }

    void StartLevelSelect()
    {
        Instantiate(levelSelectPrefab).name = levelSelectPrefab.name;
        Status.SetText("Showing Level Select...");
    }

    void StartGame()
    {
        Instantiate(levelPrefab).name = levelPrefab.name;
        Status.SetText("Showing Level...");
    }

    void StartGameNextLevel()
    {
        LevelController.CURRENT_LEVEL = 
            Math.Min(LevelController.CURRENT_LEVEL + 1, LevelController.MAX_LEVEL);
        StartGame();
    }

    void StartGameAtLevel(int level)
    {
        LevelController.CURRENT_LEVEL = Math.Min(level, LevelController.MAX_LEVEL);
        StartGame();
    }
}

using UnityEngine;
using System.Collections;
using System;
using System.IO;
using Planning;
using Planning.IO;
using StateSpaceSearchProject;
using Planning.Logic;
using System.Collections.Generic;
using UnityThread;
using PlanGraphSGW;

public class LevelController : MonoBehaviour
{
    static public int MAX_LEVEL = 5;
    static public int CURRENT_LEVEL = 3;
    static public event Action OnMove = delegate { };
    static public TextAsset domain;
    static public int num_times_enables = 0;
    static readonly Vector2 NullVector2 = new Vector2(float.NegativeInfinity, float.NegativeInfinity);

    public Level level;
    public int numberTimesEnables = 0;
    public MainGrid grid;
    public GameObject player;
    public Cell playerCell;
    public int boxStrength = 1;
    public GameObject SolidPrefab;
    public GameObject MoveablePrefab;
    public GameObject TargetPrefab;
    public GameObject PlayerPrefab;
    public GameObject PlayerGhostPrefab;
    public GameObject BoxGhostPrefab;
    public GameObject ArrowPrefab;
    public GameObject CompleteMenuPrefab;
    public GameObject FailMenuPrefab;
    public GameObject PauseMenuPrefab;
    public GameObject StartingCinematicPrefab;
    public GameObject MiddleCinematicPrefab;
    public GameObject EndingCinematicPrefab;
    private int _boxesPushed = 0;
    private bool _isComplete;
    private bool _isPause;
    private GameObject ghosts;
    private GameObject arrows;
    public PlanController planController;
    public GameObject planButton;
    private GameObject plangraph;
    internal int pgLevel;
    internal float pgLevelTimer;

    enum Direction { UP, DOWN, LEFT, RIGHT }

    void OnEnable()
    {
        num_times_enables++;
        numberTimesEnables = num_times_enables;

        InputController.OnUp += MoveUp;
        InputController.OnDown += MoveDown;
        InputController.OnLeft += MoveLeft;
        InputController.OnRight += MoveRight;
        CompleteMenu.OnClickMenu += FadeOutAndDestroy;
        CompleteMenu.OnClickRestart += FadeOutAndDestroy;
        CompleteMenu.OnClickNextLevel += FadeOutAndDestroy;
        PauseMenu.OnClickResume += ResumeGame;
    }

    void CreateLevel()
    {
        char[,] data = LevelReader.ReadLevel(CURRENT_LEVEL);
        level = Level.Create(data);
        grid = GameObjectHelper.FindChildByName(gameObject, "Ground").GetComponent<MainGrid>();
        grid.transform.localScale = new Vector3(level.width, level.height, level.height);
        grid.SetGrid(level.width, level.height);
        Camera.main.transform.localPosition = new Vector3(0, 10, 0);
        Camera.main.transform.localRotation = Quaternion.Euler(90, 0, 0);
    }

    void PlayCinematic(GameObject CinematicPrefab, bool resume = true)
    {
        if (CinematicPrefab == null)
            return;

        Status.SetText("Playing Cinematic...");
        PauseGame();
        GameObject cinematic = Instantiate(CinematicPrefab);
        cinematic.name = CinematicPrefab.name;
        cinematic.transform.SetParent(transform);

        if (resume)
            Cinematic.OnCinematicFinish += ResumeGame;
    }

    void OnDisable()
    {
        InputController.OnUp -= MoveUp;
        InputController.OnDown -= MoveDown;
        InputController.OnLeft -= MoveLeft;
        InputController.OnRight -= MoveRight;
        CompleteMenu.OnClickMenu -= FadeOutAndDestroy;
        CompleteMenu.OnClickRestart -= FadeOutAndDestroy;
        CompleteMenu.OnClickNextLevel -= FadeOutAndDestroy;
        PauseMenu.OnClickResume -= ResumeGame;
    }

    void Start()
    {
        CreateLevel();
        GameObject solids = new GameObject() { name = "Solids" };
        GameObject moveables = new GameObject() { name = "Moveable Boxes" };
        GameObject targets = new GameObject() { name = "Targets" };

        solids.transform.SetParent(transform);
        moveables.transform.SetParent(transform);
        targets.transform.SetParent(transform);

        for (int x = 0; x < level.width; x++)
            for (int y = 0; y < level.height; y++)
                switch (level.data[x, y])
                {
                    case '1':
                        GameObject solid = Spawn(SolidPrefab, grid.GetCell(x, y));
                        solid.name += " " + solids.transform.childCount;
                        solid.transform.SetParent(solids.transform);
                        break;
                    case 'S':
                        player = Spawn(PlayerPrefab, grid.GetCell(x, y));
                        player.transform.SetParent(transform);
                        playerCell = player.GetComponent<Cell>();
                        break;
                    case 'B':
                        GameObject box = Spawn(MoveablePrefab, grid.GetCell(x, y));
                        box.name += " " + moveables.transform.childCount;
                        box.transform.SetParent(moveables.transform);
                        break;
                    case 'T':
                        GameObject target = Spawn(TargetPrefab, grid.GetCell(x, y));
                        target.name += " " + targets.transform.childCount;
                        target.transform.SetParent(targets.transform);
                        grid.GetCell(x, y).GetComponent<CellManager>().gameObjectOnMe = null;
                        break;
                }

        if (domain == null)
            domain = Resources.Load<TextAsset>("Sokoban_domain");

        Status.SetText("Showing Level " + CURRENT_LEVEL + "...");
        PlayCinematic(StartingCinematicPrefab);
    }

    void Update()
    {
        if (Input.GetKeyDown(KeyCode.F) && !_isComplete && !_isPause)
        {
            _isComplete = true;
            Instantiate(FailMenuPrefab).name = FailMenuPrefab.name;
            PlayCinematic(EndingCinematicPrefab, false);
            PauseGame();
        }
        if (Input.GetKeyDown(KeyCode.P) && !_isComplete && !_isPause)
        {
            Instantiate(PauseMenuPrefab).name = PauseMenuPrefab.name;
            PauseGame();
        }
        if (Input.GetKeyDown(KeyCode.C) && !_isComplete && !_isPause)
        {
            PlayCinematic(MiddleCinematicPrefab);
        }
        CheckForCompleteNextState();
        CheckForCompleteSolution();
        CheckForPlanGraph();
    }

    public void PauseGame()
    {
        _isPause = true;
        InputController.OnUp -= MoveUp;
        InputController.OnDown -= MoveDown;
        InputController.OnLeft -= MoveLeft;
        InputController.OnRight -= MoveRight;
    }

    public void ResumeGame()
    {
        Cinematic.OnCinematicFinish -= ResumeGame;

        if (!_isPause)
            return;

        _isPause = false;
        InputController.OnUp += MoveUp;
        InputController.OnDown += MoveDown;
        InputController.OnLeft += MoveLeft;
        InputController.OnRight += MoveRight;
    }

    void MoveUp() { Move(playerCell, Direction.UP); }
    void MoveDown() { Move(playerCell, Direction.DOWN); }
    void MoveLeft() { Move(playerCell, Direction.LEFT); }
    void MoveRight() { Move(playerCell, Direction.RIGHT); }

    void Move(Cell cell, Direction direction)
    {
        GameObject destinationCell = null;
        int x = cell.x;
        int y = cell.y;
        switch (direction)
        {
            case Direction.UP:
                destinationCell = grid.GetCell(x, y + 1);
                break;
            case Direction.DOWN:
                destinationCell = grid.GetCell(x, y - 1);
                break;
            case Direction.LEFT:
                destinationCell = grid.GetCell(x - 1, y);
                break;
            case Direction.RIGHT:
                destinationCell = grid.GetCell(x + 1, y);
                break;
        }

        if (isValidMove(destinationCell, direction))
        {
            DeleteHelpers();
            cell.SetCell(destinationCell);
            if (cell.gameObject == player)
                OnMove();
        }

        _boxesPushed = 0;
        CheckTargets();
    }

    bool isValidMove(GameObject cell, Direction direction)
    {
        if (cell == null)
            return false;

        if (!isPassable(cell))
            return false;

        if (!isMovable(cell, direction))
            return false;

        return true;
    }

    private bool isMovable(GameObject cell, Direction direction)
    {
        CellManager cellManager = cell.GetComponent<CellManager>();
        if (cellManager.gameObjectOnMe != null)
        {
            Cell cellsGameObjectCell = cellManager.gameObjectOnMe.GetComponent<Cell>();
            _boxesPushed++;
            if (_boxesPushed <= boxStrength)
                Move(cellsGameObjectCell, direction);

            if (cellManager.gameObjectOnMe == null)
                return true;

            return false;
        }
        return true;
    }

    private bool isPassable(GameObject cell)
    {
        CellManager cellManager = cell.GetComponent<CellManager>();
        if (cellManager.gameObjectOnMe != null)
        {
            Cell cellsGameObjectCell = cellManager.gameObjectOnMe.GetComponent<Cell>();
            if (cellsGameObjectCell != null)
                return !cellsGameObjectCell.isSolid;
        }
        return true;
    }

    private void CheckTargets()
    {
        bool allTargetsDone = true;
        GameObject[] targets = GameObject.FindGameObjectsWithTag("Target");
        foreach (GameObject target in targets)
        {
            Cell cell = target.GetComponent<Cell>();
            GameObject c = grid.grid[cell.x, cell.y];
            CellManager cm = c.GetComponent<CellManager>();
            GameObject onCell = cm.gameObjectOnMe;
            if (onCell == null || onCell.name == "Player")
            {
                allTargetsDone = false;
                break;
            }
        }

        if (targets.GetLength(0) == 0)
            return;

        if (allTargetsDone && !_isComplete)
        {
            PlayCinematic(EndingCinematicPrefab, false);
            PauseGame();
            _isComplete = true;
            Instantiate(CompleteMenuPrefab).name = CompleteMenuPrefab.name;
        }
    }

    GameObject Spawn(GameObject spawnObject, GameObject cell)
    {
        GameObject newObject = Instantiate(spawnObject);
        newObject.name = spawnObject.name;
        newObject.GetComponent<Cell>().SetCell(cell);
        return newObject;
    }

    void FadeOutAndDestroy()
    {
        Destroy(gameObject);
    }

    private void CheckForCompleteNextState()
    {
        if (planController.planMenuState != PlanController.PlanMenuState.Complete)
            return;

        if (planController._plannerFunction != "NextState")
            return;

        if (planController.GetThread() == null)
            return;

        if (_isPause) ResumeGame();
        CreateHelpers();
        foreach (KeyValuePair<StateSpaceNode, int> entry in planController.GetThread().GetResult())
        {
            Vector2 playerPosition = GetPlayerPosition(entry.Key.state);
            int x = Convert.ToInt32(playerPosition.x);
            int y = Convert.ToInt32(playerPosition.y);
            if (grid.grid[x, y].GetComponent<CellManager>().gameObjectOnMe == null)
            {
                GameObject playerGhost = Instantiate(PlayerGhostPrefab);
                playerGhost.transform.SetParent(ghosts.transform);
                playerGhost.GetComponent<Cell>().SetCell(grid.grid[x, y]);
                string cost = entry.Value == int.MaxValue ? "∞" : entry.Value.ToString();
                playerGhost.transform.FindChild("Cost").GetComponent<TextMesh>().text = cost;
                grid.grid[x, y].GetComponent<CellManager>().gameObjectOnMe = null;
            }

            foreach (Vector2 boxPosition in GetBoxPositions(entry.Key))
            {
                x = Convert.ToInt32(boxPosition.x);
                y = Convert.ToInt32(boxPosition.y);
                if (grid.grid[x, y].GetComponent<CellManager>().gameObjectOnMe == null)
                {
                    GameObject boxGhost = Instantiate(BoxGhostPrefab);
                    boxGhost.transform.SetParent(ghosts.transform);
                    boxGhost.GetComponent<Cell>().SetCell(grid.grid[x, y]);
                    string cost = entry.Value == int.MaxValue ? "∞" : entry.Value.ToString();
                    boxGhost.transform.FindChild("Cost").GetComponent<TextMesh>().text = cost;
                    grid.grid[x, y].GetComponent<CellManager>().gameObjectOnMe = null;
                }
            }
        }
        planController.DeleteThread();
    }

    private void CheckForCompleteSolution()
    {
        if (planController.planMenuState != PlanController.PlanMenuState.Complete)
            return;

        if (planController._plannerFunction != "Solution")
            return;

        if (planController.GetThread() == null)
            return;

        if (_isPause) ResumeGame();
        ShowPlanArrows(planController.GetThread().GetPlan());
        planController.DeleteThread();
    }

    private void ShowPlanArrows(Plan plan)
    {
        transform.FindChild("Player").gameObject.SetActive(true);
        transform.FindChild("Moveable Boxes").gameObject.SetActive(true);

        if (plan == null)
            return;

        DeleteHelpers();
        CreateHelpers();
        foreach (Step step in plan)
        {
            GameObject arrow = Instantiate(ArrowPrefab);
            arrow.transform.SetParent(arrows.transform);
            Point point = SokobanDomain.GetPlayerPosition(step);
            arrow.GetComponent<Cell>().SetCell(grid.grid[point.x, point.y], false);

            string direction = SokobanDomain.GetDirectionFromStep(step);
            if (direction == "Left")
                arrow.transform.localEulerAngles += new Vector3(0, -90, 0);
            else if (direction == "Right")
                arrow.transform.localEulerAngles += new Vector3(0, 90, 0);
            else if (direction == "Up")
                arrow.transform.localEulerAngles += new Vector3(0, 0, 0);
            else if (direction == "Down")
                arrow.transform.localEulerAngles += new Vector3(0, 180, 0);
        }
    }

    private void CreateHelpers()
    {
        ghosts = new GameObject();
        ghosts.name = "Ghosts";
        ghosts.transform.SetParent(transform);
        arrows = new GameObject();
        arrows.name = "Arrows";
        arrows.transform.SetParent(transform);
        plangraph = new GameObject();
        plangraph.name = "PlanGraph";
        plangraph.transform.SetParent(transform);
    }

    public void DeleteHelpers()
    {
        if (ghosts != null)
            Destroy(ghosts);

        if (arrows != null)
            Destroy(arrows);

        if (plangraph != null)
            Destroy(plangraph);

        ghosts = arrows = plangraph = null;
    }

    private List<Vector2> GetBoxPositions(StateSpaceNode node)
    {
        List<Vector2> boxPositions = new List<Vector2>();
        foreach (Step step in node.plan)
            if (!step.name.Contains("pushbox"))
                return boxPositions;

        State state = node.state;
        foreach (Literal literal in state.Literals)
            if (literal is Predication)
            {
                Predication predication = (Predication)literal;
                if (predication.predicate == "has_box")
                {
                    string[] cell = predication.terms.get(0).name.Split('_');
                    boxPositions.Add(new Vector2(Convert.ToSingle(cell[1]), Convert.ToSingle(cell[2])));
                }
            }
        return boxPositions;
    }

    private Vector2 GetPlayerPosition(State state)
    {
        foreach (Literal literal in state.Literals)
            if (literal is Predication)
            {
                Predication predication = (Predication)literal;
                if (predication.predicate == "has_player")
                {
                    string[] cell = predication.terms.get(0).name.Split('_');
                    return new Vector2(Convert.ToSingle(cell[1]), Convert.ToSingle(cell[2]));
                }
            }
        return NullVector2;
    }

    public void ConvertSSNodeToGhosts(StateSpaceNode _previousNode)
    {
        if (ghosts == null)
            CreateHelpers();

        if (transform.FindChild("Player").gameObject.activeSelf)
            transform.FindChild("Player").gameObject.SetActive(false);

        if (transform.FindChild("Moveable Boxes").gameObject.activeSelf)
            transform.FindChild("Moveable Boxes").gameObject.SetActive(false);

        for (int i = 0; i < ghosts.transform.childCount; i++)
        {
            GameObject ghost = ghosts.transform.GetChild(i).gameObject;
            ghost.transform.localScale = Vector3.one;
            Renderer renderer = ghost.transform.GetChild(0).GetComponent<Renderer>();
            Color color = renderer.material.color;
            if (ghost.name.Contains("Player"))
            {
                Color otherColor = PlayerGhostPrefab.transform.GetChild(0).GetComponent<Renderer>().sharedMaterial.color;
                color.r = otherColor.r;
                color.g = otherColor.g;
                color.b = otherColor.b;
            }
            else
            {
                Color otherColor = BoxGhostPrefab.transform.GetChild(0).GetComponent<Renderer>().sharedMaterial.color;
                color.r = otherColor.r;
                color.g = otherColor.g;
                color.b = otherColor.b;
            }
            color.a *= 0.7f;
            color.a = Mathf.Clamp01(color.a);

            renderer.material.color = color;
        }

        foreach (Literal literal in _previousNode.state.Literals)
        {
            if (literal is Predication)
                if (((Predication)literal).predicate == "has_player")
                    CreatePlayerGhost((Predication)literal);

            if (literal is Predication)
                if (((Predication)literal).predicate == "has_box")
                    CreateBoxGhost((Predication)literal);
        }
    }

    private GameObject CreatePlayerGhost(Predication predication)
    {
        string[] cell = predication.terms.get(0).name.Split('_');
        int x = Convert.ToInt32(cell[1]);
        int y = Convert.ToInt32(cell[2]);
        GameObject playerGhost = Instantiate(PlayerGhostPrefab);
        playerGhost.transform.SetParent(ghosts.transform);
        playerGhost.GetComponent<Cell>().SetCell(grid.grid[x, y], false);
        playerGhost.transform.FindChild("Cost").GetComponent<TextMesh>().text = "";
        playerGhost.transform.localScale *= 1.1f;
        playerGhost.transform.localPosition += new Vector3(0, 0.51f, 0);
        Renderer renderer = playerGhost.transform.GetChild(0).GetComponent<Renderer>();
        renderer.material.color = new Color(0, 1, 0);
        return playerGhost;
    }

    private GameObject CreateBoxGhost(Predication predication)
    {
        string[] cell = predication.terms.get(0).name.Split('_');
        int x = Convert.ToInt32(cell[1]);
        int y = Convert.ToInt32(cell[2]);
        GameObject boxGhost = Instantiate(BoxGhostPrefab);
        boxGhost.transform.SetParent(ghosts.transform);
        boxGhost.GetComponent<Cell>().SetCell(grid.grid[x, y], false);
        boxGhost.transform.FindChild("Cost").GetComponent<TextMesh>().text = "";
        boxGhost.transform.localScale *= 1.1f;
        Renderer renderer = boxGhost.transform.GetChild(0).GetComponent<Renderer>();
        renderer.material.color = new Color(0, 0, 1);
        return boxGhost;
    }

    private void CheckForPlanGraph()
    {
        if (planController._plannerFunction != "PlanGraph")
            return;

        if (planController.GetThread() == null)
            return;

        PlanGraph planGraph = planController.GetThread().GetPlanGraph();
        if (planController.planMenuState == PlanController.PlanMenuState.Complete
        || planController.planMenuState == PlanController.PlanMenuState.Wait)
        {
            if (pgLevelTimer > 0.5f) pgLevel++;
            if (pgLevelTimer > 0.5f) pgLevelTimer = 0;
            ShowPlanGraphStates(pgLevel, planGraph.literals);
            pgLevelTimer += Time.deltaTime;

            if (pgLevel >= planGraph.Size() - 1)
            {
                if (_isPause) ResumeGame();
                planController.DeleteThread();
            }
        }
    }

    private void ShowPlanGraphStates(int level, LiteralNode[] literals)
    {

        DeleteHelpers();
        CreateHelpers();

        transform.FindChild("Player").gameObject.SetActive(true);
        transform.FindChild("Moveable Boxes").gameObject.SetActive(true);
        foreach (LiteralNode literalNode in literals)
        {
            if (literalNode.getLevel() < 0 || literalNode.getLevel() > level)
                continue;

            Literal literal = literalNode.literal;
            if (literal is Predication)
                if (((Predication)literal).predicate == "has_player")
                    CreatePlayerGhost((Predication)literal).transform.SetParent(plangraph.transform);

            if (literal is Predication)
                if (((Predication)literal).predicate == "has_box")
                    CreateBoxGhost((Predication)literal).transform.SetParent(plangraph.transform);
        }

        for (int i = 0; i < plangraph.transform.childCount; i++)
        {
            GameObject pg = plangraph.transform.GetChild(i).gameObject;
            pg.transform.localScale = Vector3.one;
            Renderer renderer = pg.transform.GetChild(0).GetComponent<Renderer>();
            Color color = renderer.material.color;
            if (pg.name.Contains("Player"))
            {
                Color otherColor = PlayerGhostPrefab.transform.GetChild(0).GetComponent<Renderer>().sharedMaterial.color;
                color.r = otherColor.r;
                color.g = otherColor.g;
                color.b = otherColor.b;
            }
            else
            {
                Color otherColor = BoxGhostPrefab.transform.GetChild(0).GetComponent<Renderer>().sharedMaterial.color;
                color.r = otherColor.r;
                color.g = otherColor.g;
                color.b = otherColor.b;
            }
            color.a *= 0.7f;
            color.a = Mathf.Clamp01(color.a);

            renderer.material.color = color;
        }
    }
}

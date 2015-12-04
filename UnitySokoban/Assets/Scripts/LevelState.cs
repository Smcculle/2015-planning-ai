using System.Collections.Generic;
using UnityEngine;

public class LevelState
{
    private GameObject _player;
    private List<GameObject> _solids;
    private List<GameObject> _targets;
    private List<GameObject> _moveableBoxes;
    private List<GameObject> _cells;
    private Level _level;
    private int levelNumber;

    public LevelState(GameObject level, Level levelInfo)
    {
        levelNumber = LevelController.CURRENT_LEVEL;
        _level = levelInfo;
        _player = level.transform.FindChild("Player").gameObject;
        _solids = GetChildren(level.transform.FindChild("Solids"));
        _targets = GetChildren(level.transform.FindChild("Targets"));
        _moveableBoxes = GetChildren(level.transform.FindChild("Moveable Boxes"));
        _cells = GetChildren(level.transform.FindChild("Ground"));
    }

    public string ToPDDL()
    {
        string pddl = "";
        pddl += string.Format("(:problem Sokoban_Level_{0}\r\n", levelNumber);
        pddl += string.Format("  (:domain Sokoban_domain)\r\n");
        pddl += string.Format("  (:objects\r\n");

        foreach (GameObject cell in _cells)
        {
            CellManager cellManager = cell.GetComponent<CellManager>();
            pddl += string.Format("    Cell_{0}_{1} - cell\r\n", cellManager._x, cellManager._y);
        }
        pddl += string.Format("  )\n");
        pddl += string.Format("  (:initial\r\n");
        pddl += string.Format("    (and\r\n");

        Cell playerCell = _player.GetComponent<Cell>();
        pddl += string.Format("      (has_player Cell_{0}_{1})\r\n", playerCell.x, playerCell.y);

        foreach (GameObject box in _moveableBoxes)
        {
            Cell cell = box.GetComponent<Cell>();
            pddl += string.Format("      (has_box Cell_{0}_{1})\r\n", cell.x, cell.y);
        }

        foreach (GameObject solid in _solids)
        {
            Cell cell = solid.GetComponent<Cell>();
            pddl += string.Format("      (is_solid Cell_{0}_{1})\r\n", cell.x, cell.y);
        }

        for (int x = 0; x < _level.width; x++)
            for (int y = 0; y < _level.height; y++)
            {
                if (x - 1 >= 0)
                    pddl += string.Format("      (adjacent Cell_{0}_{2} Cell_{1}_{2})\r\n", x, x - 1, y);
                if (x + 1 < _level.width)
                    pddl += string.Format("      (adjacent Cell_{0}_{2} Cell_{1}_{2})\r\n", x, x + 1, y);
                if (y - 1 >= 0)
                    pddl += string.Format("      (adjacent Cell_{0}_{1} Cell_{0}_{2})\r\n", x, y, y - 1);
                if (y + 1 < _level.height)
                    pddl += string.Format("      (adjacent Cell_{0}_{1} Cell_{0}_{2})\r\n", x, y, y + 1);
            }

        for (int x = 0; x < _level.width; x++)
            for (int y = 0; y < _level.height; y++)
            {
                if (x - 2 >= 0)
                    pddl += string.Format("      (adjacent_2 Cell_{0}_{2} Cell_{1}_{2})\r\n", x, x - 2, y);
                if (x + 2 < _level.width)
                    pddl += string.Format("      (adjacent_2 Cell_{0}_{2} Cell_{1}_{2})\r\n", x, x + 2, y);
                if (y - 2 >= 0)
                    pddl += string.Format("      (adjacent_2 Cell_{0}_{1} Cell_{0}_{2})\r\n", x, y, y - 2);
                if (y + 2 < _level.height)
                    pddl += string.Format("      (adjacent_2 Cell_{0}_{1} Cell_{0}_{2})\r\n", x, y, y + 2);
            }

        pddl += string.Format("    )\r\n");
        pddl += string.Format("  )\r\n");
        pddl += string.Format("  (:goal\r\n");
        pddl += string.Format("    (and\r\n");

        foreach (GameObject target in _targets)
        {
            Cell cell = target.GetComponent<Cell>();
            pddl += string.Format("      (has_box Cell_{0}_{1})\r\n", cell.x, cell.y);
        }
        pddl += string.Format("    )\r\n");
        pddl += string.Format("  )\r\n");
        pddl += string.Format(")\r\n");

        return pddl;
    }

    private List<GameObject> GetChildren(Transform parent)
    {
        List<GameObject> children = new List<GameObject>();
        for (int i = 0; i < parent.childCount; i++)
            children.Add(parent.GetChild(i).gameObject);
        return children;
    }
}

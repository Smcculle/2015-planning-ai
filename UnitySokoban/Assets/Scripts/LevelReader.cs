using System;
using System.Collections.Generic;
using System.IO;
using UnityEngine;

class LevelReader
{
    public static char[,] ReadLevel(int levelNumber)
    {
        char[,] level = null;
        TextAsset test = Resources.Load<TextAsset>("Levels/level" + levelNumber);

        int height = 0;
        int width = 0;
        string levelString = test.text.Replace("\r", "");
        string[] lines = levelString.Split('\n');
        foreach (string line in lines)
        {
            height++;
            width = Math.Max(width, line.Length);
        }

        level = new char[width, height];
        for (int y = 0; y < lines.Length; y++)
            for (int x = 0; x < lines[y].Length; x++)
                level[x, y] = lines[y][x];

        return level;
    }
}

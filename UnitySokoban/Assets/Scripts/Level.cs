public class Level
{
    public char[,] data;
    public int width;
    public int height;

    public Level(char[,] data, int width, int height)
    {
        this.data = data;
        this.width = width;
        this.height = height;

    }

    static public Level Create(char[,] data)
    {
        return new Level(data, data.GetLength(0), data.GetLength(1));
    }

}

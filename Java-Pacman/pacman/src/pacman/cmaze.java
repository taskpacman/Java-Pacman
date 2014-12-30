package pacman;

import java.awt.*;
import java.util.Random;

/* построить лабиринт */
public class cmaze {
    // константы
    static final int BLANK = 0;
    static final int WALL = 1;
    static final int DOOR = 2;
    static final int DOT = 4;
    static final int POWER_DOT = 8;

    static final int HEIGHT = 16;
    static final int WIDTH = 21;

    static final int iHeight = HEIGHT * 16;
    static final int iWidth = WIDTH * 16;

    Window applet;
    Graphics graphics;

    // изобрадение лабиринта
    Image imageMaze;

    // изображение точки
    Image imageDot;

    // сколько всего точек
    int iTotalDotcount;

    // статус лабиринта
    int[][] iMaze;

    // инициализация лабиринта
    cmaze(Window a, Graphics g) {
        // установка ассоциаций
        applet = a;
        graphics = g;

        imageMaze = applet.createImage(iWidth, iHeight);
        imageDot = applet.createImage(2, 2);

        // задаем длину/ширину
        iMaze = new int[HEIGHT][WIDTH];
    }

    public void start() {
        Random generator = new Random();
        //выбор уровня
        int level = generator.nextInt(7) + 1;
        switch (level) {
            case 1:
                ctables.MazeDefine = ctables.MazeDefine_lvl1;
                break;
            case 2:
                ctables.MazeDefine = ctables.MazeDefine_lvl2;
                break;
            case 3:
                ctables.MazeDefine = ctables.MazeDefine_lvl5;
                break;
            case 4:
                ctables.MazeDefine = ctables.MazeDefine_lvl6;
                break;
            case 5:
                ctables.MazeDefine = ctables.MazeDefine_lvl7;
                break;
            case 6:
                ctables.MazeDefine = ctables.MazeDefine_lvl8;
                break;
            case 7:
                ctables.MazeDefine = ctables.MazeDefine_lvl9;
                break;
        }
        int i, j, k;
        iTotalDotcount = 0;
        for (i = 0; i < HEIGHT; i++)
            for (j = 0; j < WIDTH; j++) {
                switch (ctables.MazeDefine[i].charAt(j)) {
                    case ' ':
                        k = BLANK;
                        break;
                    case 'X':
                        k = WALL;
                        break;
                    case '.':
                        k = DOT;
                        iTotalDotcount++;
                        break;
                    case 'O':
                        k = POWER_DOT;
                        break;
                    case '-':
                        k = DOOR;
                        break;
                    default:
                        k = DOT;
                        iTotalDotcount++;
                        break;
                }
                iMaze[i][j] = k;
            }
        //создать изображение лабиринта
        createImage();
    }

    public void draw() {
        graphics.drawImage(imageMaze, 0, 0, applet);
        drawDots();
    }

    void drawDots()
    {
        int i, j;

        for (i = 0; i < HEIGHT; i++)
            for (j = 0; j < WIDTH; j++) {
                if (iMaze[i][j] == DOT)
                    graphics.drawImage(imageDot, j * 16 + 7, i * 16 + 7, applet);
            }
    }

    void createImage() {
        // создаем изображение точнки
        cimage.drawDot(imageDot);

        // создаем изображение лабиринта
        Graphics gmaze = imageMaze.getGraphics();

        // фон
        gmaze.setColor(Color.black);
        gmaze.fillRect(0, 0, iWidth, iHeight);

        DrawWall(gmaze);
    }

    public void DrawDot(int icol, int iRow) {
        if (iMaze[iRow][icol] == DOT)
            graphics.drawImage(imageDot, icol * 16 + 7, iRow * 16 + 7, applet);
    }

    void DrawWall(Graphics g) {
        int i, j;
        int iDir;

        g.setColor(Color.blue);

        for (i = 0; i < HEIGHT; i++) {
            for (j = 0; j < WIDTH; j++) {
                for (iDir = ctables.RIGHT; iDir <= ctables.DOWN; iDir++) {
                    if (iMaze[i][j] == DOOR) {
                        g.drawLine(j * 16, i * 16 + 8, j * 16 + 16, i * 16 + 8);
                        continue;
                    }
                    if (iMaze[i][j] != WALL) continue;
                    switch (iDir) {
                        case ctables.UP:
                            if (i == 0) break;
                            if (iMaze[i - 1][j] == WALL)
                                break;
                            DrawBoundary(g, j, i - 1, ctables.DOWN);
                            break;
                        case ctables.RIGHT:
                            if (j == WIDTH - 1) break;
                            if (iMaze[i][j + 1] == WALL)
                                break;
                            DrawBoundary(g, j + 1, i, ctables.LEFT);
                            break;
                        case ctables.DOWN:
                            if (i == HEIGHT - 1) break;
                            if (iMaze[i + 1][j] == WALL)
                                break;
                            DrawBoundary(g, j, i + 1, ctables.UP);
                            break;
                        case ctables.LEFT:
                            if (j == 0) break;
                            if (iMaze[i][j - 1] == WALL)
                                break;
                            DrawBoundary(g, j - 1, i, ctables.RIGHT);
                            break;
                        default:
                    }
                }
            }
        }
    }

    //рисование границ
    void DrawBoundary(Graphics g, int col, int row, int iDir) {
        int x, y;

        x = col * 16;
        y = row * 16;

        switch (iDir) {
            case ctables.LEFT:
                if (iMaze[row + 1][col] != WALL)
                    if (iMaze[row + 1][col - 1] != WALL)
                    {
                        g.drawArc(x - 8 - 6, y + 8 - 6, 12, 12, 270, 100);
                    } else {
                        g.drawLine(x - 2, y + 8, x - 2, y + 16);
                    }
                else {
                    g.drawLine(x - 2, y + 8, x - 2, y + 17);
                    g.drawLine(x - 2, y + 17, x + 7, y + 17);
                }

                if (iMaze[row - 1][col] != WALL)
                    if (iMaze[row - 1][col - 1] != WALL)
                    {
                        g.drawArc(x - 8 - 6, y + 7 - 6, 12, 12, 0, 100);
                    } else {
                        g.drawLine(x - 2, y, x - 2, y + 7);
                    }
                else {
                    g.drawLine(x - 2, y - 2, x - 2, y + 7);
                    g.drawLine(x - 2, y - 2, x + 7, y - 2);
                }
                break;

            case ctables.RIGHT:
                if (iMaze[row + 1][col] != WALL)
                    if (iMaze[row + 1][col + 1] != WALL)
                    {
                        g.drawArc(x + 16 + 7 - 6, y + 8 - 6, 12, 12, 180, 100);
                    } else {
                        g.drawLine(x + 17, y + 8, x + 17, y + 15);
                    }
                else {
                    g.drawLine(x + 8, y + 17, x + 17, y + 17);
                    g.drawLine(x + 17, y + 8, x + 17, y + 17);
                }
                if (iMaze[row - 1][col] != WALL)
                    if (iMaze[row - 1][col + 1] != WALL)
                    {
                        g.drawArc(x + 16 + 7 - 6, y + 7 - 6, 12, 12, 90, 100);
                    } else {
                        g.drawLine(x + 17, y, x + 17, y + 7);
                    }
                else {
                    g.drawLine(x + 8, y - 2, x + 17, y - 2);
                    g.drawLine(x + 17, y - 2, x + 17, y + 7);
                }
                break;

            case ctables.UP:
                if (iMaze[row][col - 1] != WALL)
                    if (iMaze[row - 1][col - 1] != WALL)
                    {
                        g.drawArc(x + 7 - 6, y - 8 - 6, 12, 12, 180, 100);
                    } else {
                        g.drawLine(x, y - 2, x + 7, y - 2);
                    }
                if (iMaze[row][col + 1] != WALL)
                    if (iMaze[row - 1][col + 1] != WALL)
                    {
                        g.drawArc(x + 8 - 6, y - 8 - 6, 12, 12, 270, 100);
                    } else {
                        g.drawLine(x + 8, y - 2, x + 16, y - 2);
                    }
                break;

            case ctables.DOWN:
                if (iMaze[row][col - 1] != WALL)
                    if (iMaze[row + 1][col - 1] != WALL)
                    {
                        g.drawArc(x + 7 - 6, y + 16 + 7 - 6, 12, 12, 90, 100);
                    } else {
                        g.drawLine(x, y + 17, x + 7, y + 17);
                    }
                if (iMaze[row][col + 1] != WALL)
                    if (iMaze[row + 1][col + 1] != WALL)
                    {
                        g.drawArc(x + 8 - 6, y + 16 + 7 - 6, 12, 12, 0, 100);
                    } else {
                        g.drawLine(x + 8, y + 17, x + 15, y + 17);
                    }
                break;
        }
    }

}


package pacman;

import java.awt.*;

public class cpac {
    // кадры ожидания после съеденной точки
    final int DOT_WAIT = 4;

    int iDotWait;

    // текущая позиция
    int iX, iY;
    // текущее направление
    int iDir;

    Window applet;
    Graphics graphics;

    // изображение пакмана
    Image[][] imagePac;

    cmaze maze;

    cpowerdot powerDot;

    cpac(Window a, Graphics g, cmaze m, cpowerdot p) {
        applet = a;
        graphics = g;
        maze = m;
        powerDot = p;

        // инициализация изображения пакмана
        imagePac = new Image[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                imagePac[i][j] = applet.createImage(18, 18);
                cimage.drawPac(imagePac[i][j], i, j);
            }
    }

    //начальные значения пакмана
    public void start() {
        iX = 10 * 16;
        iY = 10 * 16;
        iDir = 1;
        iDotWait = 0;
    }

    //рисование
    public void draw() {
        maze.DrawDot(iX / 16, iY / 16);
        maze.DrawDot(iX / 16 + (iX % 16 > 0 ? 1 : 0), iY / 16 + (iY % 16 > 0 ? 1 : 0));

        int iImageStep = (iX % 16 + iY % 16) / 2;    // determine shape of PAc
        if (iImageStep < 4)
            iImageStep = 3 - iImageStep;
        else
            iImageStep -= 4;
        graphics.drawImage(imagePac[iDir][iImageStep], iX - 1, iY - 1, applet);
    }

    // возвращает 1 если съел точку
    // возвращает 2 если съел точку-энерджайхер
    public int move(int iNextDir) {
        int eaten = 0;
        if (iNextDir != -1 && iNextDir != iDir)
        // изменить направление
        {
            if (iX % 16 != 0 || iY % 16 != 0) {
                if (iNextDir % 2 == iDir % 2)
                    iDir = iNextDir;
            } else
            {
                if (mazeOK(iX / 16 + ctables.iXDirection[iNextDir],
                        iY / 16 + ctables.iYDirection[iNextDir])) {
                    iDir = iNextDir;
                    iNextDir = -1;
                }
            }
        }
        if (iX % 16 == 0 && iY % 16 == 0) {

            // если пак съел что-то
            switch (maze.iMaze[iY / 16][iX / 16]) {
                case cmaze.DOT:
                    eaten = 1;
                    maze.iMaze[iY / 16][iX / 16] = cmaze.BLANK;    // съел точку
                    maze.iTotalDotcount--; //вычитаем из объещего кол-ва точек
                    iDotWait = DOT_WAIT;
                    break;
                case cmaze.POWER_DOT:
                    eaten = 2;
                    powerDot.eat(iX / 16, iY / 16);
                    maze.iMaze[iY / 16][iX / 16] = cmaze.BLANK;
                    break;
            }

            if (maze.iMaze[iY / 16 + ctables.iYDirection[iDir]]
                    [iX / 16 + ctables.iXDirection[iDir]] == 1) {
                return (eaten);    // not valid move
            }
        }
        if (iDotWait == 0) {
            iX += ctables.iXDirection[iDir];
            iY += ctables.iYDirection[iDir];
        } else iDotWait--;
        return (eaten);
    }

    boolean mazeOK(int iRow, int icol) {
        if ((maze.iMaze[icol][iRow] & (cmaze.WALL | cmaze.DOOR)) == 0)
            return (true);
        return (false);
    }
}










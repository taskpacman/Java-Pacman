package pacman;

/**
 * cpacmove.java
 * вычисляет перемещения пакмана(для призраков
 */
class cpacmove {
    // Вещи, которые влияют на решения:
    // статус призраков
    // позиция призраков
    // направление движения призраков
    // позиция точек
    // позиция энерджайзеров
    // чем ближе призраки, больше веса, чтобы перейти к энерджайзеру

    int iDirScore[];

    int iValid[];

    cpac cPac;
    cghost[] cGhost;
    cmaze cMaze;

    cpacmove(cpac pac, cghost ghost[], cmaze maze) {
        iDirScore = new int[4];

        iValid = new int[4];
        cPac = pac;

        cGhost = new cghost[4];
        for (int i = 0; i < 4; i++)
            cGhost[i] = ghost[i];

        cMaze = maze;
    }

    public int GetNextDir()
            throws Error {
        int i;

        for (i = 0; i < 4; i++)
            iDirScore[i] = 0;

        AddDotScore();

        AddGhostScore();

        AddPowerDotScore();

        // определяет направление на основе очков
        for (i = 0; i < 4; i++)
            iValid[i] = 1;

        int iHigh, iHDir;

        while (true) {
            iHigh = -1000000;
            iHDir = -1;
            for (i = 0; i < 4; i++) {
                if (iValid[i] == 1 && iDirScore[i] > iHigh) {
                    iHDir = i;
                    iHigh = iDirScore[i];
                }
            }

            if (iHDir == -1) {
                throw new Error("cpacmove: can't find a way?");
            }

            if (cPac.iX % 16 == 0 && cPac.iY % 16 == 0) {
                if (cPac.mazeOK(cPac.iX / 16 + ctables.iXDirection[iHDir],
                        cPac.iY / 16 + ctables.iYDirection[iHDir]))
                    return (iHDir);
            } else
                return (iHDir);

            iValid[iHDir] = 0;
        }
    }

    void AddGhostScore() {
        int iXDis, iYDis;    // дистанция
        double iDis;        // дистанция

        int iXFact, iYFact;

        // расчет призраков по одному
        for (int i = 0; i < 4; i++) {
            iXDis = cGhost[i].iX - cPac.iX;
            iYDis = cGhost[i].iY - cPac.iY;

            iDis = Math.sqrt(iXDis * iXDis + iYDis * iYDis);

            if (cGhost[i].iStatus == cGhost[i].BLIND) {


            } else {
                iDis = 18 * 13 / iDis / iDis;
                iXFact = (int) (iDis * iXDis);
                iYFact = (int) (iDis * iYDis);

                if (iXDis >= 0) {
                    iDirScore[ctables.LEFT] += iXFact;
                } else {
                    iDirScore[ctables.RIGHT] += -iXFact;
                }

                if (iYDis >= 0) {
                    iDirScore[ctables.UP] += iYFact;
                } else {
                    iDirScore[ctables.DOWN] += -iYFact;
                }
            }
        }
    }

    void AddDotScore() {
        int i, j;

        int dDis, dShortest;
        int iXDis, iYDis;
        int iX = 0, iY = 0;

        dShortest = 1000;

        // поиск ближайшей точки
        for (i = 0; i < cmaze.HEIGHT; i++)
            for (j = 0; j < cmaze.WIDTH; j++) {
                if (cMaze.iMaze[i][j] == cmaze.DOT) {
                    iXDis = j * 16 - 8 - cPac.iX;
                    iYDis = i * 16 - 8 - cPac.iY;
                    dDis = iXDis * iXDis + iYDis * iYDis;

                    if (dDis < dShortest) {
                        dShortest = dDis;

                        iX = iXDis;
                        iY = iYDis;
                    }
                }
            }


        int iFact = 100000;

        if (iX > 0) {
            iDirScore[ctables.RIGHT] += iFact;
        } else if (iX < 0) {
            iDirScore[ctables.LEFT] += iFact;
        }

        if (iY > 0) {
            iDirScore[ctables.DOWN] += iFact;
        } else if (iY < 0) {
            iDirScore[ctables.UP] += iFact;
        }
    }

    void AddPowerDotScore() {

    }
}

package pacman;

import java.lang.Error;
import java.awt.*;

public class cghost {
    final int IN = 0;
    final int OUT = 1;
    final int BLIND = 2;
    final int EYE = 3;

    final int[] steps = {7, 7, 1, 1};
    final int[] frames = {8, 8, 2, 1};

    final int INIT_BLIND_COUNT = 600;
    int blindCount;

    cspeed speed = new cspeed();

    int iX, iY, iDir, iStatus;
    int iBlink, iBlindCount;

    final int DIR_FACTOR = 2;
    final int POS_FACTOR = 10;

    Window applet;
    Graphics graphics;

    // лабиринт
    cmaze maze;

    // изображение призраков
    Image imageGhost;
    Image imageBlind;
    Image imageEye;

    cghost(Window a, Graphics g, cmaze m, Color color) {
        applet = a;
        graphics = g;
        maze = m;

        imageGhost = applet.createImage(18, 18);
        cimage.drawGhost(imageGhost, 0, color);

        imageBlind = applet.createImage(18, 18);
        cimage.drawGhost(imageBlind, 1, Color.white);

        imageEye = applet.createImage(18, 18);
        cimage.drawGhost(imageEye, 2, Color.lightGray);
    }

    public void start(int initialPosition, int round) {
        if (initialPosition >= 2)
            initialPosition++;
        iX = (8 + initialPosition) * 16;
        iY = 8 * 16;
        iDir = 3;
        iStatus = IN;

        blindCount = INIT_BLIND_COUNT / ((round + 1) / 2);

        speed.start(steps[iStatus], frames[iStatus]);
    }

    public void draw() {
        maze.DrawDot(iX / 16, iY / 16);
        maze.DrawDot(iX / 16 + (iX % 16 > 0 ? 1 : 0), iY / 16 + (iY % 16 > 0 ? 1 : 0));

        if (iStatus == BLIND && iBlink == 1 && iBlindCount % 32 < 16)
            graphics.drawImage(imageGhost, iX - 1, iY - 1, applet);
        else if (iStatus == OUT || iStatus == IN)
            graphics.drawImage(imageGhost, iX - 1, iY - 1, applet);
        else if (iStatus == BLIND)
            graphics.drawImage(imageBlind, iX - 1, iY - 1, applet);
        else
            graphics.drawImage(imageEye, iX - 1, iY - 1, applet);
    }

    public void move(int iPacX, int iPacY, int iPacDir) {
        if (iStatus == BLIND) {
            iBlindCount--;
            if (iBlindCount < blindCount / 3)
                iBlink = 1;
            if (iBlindCount == 0)
                iStatus = OUT;
            if (iBlindCount % 2 == 1)    // уменьньшение скорости в 2 раза у ослепленных призраков
                return;
        }

        if (speed.isMove() == 0)
            // без перемещения
            return;

        if (iX % 16 == 0 && iY % 16 == 0)
        // определить направление
        {
            switch (iStatus) {
                case IN:
                    iDir = INSelect();
                    break;
                case OUT:
                    iDir = OUTSelect(iPacX, iPacY, iPacDir);
                    break;
                case BLIND:
                    iDir = BLINDSelect(iPacX, iPacY, iPacDir);
                    break;
                case EYE:
                    iDir = EYESelect();
            }
        }

        if (iStatus != EYE) {
            iX += ctables.iXDirection[iDir];
            iY += ctables.iYDirection[iDir];
        } else {
            iX += 2 * ctables.iXDirection[iDir];
            iY += 2 * ctables.iYDirection[iDir];
        }

    }

    public int INSelect()
        // считаем доступные направления
            throws Error {
        int iM, i, iRand;
        int iDirTotal = 0;

        for (i = 0; i < 4; i++) {
            iM = maze.iMaze[iY / 16 + ctables.iYDirection[i]]
                    [iX / 16 + ctables.iXDirection[i]];
            if (iM != cmaze.WALL && i != ctables.iBack[iDir]) {
                iDirTotal++;
            }
        }
        // случайный выбор направления
        if (iDirTotal != 0) {
            iRand = cuty.RandSelect(iDirTotal);
            if (iRand >= iDirTotal)
                throw new Error("iRand out of range");
            //				exit(2);
            for (i = 0; i < 4; i++) {
                iM = maze.iMaze[iY / 16 + ctables.iYDirection[i]]
                        [iX / 16 + ctables.iXDirection[i]];
                if (iM != cmaze.WALL && i != ctables.iBack[iDir]) {
                    iRand--;
                    if (iRand < 0)
                    // правильный выбор направления
                    {
                        if (iM == cmaze.DOOR)
                            iStatus = OUT;
                        iDir = i;
                        break;
                    }
                }
            }
        }
        return (iDir);
    }

    public int OUTSelect(int iPacX, int iPacY, int iPacDir)
        // счетчик доступных направлений
            throws Error {
        int iM, i, iRand;
        int iDirTotal = 0;
        int[] iDirCount = new int[4];

        for (i = 0; i < 4; i++) {
            iDirCount[i] = 0;
            iM = maze.iMaze[iY / 16 + ctables.iYDirection[i]]
                    [iX / 16 + ctables.iXDirection[i]];
            if (iM != cmaze.WALL && i != ctables.iBack[iDir] && iM != cmaze.DOOR)
            // делаем дверь недоступной (в доме призраков)
            {
                iDirCount[i]++;
                iDirCount[i] += iDir == iPacDir ?
                        DIR_FACTOR : 0;
                switch (i) {
                    case 0:    // вправо
                        iDirCount[i] += iPacX > iX ? POS_FACTOR : 0;
                        break;
                    case 1:    // вверх
                        iDirCount[i] += iPacY < iY ?
                                POS_FACTOR : 0;
                        break;
                    case 2:    // влево
                        iDirCount[i] += iPacX < iX ?
                                POS_FACTOR : 0;
                        break;
                    case 3:    // вниз
                        iDirCount[i] += iPacY > iY ?
                                POS_FACTOR : 0;
                        break;
                }
                iDirTotal += iDirCount[i];
            }
        }
        // случайный выбор направления
        if (iDirTotal != 0) {
            iRand = cuty.RandSelect(iDirTotal);
            if (iRand >= iDirTotal)
                throw new Error("iRand out of range");
            // exit(2);
            for (i = 0; i < 4; i++) {
                iM = maze.iMaze[iY / 16 + ctables.iYDirection[i]]
                        [iX / 16 + ctables.iXDirection[i]];
                if (iM != cmaze.WALL && i != ctables.iBack[iDir] && iM != cmaze.DOOR) {
                    iRand -= iDirCount[i];
                    if (iRand < 0)
                    // направление вправо
                    {
                        iDir = i;
                        break;
                    }
                }
            }
        } else
            throw new Error("iDirTotal out of range");
        // exit(1);
        return (iDir);
    }

    public void blind() { //меняем режим преследования
        if (iStatus == BLIND || iStatus == OUT) {
            iStatus = BLIND;
            iBlindCount = blindCount;
            iBlink = 0;
            // меняем направления
            if (iX % 16 != 0 || iY % 16 != 0) {
                iDir = ctables.iBack[iDir];
                // особое состояние:
                // когда призрак покидает дом, он не может вернуться
                int iM;
                iM = maze.iMaze[iY / 16 + ctables.iYDirection[iDir]]
                        [iX / 16 + ctables.iXDirection[iDir]];
                if (iM == cmaze.DOOR)
                    iDir = ctables.iBack[iDir];
            }
        }
    }

    public int EYESelect()
        // счетчик доступных направлений
            throws Error {
        int iM, i, iRand;
        int iDirTotal = 0;
        int[] iDirCount = new int[4];

        for (i = 0; i < 4; i++) {
            iDirCount[i] = 0;
            iM = maze.iMaze[iY / 16 + ctables.iYDirection[i]]
                    [iX / 16 + ctables.iXDirection[i]];
            if (iM != cmaze.WALL && i != ctables.iBack[iDir]) {
                iDirCount[i]++;
                switch (i) {
                    case 0:    // вправо
                        iDirCount[i] += 160 > iX ?
                                POS_FACTOR : 0;
                        break;
                    case 1:    // вверх
                        iDirCount[i] += 96 < iY ?
                                POS_FACTOR : 0;
                        break;
                    case 2:    // влево
                        iDirCount[i] += 160 < iX ?
                                POS_FACTOR : 0;
                        break;
                    case 3:    // вниз
                        iDirCount[i] += 96 > iY ?
                                POS_FACTOR : 0;
                        break;
                }
                iDirTotal += iDirCount[i];
            }
        }
        // случайный выбор направления
        if (iDirTotal != 0) {
            iRand = cuty.RandSelect(iDirTotal);
            if (iRand >= iDirTotal)
                throw new Error("RandSelect out of range");
            //				exit(2);
            for (i = 0; i < 4; i++) {
                iM = maze.iMaze[iY / 16 + ctables.iYDirection[i]]
                        [iX / 16 + ctables.iXDirection[i]];
                if (iM != cmaze.WALL && i != ctables.iBack[iDir]) {
                    iRand -= iDirCount[i];
                    if (iRand < 0)
                    // выбор направления вправо
                    {
                        if (iM == cmaze.DOOR)
                            iStatus = IN;
                        iDir = i;
                        break;
                    }
                }
            }
        } else
            throw new Error("iDirTotal out of range");
        return (iDir);
    }

    public int BLINDSelect(int iPacX, int iPacY, int iPacDir)
        // счетчик направлений
            throws Error {
        int iM, i, iRand;
        int iDirTotal = 0;
        int[] iDirCount = new int[4];

        for (i = 0; i < 4; i++) {
            iDirCount[i] = 0;
            iM = maze.iMaze[iY / 16 + ctables.iYDirection[i]][iX / 16 + ctables.iXDirection[i]];
            if (iM != cmaze.WALL && i != ctables.iBack[iDir] && iM != cmaze.DOOR)
            // дверь недоступна для входа
            {
                iDirCount[i]++;
                iDirCount[i] += iDir == iPacDir ?
                        DIR_FACTOR : 0;
                switch (i) {
                    case 0:    // вправо
                        iDirCount[i] += iPacX < iX ?
                                POS_FACTOR : 0;
                        break;
                    case 1:    // вверх
                        iDirCount[i] += iPacY > iY ?
                                POS_FACTOR : 0;
                        break;
                    case 2:    // влево
                        iDirCount[i] += iPacX > iX ?
                                POS_FACTOR : 0;
                        break;
                    case 3:    // вниз
                        iDirCount[i] += iPacY < iY ?
                                POS_FACTOR : 0;
                        break;
                }
                iDirTotal += iDirCount[i];
            }
        }
        // случайный выбор движения
        if (iDirTotal != 0) {
            iRand = cuty.RandSelect(iDirTotal);
            if (iRand >= iDirTotal)
                throw new Error("RandSelect out of range");
            //				exit(2);
            for (i = 0; i < 4; i++) {
                iM = maze.iMaze[iY / 16 + ctables.iYDirection[i]]
                        [iX / 16 + ctables.iXDirection[i]];
                if (iM != cmaze.WALL && i != ctables.iBack[iDir]) {
                    iRand -= iDirCount[i];
                    if (iRand < 0)
                    // правильный выбор
                    {
                        iDir = i;
                        break;
                    }
                }
            }
        } else
            throw new Error("iDirTotal out of range");
        return (iDir);
    }

    // возвращает 1 если поймают пакмана
    // возвращает 2 если пойманы пакманом
    int testCollision(int iPacX, int iPacY) {
        if (iX <= iPacX + 2 && iX >= iPacX - 2
                && iY <= iPacY + 2 && iY >= iPacY - 2) {
            switch (iStatus) {
                case OUT:
                    return (1);
                case BLIND:
                    iStatus = EYE;
                    iX = iX / 4 * 4;
                    iY = iY / 4 * 4;
                    return (2);
            }
        }
        return (0);
    }
}



package pacman;

import java.awt.*;
import java.awt.event.*;

//main class
public class cpcman extends Frame
        implements Runnable, KeyListener, ActionListener, WindowListener {
    private static final long serialVersionUID = 3582431359568375379L;
    // таймер
    Thread timer;
    int timerPeriod = 12;  // в милисекундах
    int signalMove = 0;

    // для графики
    final int canvasWidth = 368;
    final int canvasHeight = 288 + 1;

    // рисование канвы
    int topOffset;
    int leftOffset;

    // размер лабиринта
    final int iMazeX = 16;
    final int iMazeY = 16;

    Image offScreen;
    Graphics offScreenG;

    // объекты
    cmaze maze;
    cpac pac;
    cpowerdot powerDot;
    cghost[] ghosts;

    // счетчик жизней пака
    final int PAcLIVE = 3;
    int pacRemain;
    int changePacRemain;

    // очки
    int score;
    int hiScore;
    int scoreGhost;    // очки за съедение призрака
    int changeScore;    // меняет очки на экране
    int changeHiScore;  // меняет hiscore на экране

    // изображение очков
    Image imgScore;
    Graphics imgScoreG;
    Image imgHiScore;
    Graphics imgHiScoreG;

    // статус игры
    final int INITIMAGE = 100;
    final int STARTWAIT = 0;  //задержка до старта
    final int RUNNING = 1;
    final int DEADWAIT = 2;   // задержка после смерти
    final int SUSPENDED = 3;
    int gameState;

    final int WAITCOUNT = 100;
    int wait;

    // раунды
    int round;
    boolean newMaze;

    // менюшки
    MenuBar menuBar;
    Menu help;
    MenuItem about;

    // the direction specified by key
    int pacKeyDir;
    int key = 0;
    final int NONE = 0;
    final int SUSPEND = 1;  // стоп/старт
    final int BOSS = 2;      // босс


    public cpcman() {
        super("PACMAN");

        hiScore = 0;

        gameState = INITIMAGE;

        initGUI();

        addWindowListener(this);

        addKeyListener(this);

        about.addActionListener(this);

        setSize(canvasWidth, canvasHeight);

        show();
    }

    void initGUI() {
        menuBar = new MenuBar();
        help = new Menu("HELP");
        about = new MenuItem("About");

        help.add(about);
        menuBar.add(help);

        setMenuBar(menuBar);

        addNotify();

    }

    public void initImages() {
        //создаие канвы
        offScreen = createImage(cmaze.iWidth, cmaze.iHeight);
        if (offScreen == null)
            System.out.println("createImage failed");
        offScreenG = offScreen.getGraphics();

        // инициализация лабиринта
        maze = new cmaze(this, offScreenG);

        // инициализация призраков
        // 4 призрака
        ghosts = new cghost[4];
        for (int i = 0; i < 4; i++) {
            Color color;
            if (i == 0)
                color = Color.red;
            else if (i == 1)
                color = Color.blue;
            else if (i == 2)
                color = Color.white;
            else
                color = Color.orange;
            ghosts[i] = new cghost(this, offScreenG, maze, color);
        }

        // инициализация энерджайзера
        powerDot = new cpowerdot(this, offScreenG, ghosts);

        // инициализация пакмана
        pac = new cpac(this, offScreenG, maze, powerDot);

        // инициалиалазация очков
        imgScore = createImage(150, 16);
        imgScoreG = imgScore.getGraphics();
        imgHiScore = createImage(150, 16);
        imgHiScoreG = imgHiScore.getGraphics();

        imgHiScoreG.setColor(Color.black);
        imgHiScoreG.fillRect(0, 0, 150, 16);
        imgHiScoreG.setColor(Color.red);
        imgHiScoreG.setFont(new Font("Helvetica", Font.BOLD, 12));
        imgHiScoreG.drawString("HI SCORE", 0, 14);

        imgScoreG.setColor(Color.black);
        imgScoreG.fillRect(0, 0, 150, 16);
        imgScoreG.setColor(Color.green);
        imgScoreG.setFont(new Font("Helvetica", Font.BOLD, 12));
        imgScoreG.drawString("SCORE", 0, 14);
    }

    void startTimer() {
        // запуск таймера
        timer = new Thread(this);
        timer.start();
    }

    //старт игры
    void startGame() {
        pacRemain = PAcLIVE;
        changePacRemain = 1;

        score = 0;
        changeScore = 1;

        newMaze = true;

        round = 1;

        startRound();
    }

    void startRound() {
        if (newMaze == true) {
            maze.start();
            powerDot.start();
            newMaze = false;
        }

        maze.draw();    // рисование лабиринта

        pac.start();
        pacKeyDir = ctables.DOWN;
        for (int i = 0; i < 4; i++)
            ghosts[i].start(i, round);

        gameState = STARTWAIT;
        wait = WAITCOUNT;
    }

    ///////////////////////////////////////////
    // рисование всего
    ///////////////////////////////////////////
    public void paint(Graphics g) {
        if (gameState == INITIMAGE) {
            initImages();
            Insets insets = getInsets();

            topOffset = insets.top;
            leftOffset = insets.left;

            setSize(canvasWidth + insets.left + insets.right,
                    canvasHeight + insets.top + insets.bottom);

            setResizable(false);

            startGame();
            startTimer();

        }

        g.setColor(Color.black);
        g.fillRect(leftOffset, topOffset, canvasWidth, canvasHeight);

        changeScore = 1;
        changeHiScore = 1;
        changePacRemain = 1;

        paintUpdate(g);
    }

    void paintUpdate(Graphics g) {
        powerDot.draw();

        for (int i = 0; i < 4; i++)
            ghosts[i].draw();

        pac.draw();
        g.drawImage(offScreen,
                iMazeX + leftOffset, iMazeY + topOffset, this);

        // инфа на экране
        if (changeHiScore == 1) {
            imgHiScoreG.setColor(Color.black);
            imgHiScoreG.fillRect(70, 0, 80, 16);
            imgHiScoreG.setColor(Color.red);
            imgHiScoreG.drawString(Integer.toString(hiScore), 70, 14);
            g.drawImage(imgHiScore,
                    8 + leftOffset, 0 + topOffset, this);

            changeHiScore = 0;
        }

        if (changeScore == 1) {
            imgScoreG.setColor(Color.black);
            imgScoreG.fillRect(70, 0, 80, 16);
            imgScoreG.setColor(Color.green);
            imgScoreG.drawString(Integer.toString(score), 70, 14);
            g.drawImage(imgScore,
                    158 + leftOffset, 0 + topOffset, this);

            changeScore = 0;
        }

        // обновление  пака
        if (changePacRemain == 1) {
            int i;
            for (i = 1; i < pacRemain; i++) {
                g.drawImage(pac.imagePac[0][0],
                        16 * i + leftOffset,
                        canvasHeight - 18 + topOffset, this);
            }
            g.drawImage(powerDot.imageBlank,
                    16 * i + leftOffset,
                    canvasHeight - 17 + topOffset, this);

            changePacRemain = 0;
        }
    }

    ////////////////////////////////////////////////////////////
    // контроль движений
    // выполняется в фоне
    ////////////////////////////////////////////////////////////
    void move() {
        int k;

        int oldScore = score;

        for (int i = 0; i < 4; i++)
            ghosts[i].move(pac.iX, pac.iY, pac.iDir);

        k = pac.move(pacKeyDir);

        if (k == 1)    // съел точку
        {
            changeScore = 1;
            score += 10 * ((round + 1) / 2);
        } else if (k == 2)    // съел энерджайзер
        {
            scoreGhost = 200;
        }

        if (maze.iTotalDotcount == 0) {
            gameState = DEADWAIT;
            wait = WAITCOUNT;
            newMaze = true;
            round++;
            return;
        }

        for (int i = 0; i < 4; i++) {
            k = ghosts[i].testCollision(pac.iX, pac.iY);
            if (k == 1)    // пакмана съели
            {
                pacRemain--;
                changePacRemain = 1;
                gameState = DEADWAIT;    // оставка игры
                wait = WAITCOUNT;
                return;
            } else if (k == 2)    // съеден паком
            {
                score += scoreGhost * ((round + 1) / 2);
                changeScore = 1;
                scoreGhost *= 2;
            }
        }

        if (score > hiScore) {
            hiScore = score;
            changeHiScore = 1;
        }

        if (changeScore == 1) {
            if (score / 10000 - oldScore / 10000 > 0) {
                pacRemain++;            //бонус
                changePacRemain = 1;
            }
        }
    }


    public void update(Graphics g) {
        if (gameState == INITIMAGE)
            return;

        if (signalMove != 0) {
            signalMove = 0;

            if (wait != 0) {
                wait--;
                return;
            }

            switch (gameState) {
                case STARTWAIT:
                    if (pacKeyDir == ctables.UP)    // кнопка начала игры
                        gameState = RUNNING;
                    else
                        return;
                    break;
                case RUNNING:
                    if (key == SUSPEND)
                        gameState = SUSPENDED;
                    else
                        move();
                    break;
                case DEADWAIT:
                    if (pacRemain > 0)
                        startRound();
                    else
                        startGame();
                    gameState = STARTWAIT;
                    wait = WAITCOUNT;
                    pacKeyDir = ctables.DOWN;
                    break;
                case SUSPENDED:
                    if (key == SUSPEND)
                        gameState = RUNNING;
                    break;
            }
            key = NONE;
        }

        paintUpdate(g);
    }

    ///////////////////////////////////////
    // обработка кнопок с клавы
    ///////////////////////////////////////
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_L:
                pacKeyDir = ctables.RIGHT;
                // e.consume();
                break;
            case KeyEvent.VK_UP:
                pacKeyDir = ctables.UP;
                // e.consume();
                break;
            case KeyEvent.VK_LEFT:
                pacKeyDir = ctables.LEFT;
                // e.consume();
                break;
            case KeyEvent.VK_DOWN:
                pacKeyDir = ctables.DOWN;
                // e.consume();
                break;
            case KeyEvent.VK_S:
                key = SUSPEND;
                break;
            case KeyEvent.VK_B:
                key = BOSS;
                break;
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    /////////////////////////////////////////////////
    // меню about
    /////////////////////////////////////////////////
    public void actionPerformed(ActionEvent e) {
        if (gameState == RUNNING)
            key = SUSPEND;
        new cabout(this);
        // e.consume();
    }


    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        dispose();
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    /////////////////////////////////////////////////
    // таймер
    /////////////////////////////////////////////////
    public void run() {
        while (true) {
            try {
                Thread.sleep(timerPeriod);
            } catch (InterruptedException e) {
                return;
            }

            signalMove++;
            repaint();
        }
    }

    boolean finalized = false;

    public void dispose() {
        timer.interrupt();

        offScreenG.dispose();
        offScreenG = null;

        maze = null;
        pac = null;
        powerDot = null;
        for (int i = 0; i < 4; i++)
            ghosts[i] = null;
        ghosts = null;

        // очки
        imgScore = null;
        imgHiScore = null;
        imgScoreG.dispose();
        imgScoreG = null;
        imgHiScoreG.dispose();
        imgHiScoreG = null;

        // GUIs
        menuBar = null;
        help = null;
        about = null;

        super.dispose();

        finalized = true;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }
}
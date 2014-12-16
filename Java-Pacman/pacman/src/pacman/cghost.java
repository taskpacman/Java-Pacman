
package pacman;

import java.lang.Error;
import java.awt.*;

public class cghost
{
	final int IN=0;
	final int OUT=1;
	final int BLIND=2;
	final int EYE=3;

	final int[] steps=	{7, 7, 1, 1};
	final int[] frames=	{8, 8, 2, 1};

	final int INIT_BLIND_COUNT=600;
	int blindCount;

	int iX, iY, iDir, iStatus;
	int iBlink, iBlindCount;

	final int DIR_FACTOR=2;
	final int POS_FACTOR=10;



	public void start(int initialPosition, int round)
	{

	}

	public void draw()
	{

	}  

	public void move(int iPacX, int iPacY, int iPacDir)
	{

	}

	public void blind()
	{

	}

	// 1 если поймали пакмана
	// 2 если поймал пакман
	int testCollision(int iPacX, int iPacY)
	{
		if (iX<=iPacX+2 && iX>=iPacX-2
				&& iY<=iPacY+2 && iY>=iPacY-2)
		{
			switch (iStatus)
			{
			case OUT:
				return(1);
			case BLIND:
				iStatus=EYE;
				iX=iX/4*4;
				iY=iY/4*4;
				return(2);
			}	
		}
		return(0);
	}
}



package cn.hdce.mingtfarc.Sarah.api;

import java.util.Random;

public class MainAPI implements RandomNums,PluginHooks{
	
	//public static int getRandomNum(int num)
	//{
		//getRandomNums(num);
		//return 0;
	//}
	public int getRandomNum(int num)
	{
		Random r=new Random();
		return r.nextInt(num);
	}
	public int getRandomNumWithRange(int min,int max)
	{
		Random r=new Random();
		return r.nextInt(max-min)+min;
	}
	public DeathPunishHook getDeathPunishHook()
	{
		DeathPunishHook y = null;
		return y;
	}
}

package cn.hdce.mingtfarc.Sarah;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import cn.hdce.mingtfarc.Sarah.api.Help;
import cn.hdce.mingtfarc.Sarah.api.MainAPI;
import cn.hdce.mingtfarc.Sarah.api.RandomNums;

public class Bounties extends JavaPlugin{
	public static final String pre=ChatColor.GOLD+"§l[赏金系统]§r"+ChatColor.GREEN;
	public static final String cmdName="/bounties";
	public static Economy economy = null;
	public final String deathFilePath="./plugins/Bounties/death.yml";	
	public List al;
	public File deathFile;
	public FileConfiguration deathConfig;
	@Override
	public void onEnable()
	{
		getLogger().info("大明Tfarc的赏金插件 已经启用");
		this.getServer().getPluginManager().registerEvents(new Eve(),this);
		this.getCommand("bounties").setExecutor(new Cmd());
		this.deathFile=new File(this.deathFilePath);
		this.deathConfig=YamlConfiguration.loadConfiguration(deathFile);
		if(!this.deathFile.exists())
		{
			this.saveResource("death.yml", true);
		}
	}
	
	public class Cmd implements CommandExecutor,Help
	{
		

		private boolean setupEconomy()
	    {
	        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) {
	            economy = economyProvider.getProvider();
	        }

	        return (economy != null);
	    }
		@Override
		public boolean onCommand(CommandSender cs, Command cmd, String label,String[] args) {
			if(args.length==0)
			{
				help(cs);
			}
			else if(args[0].equalsIgnoreCase("restart"))
			{
				
				if(cs.isOp())
				{
				cs.sendMessage(pre+"正在重启...");
				getServer().getPluginManager().disablePlugin(new Bounties());
				getServer().getPluginManager().enablePlugin(new Bounties());
				cs.sendMessage(pre+"重启完成~");
				return true;
				}
				else 
					return false;
			}
			
			return true;
		}
		public void help(CommandSender cs)
		{
			String[] msg=
			{
					pre+"======赏金系统帮助======  "+ChatColor.GRAY+"by mingtfarc",
					pre+"<>  -  必填      []  -  选填",
					pre+"§c注：玩家死亡后系统会存储60节操，达到998节操时就会有5%几率被添加到赏金系统",
					pre+cmdName+" add <玩家名> [最小奖励金钱] [最大奖励金钱]- 增加一个玩家到赏金系统中",
					pre+cmdName+" remove 玩家名 - 从赏金系统中删除一个玩家",
					pre+cmdName+" restart - 重启赏金系统"
					
			};
			
			cs.sendMessage(msg);
		}
	}
	public class Eve implements Listener
	{
		public String name;
		public List<String> alp= deathConfig.getStringList("players");
		public int money2;
		@EventHandler
		public void death(PlayerDeathEvent e)
		{
			
			RandomNums rn=new MainAPI();
			Player p=e.getEntity();
			Player killer=p.getKiller();
			
			money2=rn.getRandomNumWithRange(1, 2000);	
			name=p.getName();
			
			int money=deathConfig.getInt("money", 0);//总计的钱数
			for(int x=0;x<alp.toArray().length;x++)//找到一个就循环一次
			{
				String eachPlayerInList=(String)alp.toArray()[x];//每个玩家
				if(name.contains(eachPlayerInList))
				{
					if(killer==null)
						return;
					economy.bankDeposit(killer.getName(), money2);
					Bukkit.broadcastMessage(pre+killer.getName()+"击杀了"+name+"，会获得"+money2);
					alp.remove(name);
					try {
						deathConfig.save(deathFile);
					} catch (IOException e1) {							
						e1.printStackTrace();
					}
				}
			}
			
			if(money>=998)//如果缓存金钱大于等于998
			{
				if(rn.getRandomNum(21)==1)//几率5%(1/20)
				{
							
					boolean inList=alp.contains(name);//死的玩家是否在列表中
					if(!inList)
					{
						
						alp.add(name);
						deathConfig.set("players", alp);
						try {
							deathConfig.save(deathFile);
						} catch (IOException e1) {							
							e1.printStackTrace();
						}
						Bukkit.broadcastMessage(pre+name+" 已经被添加到赏金名单中..");
						Bukkit.broadcastMessage(pre+" 杀掉可以获得 "+money2+"个节操");
						Bukkit.broadcastMessage(pre+" 限时 5 分钟");
						new BukkitRunnable()
						{
							public void run()
							{
								alp.remove(name);
								Bukkit.broadcastMessage(pre+"时间到了~");
								deathConfig.set("players", alp);
								try {
									deathConfig.save(deathFile);
								} catch (IOException e1) {							
									e1.printStackTrace();
								}
							}
						}.runTaskLater(new Bounties(), 5*60*1000L);//5分钟后执行run()
					}
				}
				else return;
			}
			else
			{
				deathConfig.set("money", money+60);//不大于998 +60
				try {
					deathConfig.save(deathFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	
	}

}

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
	public static final String pre=ChatColor.GOLD+"��l[�ͽ�ϵͳ]��r"+ChatColor.GREEN;
	public static final String cmdName="/bounties";
	public static Economy economy = null;
	public final String deathFilePath="./plugins/Bounties/death.yml";	
	public List al;
	public File deathFile;
	public FileConfiguration deathConfig;
	@Override
	public void onEnable()
	{
		getLogger().info("����Tfarc���ͽ��� �Ѿ�����");
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
				cs.sendMessage(pre+"��������...");
				getServer().getPluginManager().disablePlugin(new Bounties());
				getServer().getPluginManager().enablePlugin(new Bounties());
				cs.sendMessage(pre+"�������~");
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
					pre+"======�ͽ�ϵͳ����======  "+ChatColor.GRAY+"by mingtfarc",
					pre+"<>  -  ����      []  -  ѡ��",
					pre+"��cע�����������ϵͳ��洢60�ڲ٣��ﵽ998�ڲ�ʱ�ͻ���5%���ʱ���ӵ��ͽ�ϵͳ",
					pre+cmdName+" add <�����> [��С������Ǯ] [�������Ǯ]- ����һ����ҵ��ͽ�ϵͳ��",
					pre+cmdName+" remove ����� - ���ͽ�ϵͳ��ɾ��һ�����",
					pre+cmdName+" restart - �����ͽ�ϵͳ"
					
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
			
			int money=deathConfig.getInt("money", 0);//�ܼƵ�Ǯ��
			for(int x=0;x<alp.toArray().length;x++)//�ҵ�һ����ѭ��һ��
			{
				String eachPlayerInList=(String)alp.toArray()[x];//ÿ�����
				if(name.contains(eachPlayerInList))
				{
					if(killer==null)
						return;
					economy.bankDeposit(killer.getName(), money2);
					Bukkit.broadcastMessage(pre+killer.getName()+"��ɱ��"+name+"������"+money2);
					alp.remove(name);
					try {
						deathConfig.save(deathFile);
					} catch (IOException e1) {							
						e1.printStackTrace();
					}
				}
			}
			
			if(money>=998)//��������Ǯ���ڵ���998
			{
				if(rn.getRandomNum(21)==1)//����5%(1/20)
				{
							
					boolean inList=alp.contains(name);//��������Ƿ����б���
					if(!inList)
					{
						
						alp.add(name);
						deathConfig.set("players", alp);
						try {
							deathConfig.save(deathFile);
						} catch (IOException e1) {							
							e1.printStackTrace();
						}
						Bukkit.broadcastMessage(pre+name+" �Ѿ�����ӵ��ͽ�������..");
						Bukkit.broadcastMessage(pre+" ɱ�����Ի�� "+money2+"���ڲ�");
						Bukkit.broadcastMessage(pre+" ��ʱ 5 ����");
						new BukkitRunnable()
						{
							public void run()
							{
								alp.remove(name);
								Bukkit.broadcastMessage(pre+"ʱ�䵽��~");
								deathConfig.set("players", alp);
								try {
									deathConfig.save(deathFile);
								} catch (IOException e1) {							
									e1.printStackTrace();
								}
							}
						}.runTaskLater(new Bounties(), 5*60*1000L);//5���Ӻ�ִ��run()
					}
				}
				else return;
			}
			else
			{
				deathConfig.set("money", money+60);//������998 +60
				try {
					deathConfig.save(deathFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	
	}

}

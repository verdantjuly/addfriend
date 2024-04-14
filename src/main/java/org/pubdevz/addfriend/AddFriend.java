package org.pubdevz.addfriend;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class AddFriend extends JavaPlugin implements Listener {

    public String playerName;
    @Override
    public void onEnable() {

        // 플러그인 폴더 내에 'data' 폴더를 참조하는 File 객체 생성
        File dataFolder = new File(getDataFolder(), "friendList");

        // 'data' 폴더가 존재하지 않는 경우
        if (!dataFolder.exists()) {
            // 'data' 폴더를 생성
            boolean result = dataFolder.mkdirs();
            if (result) {
                getLogger().info("friendList 폴더가 성공적으로 생성되었습니다.");
            } else {
                getLogger().severe("friendList 폴더 생성에 실패하였습니다.");
            }
        }
        this.getCommand("addfriend").setExecutor(new AddFriendExecutor());
        this.getCommand("listfriend").setExecutor(new AddFriendExecutor());
        this.getCommand("removefriend").setExecutor(new AddFriendExecutor());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerName = player.getName();
    }

    public String getPlayerName(){
        return playerName;
    }

}



